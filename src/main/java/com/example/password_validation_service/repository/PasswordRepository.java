package com.example.password_validation_service.repository;

import com.example.password_validation_service.model.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<PasswordEntity, Long> {
}
