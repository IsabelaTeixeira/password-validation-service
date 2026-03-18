package com.example.password_validation_service.service;

import com.example.password_validation_service.exception.ApiException;
import com.example.password_validation_service.message.PasswordProducer;
import com.example.password_validation_service.model.PasswordEntity;
import com.example.password_validation_service.repository.PasswordRepository;
import com.example.password_validation_service.validator.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @InjectMocks
    private PasswordService service;

    @Mock
    private PasswordCacheService cacheService;

    @Mock
    private PasswordValidator validator;

    @Mock
    private PasswordRepository repository;

    @Mock
    private PasswordProducer producer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnCachedResult_WhenCacheExists() {
        when(cacheService.getCachedValidation("AbTp9!foK")).thenReturn(true);

        boolean result = service.validatePassword("AbTp9!foK");

        assertTrue(result);
        verify(validator, never()).isValid(any());
        verify(cacheService, never()).cacheValidation(any(), any());
    }

    @Test
    void shouldValidateAndCache_WhenNotCachedAndValid() {
        when(cacheService.getCachedValidation("AbTp9!foK")).thenReturn(null);
        when(validator.isValid("AbTp9!foK")).thenReturn(true);

        boolean result = service.validatePassword("AbTp9!foK");

        assertTrue(result);
        verify(validator).isValid("AbTp9!foK");
        verify(cacheService).cacheValidation("AbTp9!foK", true);
    }

    @Test
    void shouldValidateAndNotCache_WhenInvalid() {
        when(cacheService.getCachedValidation("AbTp9")).thenReturn(null);
        when(validator.isValid("AbTp9")).thenReturn(false);

        boolean result = service.validatePassword("AbTp9");

        assertFalse(result);
        verify(cacheService, never()).cacheValidation(any(), any());
    }

    @Test
    void shouldNotCallOtherServices_WhenPasswordIsInvalid() {
        String invalidPassword = "abc";

        when(validator.isValid(invalidPassword)).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class,
                () -> service.processPassword(invalidPassword));

        assert (exception.getMessage().equals("Password does not meet security rules"));
        assert (exception.getStatus() == HttpStatus.UNPROCESSABLE_CONTENT);

        verify(validator).isValid(invalidPassword);

    }

    @Test
    void shouldProcessPasswordSuccessfully_WhenValid() {
        when(validator.isValid("AbTp9!foK")).thenReturn(true);
        when(passwordEncoder.encode("AbTp9!foK")).thenReturn("hashed");

        PasswordEntity entity = PasswordEntity.builder()
                .id(1L)
                .passwordHash("hashed")
                .createdAt(OffsetDateTime.now())
                .build();

        when(repository.save(any())).thenReturn(entity);

        String result = service.processPassword("AbTp9!foK");

        assertEquals("hashed", result);

        verify(passwordEncoder).encode("AbTp9!foK");
        verify(repository).save(any());
        verify(producer).sendPasswordCreatedEvent(eq(1L), any());
    }

}