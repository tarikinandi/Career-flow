package com.careerflow.backend.controller;

import com.careerflow.backend.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthAndErrorHandlingIT extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;

    private void register(String json, int expectedStatus) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void registerRejectsShortPasswordAndBadEmail() throws Exception {
        register("{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"a@example.test\",\"password\":\"short\"}", 400);
        register("{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"not-an-email\",\"password\":\"long-enough-pw\"}", 400);
        register("{\"firstName\":\"\",\"lastName\":\"B\",\"email\":\"a@example.test\",\"password\":\"long-enough-pw\"}", 400);
    }

    @Test
    void duplicateEmailIsAConflict() throws Exception {
        String json = "{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"dup@example.test\",\"password\":\"long-enough-pw\"}";
        register(json, 200);
        register(json, 409);
    }

    @Test
    void protectedEndpointWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/applications")).andExpect(status().is4xxClientError());
    }

    @Test
    void malformedOrForgedTokenIsRejectedNotAServerError() throws Exception {
        mockMvc.perform(get("/api/v1/applications").header(HttpHeaders.AUTHORIZATION, "Bearer not.a.jwt"))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(get("/api/v1/applications").header(HttpHeaders.AUTHORIZATION,
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGV4YW1wbGUudGVzdCJ9.AAAA"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void malformedJsonGivesGenericBadRequestWithoutParserDetails() throws Exception {
        mockMvc.perform(post("/api/v1/auth").contentType(MediaType.APPLICATION_JSON).content("{\"email\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(content().string(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("at com."))));
    }
}
