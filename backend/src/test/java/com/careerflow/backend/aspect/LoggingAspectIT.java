package com.careerflow.backend.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.careerflow.backend.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoggingAspectIT extends AbstractIntegrationTest {

    private static final String PASSWORD = "S3cret-Pa55word-Marker";

    @Autowired private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> appender;
    private Logger root;
    private Level previousRootLevel;

    @BeforeEach
    void attachAppender() {
        root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        previousRootLevel = root.getLevel();
        // Most verbose level: if anything would log the password at any level, we see it.
        root.setLevel(Level.TRACE);
        appender = new ListAppender<>();
        appender.start();
        root.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        root.detachAppender(appender);
        root.setLevel(previousRootLevel);
    }

    private List<String> captured() {
        return appender.list.stream().map(e -> e.getLevel() + " " + e.getFormattedMessage()).toList();
    }

    private static String registerBody(String email) {
        return "{\"firstName\":\"Log\",\"lastName\":\"Test\",\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}";
    }

    @Test
    void registerAndLoginDoNotLogThePassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("log@example.test")))
                .andExpect(status().isOk());
        String loginBody = "{\"email\":\"log@example.test\",\"password\":\"" + PASSWORD + "\"}";
        String response = mockMvc.perform(post("/api/v1/auth").contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(appender.list).isNotEmpty();
        assertThat(captured()).noneMatch(line -> line.contains(PASSWORD));
        // The JWT returned by login must not be logged either.
        String jwt = response.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        assertThat(jwt).isNotEmpty().isNotEqualTo(response);
        assertThat(captured()).noneMatch(line -> line.contains(jwt));
    }

    @Test
    void failedLoginDoesNotLogThePassword() throws Exception {
        String loginBody = "{\"email\":\"nobody@example.test\",\"password\":\"" + PASSWORD + "\"}";
        mockMvc.perform(post("/api/v1/auth").contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isUnauthorized());

        assertThat(captured()).noneMatch(line -> line.contains(PASSWORD));
    }

    @Test
    void aspectStillLogsMethodNames() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody("log2@example.test")))
                .andExpect(status().isOk());

        assertThat(captured()).anyMatch(l -> l.contains("AuthenticationServiceImpl.register()"));
    }
}
