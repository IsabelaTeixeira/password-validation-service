package com.example.password_validation_service.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordProducerTest {

    @InjectMocks
    private PasswordProducer producer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldSendPasswordCreatedEvent() {
        Long id = 1L;
        OffsetDateTime createdAt = OffsetDateTime.now();

        producer.sendPasswordCreatedEvent(id, createdAt);

        verify(kafkaTemplate).send(
                eq("password-validated"),
                eq("Password ID: " + id + " | Created at: " + createdAt)
        );

        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
        verifyNoMoreInteractions(kafkaTemplate);
    }
}
