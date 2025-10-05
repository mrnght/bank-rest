package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserDetailsImpl userDetails;

    @Test
    void registerUserTest() throws Exception {
        UserAuthenticationDto authDto = new UserAuthenticationDto("Aleksandr", "password123");

        Map<String, String> responseMap = Map.of("message", "User registered successfully", "userId", "123");

        Mockito.when(service.createUser(any(UserAuthenticationDto.class))).thenReturn(responseMap);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value("123"));
    }

    @Test
    void performLoginTest() throws Exception {
        UserAuthenticationDto authDto = new UserAuthenticationDto("Aleksandr", "password123");

        Map<String, String> responseMap = Map.of("token", "jwt-token-here", "message", "Login successful");

        Mockito.when(service.performLogin(any(UserAuthenticationDto.class))).thenReturn(responseMap);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void getUserTest() throws Exception {
        UserViewDto responseDto = new UserViewDto(123L, "Aleksandr", "ROLE_USER");

        Mockito.when(service.getUser(123L)).thenReturn(responseDto);

        mockMvc.perform(get("/users/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.username").value("Aleksandr"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }
}