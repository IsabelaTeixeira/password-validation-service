package com.example.password_validation_service.controller;

import com.example.password_validation_service.dto.PasswordRequest;
import com.example.password_validation_service.service.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    PasswordService passwordService;

    @PostMapping("/validate")
    public boolean validatePassword(@RequestBody PasswordRequest request) {
        log.info("Password validation request received");
        return passwordService.validatePassword(request.getPassword());
    }

    @PostMapping("/hash")
    public String hashPassword(@RequestBody PasswordRequest request) {
        log.info("Password hash generation request received");
        return passwordService.processPassword(request.getPassword());
    }
}
