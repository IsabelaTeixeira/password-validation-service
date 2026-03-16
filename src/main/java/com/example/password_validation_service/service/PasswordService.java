package com.example.password_validation_service.service;

import com.example.password_validation_service.message.PasswordProducer;
import com.example.password_validation_service.model.PasswordEntity;
import com.example.password_validation_service.repository.PasswordRepository;
import com.example.password_validation_service.validator.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Service
@AllArgsConstructor
public class PasswordService {
    private final PasswordValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRepository repository;
    private final PasswordProducer producer;

    public boolean validatePassword(String password) {
        return validator.isValid(password);
    }

    public String processPassword(String password) {
        if (!validator.isValid(password)) {
            throw new IllegalArgumentException("Password does not meet security rules");
        }
        String hash = passwordEncoder.encode(password);

        PasswordEntity entity = PasswordEntity.builder()
                .passwordHash(hash)
                .build();

        PasswordEntity saved = repository.save(entity);
        producer.sendPasswordCreatedEvent(saved.getId(),saved.getCreatedAt());

        return hash;
    }
}
