package com.example.password_validation_service.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@AllArgsConstructor
@Service
public class PasswordCacheService {

    private final RedisTemplate<String, Boolean> redisTemplate;

    public Boolean getCachedValidation(String password) {
        return redisTemplate.opsForValue().get(password);
    }

    public void cacheValidation(String password, Boolean valid) {
        redisTemplate.opsForValue().set(password, valid, Duration.ofMinutes(10));
    }
}
