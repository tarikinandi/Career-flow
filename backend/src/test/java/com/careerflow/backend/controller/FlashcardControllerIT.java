package com.careerflow.backend.controller;

import com.careerflow.backend.dto.request.DeckRequest;
import com.careerflow.backend.dto.request.FlashcardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Test
    void shouldStudyFlashcardFlow() throws Exception {
        // 1. Deste Oluştur
        DeckRequest deckReq = new DeckRequest();
        deckReq.setTitle("Java Core");

        mockMvc.perform(post("/api/v1/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deckReq)))
                .andExpect(status().isCreated());

        FlashcardRequest cardReq = new FlashcardRequest();
        cardReq.setDeckId(1L);
        cardReq.setFrontContent("Question?");
        cardReq.setBackContent("Answer.");

        mockMvc.perform(post("/api/v1/flashcards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/flashcards/1/study")
                        .param("quality", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.repetitions").value(1));
    }
}