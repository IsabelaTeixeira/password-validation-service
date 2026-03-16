package com.example.password_validation_service.validator;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-+])(?!.*(.).*\\1)[A-Za-z0-9!@#$%^&*()\\-+]{9,}$";

    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return password.matches(PASSWORD_REGEX);
    }

}
