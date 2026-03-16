package com.example.password_validation_service.controller;

import com.example.password_validation_service.dto.PasswordRequest;
import com.example.password_validation_service.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    PasswordService passwordService;

        @PostMapping("/validate")
        public boolean validatePassword(@RequestBody PasswordRequest request) {
            return passwordService.validatePassword(request.getPassword());
        }

        @PostMapping("/hash")
         public String hashPassword(@RequestBody PasswordRequest request) {
            return passwordService.processPassword(request.getPassword());
    }
}
