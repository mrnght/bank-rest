package com.example.bankcards.controller;

import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.payment.PaymentService;
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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PaymentController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserDetailsImpl userDetails;

    @Test
    void createPaymentTest() throws Exception {
        PaymentCreateDto createDto = new PaymentCreateDto(
                new BigDecimal("35"),
                "123456789101111",
                "5678123491011111"
        );

        PaymentViewDto responseDto = new PaymentViewDto(
                new BigDecimal("35"),
                "123456789101111",
                "5678123491011111"
        );

        Mockito.when(service.createPayment(any(PaymentCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/cards/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(35))
                .andExpect(jsonPath("$.cardSenderNumber").value("123456789101111"))
                .andExpect(jsonPath("$.cardPayeeNumber").value("5678123491011111"));
    }
}
