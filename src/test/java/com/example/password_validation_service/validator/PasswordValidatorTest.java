package com.example.password_validation_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void shouldReturnTrue_WhenPasswordIsValid() {
        boolean result = validator.isValid("AbTp9!fok");

        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abcdefghi",
            "ABCDEFGHI",
            "123456789",
            "Abcdefghi",
            "Abcdefg1h",
            "Abcdefg!h"
    })
    void shouldReturnFalse_ForInvalidPasswords(String password) {
        assertFalse(validator.isValid(password));
    }

}