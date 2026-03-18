package com.example.password_validation_service.controller;

import com.example.password_validation_service.exception.ApiException;
import com.example.password_validation_service.service.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PasswordController.class)
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordService passwordService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void shouldValidatePassword_WhenPasswordIsValid() throws Exception {
        String json = """
                {"password": "AbTp9!foK"}
                """;
        Mockito.when(passwordService.validatePassword("AbTp9!foK")).thenReturn(true);

        mockMvc.perform(post("/password/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(passwordService).validatePassword("AbTp9!foK");
    }

    @Test
    void shouldValidatePassword_WhenPasswordIsNotValid() throws Exception {
        String json = """
                {"password": "AbTp9!foK"}
                """;
        Mockito.when(passwordService.validatePassword("AbTp9")).thenReturn(false);

        mockMvc.perform(post("/password/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

    }

    @Test
    void shouldReturn422_WhenPasswordDoesNotMeetRules() throws Exception {
        String json = """
                {"password": "AbTp9"}
                """;
        Mockito.when(passwordService.processPassword("AbTp9"))
                .thenThrow(new ApiException(
                        "Password does not meet security rules",
                        HttpStatus.UNPROCESSABLE_CONTENT
                ));

        mockMvc.perform(post("/password/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void shouldGenerateHash_WhenPasswordIsValid() throws Exception {
        String json = """
                {"password": "AbTp9!foK"}
                """;
        Mockito.when(passwordService.processPassword("AbTp9!foK"))
                .thenReturn("$2a$10$hashmockado");

        mockMvc.perform(post("/password/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("$2a$10$hashmockado"));
    }

}