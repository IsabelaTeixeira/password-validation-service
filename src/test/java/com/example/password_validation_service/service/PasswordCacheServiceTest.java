package com.example.password_validation_service.service;

import com.example.password_validation_service.validator.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordCacheServiceTest {

    @InjectMocks
    private PasswordCacheService cacheService;

    @Mock
    private RedisTemplate<String, Boolean> redisTemplate;

    @Mock
    private ValueOperations<String, Boolean> valueOperations;

    @Test
    void shouldReturnCachedValidation() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("abc")).thenReturn(true);

        Boolean result = cacheService.getCachedValidation("abc");

        assertTrue(result);
        verify(valueOperations).get("abc");
    }

    @Test
    void shouldReturnNullWhenCacheMiss() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("abc")).thenReturn(null);

        Boolean result = cacheService.getCachedValidation("abc");

        assertNull(result);
    }

    @Test
    void shouldReturnFalse_WhenPasswordIsNull() {
        PasswordValidator validator = new PasswordValidator();

        boolean result = validator.isValid(null);

        assertFalse(result);
    }

    @Test
    void shouldCacheValidationWithTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cacheService.cacheValidation("abc", true);
        Duration ttl = Duration.ofMinutes(10);
        verify(valueOperations).set("abc", true, ttl);
    }
}