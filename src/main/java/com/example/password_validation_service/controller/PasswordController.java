package com.example.password_validation_service.controller;

import com.example.password_validation_service.dto.PasswordRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {

        @PostMapping("/validate")
        public boolean validatePassword(@RequestBody PasswordRequest request) {
            return false;

        }
    }
