package com.example.password_validation_service.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@AllArgsConstructor
@Builder
public class PasswordProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPasswordCreatedEvent(Long id, OffsetDateTime createdAt) {
    kafkaTemplate.send("password-validated", "Password ID: "+ id + " | Created at: " + createdAt);
    }

}
