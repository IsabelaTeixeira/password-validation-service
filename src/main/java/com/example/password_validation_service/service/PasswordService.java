package com.example.password_validation_service.service;

import com.example.password_validation_service.model.PasswordEntity;
import com.example.password_validation_service.repository.PasswordRepository;
import com.example.password_validation_service.validator.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PasswordService {
    private final PasswordValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRepository repository;

    public boolean validatePassword(String password) {
        return validator.isValid(password);
    }

    public String processPassword(String password) {

        if (!validator.isValid(password)) {
            throw new RuntimeException("Invalid password");
        }
        String hash = passwordEncoder.encode(password);

        PasswordEntity entity = PasswordEntity.builder()
                .passwordHash(hash)
                .build();

        repository.save(entity);
        return hash;
    }
}
