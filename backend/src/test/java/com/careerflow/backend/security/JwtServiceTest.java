package com.careerflow.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static JwtService withSecret(String secret) {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secretKey", secret);
        return service;
    }

    @Test
    void acceptsA256BitKey() {
        String key = Base64.getEncoder().encodeToString(new byte[32]);
        assertThatCode(() -> withSecret(key).validateSecret()).doesNotThrowAnyException();
    }

    @Test
    void rejectsAKeyShorterThan256Bits() {
        String key = Base64.getEncoder().encodeToString(new byte[16]);
        assertThatThrownBy(() -> withSecret(key).validateSecret())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("256 bits");
    }

    @Test
    void rejectsANonBase64Key() {
        assertThatThrownBy(() -> withSecret("not base64 !!!").validateSecret())
                .isInstanceOf(IllegalStateException.class);
    }
}
