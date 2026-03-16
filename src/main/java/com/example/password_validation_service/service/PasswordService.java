package com.example.password_validation_service.service;

import com.example.password_validation_service.exception.ApiException;
import com.example.password_validation_service.message.PasswordProducer;
import com.example.password_validation_service.model.PasswordEntity;
import com.example.password_validation_service.repository.PasswordRepository;
import com.example.password_validation_service.validator.PasswordValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PasswordService {
    private final PasswordValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRepository repository;
    private final PasswordProducer producer;
    private final PasswordCacheService cacheService;

    public boolean validatePassword(String password) {

        Boolean cachedResult = cacheService.getCachedValidation(password);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean isValid = validator.isValid(password);

        if(isValid){
        cacheService.cacheValidation(password, true);
        }
        return isValid;
    }

    public String processPassword(String password) {
        if (!validator.isValid(password)) {
            throw new ApiException("Password does not meet security rules", HttpStatus.UNPROCESSABLE_CONTENT);
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
