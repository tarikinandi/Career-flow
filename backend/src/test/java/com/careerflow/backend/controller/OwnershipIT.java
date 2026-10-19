package com.careerflow.backend.controller;

import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.DeckRepository;
import com.careerflow.backend.repository.FlashcardRepository;
import com.careerflow.backend.repository.InterviewRepository;
import com.careerflow.backend.repository.UserRepository;
import com.careerflow.backend.security.JwtService;
import com.careerflow.backend.support.AbstractIntegrationTest;
import com.careerflow.backend.support.TestApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Two users: A owns data, B tries every by-id operation on it.
 * Every attempt must answer 404 (same as a missing id, so existence is not leaked)
 * and must not change A's data.
 */
class OwnershipIT extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private InterviewRepository interviewRepository;
    @Autowired private DeckRepository deckRepository;
    @Autowired private FlashcardRepository flashcardRepository;

    private TestApi api;
    private String tokenA;
    private String tokenB;
    private long appId;
    private long interviewId;
    private long deckId;
    private long cardId;

    @BeforeEach
    void setUp() throws Exception {
        api = new TestApi(mockMvc, objectMapper, userRepository, passwordEncoder, jwtService);
        tokenA = api.newUserToken("a@example.test");
        tokenB = api.newUserToken("b@example.test");

        appId = api.createAndGetId("/api/v1/applications", tokenA, TestApi.application("Acme"));
        interviewId = api.createAndGetId("/api/v1/applications/" + appId + "/interviews", tokenA,
                Map.of("round", "RECRUITER_SCREEN"));
        deckId = api.createAndGetId("/api/v1/decks", tokenA, Map.of("title", "Java"));
        cardId = api.createAndGetId("/api/v1/flashcards", tokenA,
                Map.of("deckId", deckId, "frontContent", "Q", "backContent", "A"));
    }

    private void assertNotFoundForB(String method, String url, Object body) throws Exception {
        api.call(method, url, tokenB, body)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void ownerStillHasFullAccess() throws Exception {
        api.call("GET", "/api/v1/applications/" + appId, tokenA, null).andExpect(status().isOk());
        api.call("PUT", "/api/v1/applications/" + appId, tokenA, TestApi.application("Acme 2")).andExpect(status().isOk());
        api.call("GET", "/api/v1/applications/" + appId + "/interviews", tokenA, null).andExpect(status().isOk());
        api.call("GET", "/api/v1/decks/" + deckId + "/cards", tokenA, null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1));
        api.call("GET", "/api/v1/decks/" + deckId + "/due-cards", tokenA, null).andExpect(status().isOk());
        api.call("POST", "/api/v1/flashcards/" + cardId + "/study?quality=4", tokenA, null).andExpect(status().isOk());
        api.call("DELETE", "/api/v1/interviews/" + interviewId, tokenA, null).andExpect(status().isOk());
        api.call("DELETE", "/api/v1/flashcards/" + cardId, tokenA, null).andExpect(status().isOk());
        api.call("DELETE", "/api/v1/decks/" + deckId, tokenA, null).andExpect(status().isOk());
        api.call("DELETE", "/api/v1/applications/" + appId, tokenA, null).andExpect(status().isOk());
    }

    @Test
    void otherUserCannotGetApplication() throws Exception {
        assertNotFoundForB("GET", "/api/v1/applications/" + appId, null);
    }

    @Test
    void otherUserCannotUpdateApplication() throws Exception {
        assertNotFoundForB("PUT", "/api/v1/applications/" + appId, TestApi.application("Hijacked"));
        assertThat(applicationRepository.findById(appId).orElseThrow().getCompanyName()).isEqualTo("Acme");
    }

    @Test
    void otherUserCannotDeleteApplication() throws Exception {
        assertNotFoundForB("DELETE", "/api/v1/applications/" + appId, null);
        assertThat(applicationRepository.existsById(appId)).isTrue();
    }

    @Test
    void otherUserCannotAddInterviewToApplication() throws Exception {
        assertNotFoundForB("POST", "/api/v1/applications/" + appId + "/interviews", Map.of("round", "TECHNICAL"));
        assertThat(interviewRepository.findAllByApplicationId(appId)).hasSize(1);
    }

    @Test
    void otherUserCannotListInterviewsOfApplication() throws Exception {
        assertNotFoundForB("GET", "/api/v1/applications/" + appId + "/interviews", null);
    }

    @Test
    void otherUserCannotDeleteInterview() throws Exception {
        assertNotFoundForB("DELETE", "/api/v1/interviews/" + interviewId, null);
        assertThat(interviewRepository.existsById(interviewId)).isTrue();
    }

    @Test
    void otherUserCannotDeleteDeck() throws Exception {
        assertNotFoundForB("DELETE", "/api/v1/decks/" + deckId, null);
        assertThat(deckRepository.existsById(deckId)).isTrue();
    }

    @Test
    void otherUserCannotCreateCardInDeck() throws Exception {
        assertNotFoundForB("POST", "/api/v1/flashcards",
                Map.of("deckId", deckId, "frontContent", "Injected", "backContent", "Injected"));
        assertThat(flashcardRepository.findAllByDeckId(deckId)).hasSize(1);
    }

    @Test
    void otherUserCannotListCardsOfDeck() throws Exception {
        assertNotFoundForB("GET", "/api/v1/decks/" + deckId + "/cards", null);
    }

    @Test
    void otherUserCannotListDueCardsOfDeck() throws Exception {
        assertNotFoundForB("GET", "/api/v1/decks/" + deckId + "/due-cards", null);
    }

    @Test
    void otherUserCannotStudyCard() throws Exception {
        assertNotFoundForB("POST", "/api/v1/flashcards/" + cardId + "/study?quality=5", null);
        assertThat(flashcardRepository.findById(cardId).orElseThrow().getRepetitions()).isZero();
    }

    @Test
    void otherUserCannotDeleteCard() throws Exception {
        assertNotFoundForB("DELETE", "/api/v1/flashcards/" + cardId, null);
        assertThat(flashcardRepository.existsById(cardId)).isTrue();
    }

    @Test
    void foreignAndMissingIdsAreIndistinguishable() throws Exception {
        long missingId = appId + 1000;
        String foreign = api.call("GET", "/api/v1/applications/" + appId, tokenB, null)
                .andReturn().getResponse().getContentAsString();
        String missing = api.call("GET", "/api/v1/applications/" + missingId, tokenB, null)
                .andReturn().getResponse().getContentAsString();
        String foreignMsg = objectMapper.readTree(foreign).path("message").asText();
        String missingMsg = objectMapper.readTree(missing).path("message").asText();
        // The message embeds the requested id; compare with the id normalised away.
        assertThat(foreignMsg.replace(String.valueOf(appId), "ID"))
                .isEqualTo(missingMsg.replace(String.valueOf(missingId), "ID"));
    }

    @Test
    void listEndpointsOnlyShowOwnData() throws Exception {
        api.call("GET", "/api/v1/applications", tokenB, null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.content.length()").value(0));
        api.call("GET", "/api/v1/decks", tokenB, null)
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.content.length()").value(0));
    }
}
