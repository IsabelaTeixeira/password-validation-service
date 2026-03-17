package com.example.password_validation_service.service;

import com.example.password_validation_service.exception.ApiException;
import com.example.password_validation_service.message.PasswordProducer;
import com.example.password_validation_service.model.PasswordEntity;
import com.example.password_validation_service.repository.PasswordRepository;
import com.example.password_validation_service.validator.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor

public class PasswordService {

    private final PasswordValidator validator;

    private final PasswordEncoder passwordEncoder;

    private final PasswordRepository repository;

    private final PasswordProducer producer;

    private final PasswordCacheService cacheService;

    public boolean validatePassword(String password) {
        log.info("Starting password validation");

        Boolean cachedResult = cacheService.getCachedValidation(password);
        if (cachedResult != null) {
            log.info("Password validation result retrieved from cache");
            return cachedResult;
        }

        boolean isValid = validator.isValid(password);
        log.info("Password validation: {}", isValid);

        if (isValid) {
            cacheService.cacheValidation(password, true);
            log.info("Result cached successfully");
        }

        return isValid;
    }

    public String processPassword(String password) {
        log.info("Starting hash password processing");

        if (!validator.isValid(password)) {
            log.warn("Password processing failed: password does not meet security rules");
            throw new ApiException("Password does not meet security rules", HttpStatus.UNPROCESSABLE_CONTENT);
        }

        log.info("Password validated");
        String hash = passwordEncoder.encode(password);
        log.info("Password hash generated successfully");

        PasswordEntity entity = PasswordEntity.builder()
                .passwordHash(hash)
                .build();

        PasswordEntity saved = repository.save(entity);
        log.info("Password saved in database: Password ID: {} | Created at: {}", saved.getId(), saved.getCreatedAt());
        producer.sendPasswordCreatedEvent(saved.getId(), saved.getCreatedAt());
        log.info("Password created event published: Password ID: {} | Created at: {}", saved.getId(), saved.getCreatedAt());

        return hash;
    }
}
