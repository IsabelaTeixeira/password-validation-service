package com.example.password_validation_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setup() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void shouldEncodePassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String raw = "AbTp9!foK";
        String hash = encoder.encode(raw);
        assertTrue(encoder.matches(raw, hash));
    }
}