package com.example.password_validation_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
@Service
public class PasswordCacheService {

    private final RedisTemplate<String, Boolean> redisTemplate;

    public Boolean getCachedValidation(String password) {
        return redisTemplate.opsForValue().get(password);
    }

    public void cacheValidation(String password, Boolean valid) {
        Duration ttl = Duration.ofMinutes(10);
        redisTemplate.opsForValue().set(password, valid, ttl);
        log.info("Password validation result cached successfully (TTL: {} minutes)", ttl.toMinutes());
    }
}
