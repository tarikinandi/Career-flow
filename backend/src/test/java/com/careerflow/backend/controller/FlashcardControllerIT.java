package com.careerflow.backend.controller;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.request.FlashcardRequest;
import com.careerflow.backend.model.User;
import com.careerflow.backend.model.enums.Role;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class FlashcardControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    private String jwtToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .firstName("Flash")
                .lastName("Test")
                .email("flash.test@integration.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        jwtToken = "Bearer " + jwtService.generateToken(user);
    }

    @Test
    void shouldStudyFlashcardFlow() throws Exception {
        DeckRequest deckReq = new DeckRequest();
        deckReq.setTitle("Java Core");

        MvcResult deckResult = mockMvc.perform(post("/api/v1/decks")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken) // Token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deckReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String deckResponseStr = deckResult.getResponse().getContentAsString();
        JsonNode deckRoot = objectMapper.readTree(deckResponseStr);
        Long deckId = deckRoot.path("data").path("id").asLong();

        FlashcardRequest cardReq = new FlashcardRequest();
        cardReq.setDeckId(deckId);
        cardReq.setFrontContent("Question?");
        cardReq.setBackContent("Answer.");

        MvcResult cardResult = mockMvc.perform(post("/api/v1/flashcards")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken) // Token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String cardResponseStr = cardResult.getResponse().getContentAsString();
        JsonNode cardRoot = objectMapper.readTree(cardResponseStr);
        Long cardId = cardRoot.path("data").path("id").asLong();

        mockMvc.perform(post("/api/v1/flashcards/" + cardId + "/study")
                        .header(HttpHeaders.AUTHORIZATION, jwtToken) // Token
                        .param("quality", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.repetitions").value(1));
    }
}