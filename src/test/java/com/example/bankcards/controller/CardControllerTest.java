package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardStatus;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CardController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService service;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserDetailsImpl userDetails;

    @Test
    void createCardTest() throws Exception {
        CardCreateDto createDto = new CardCreateDto(1L);
        CardViewDto response = CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .build();

        when(service.createCard(createDto)).thenReturn(response);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void changeCardStatusTest() throws Exception {
        Long id = 1L;
        CardUpdateDto updateDto = new CardUpdateDto(CardStatus.BLOCKED);
        CardViewDto response = CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.BLOCKED)
                .build();

        when(service.changeCardStatus(id, updateDto)).thenReturn(response);

        mockMvc.perform(put("/cards/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void approveBlockRequestTest() throws Exception {
        Long requestId = 1L;
        CardViewDto response = CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.BLOCKED)
                .build();

        when(service.approveBlockRequest(requestId)).thenReturn(response);

        mockMvc.perform(put("/cards/block-request/{requestId}/approve", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void deleteCardTest() throws Exception {
        Long id = 1L;
        CardViewDto response = CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.DELETED)
                .build();

        when(service.deleteCard(id)).thenReturn(response);

        mockMvc.perform(delete("/cards/{id}/delete", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"));
    }

    @Test
    void getAllCardsTest() throws Exception {
        List<CardViewDto> response = List.of(CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .build());

        when(service.getAllCards()).thenReturn(response);

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void requestBlockCardTest() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/cards/user/{id}/block", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Запрос на блокировку отправлен и ожидает одобрения администратора"));
    }

    @Test
    void getCardTest() throws Exception {
        Long id = 1L;
        CardViewDto response = CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .build();

        when(service.getCard(id)).thenReturn(response);

        mockMvc.perform(get("/cards/user/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 3456"));
    }

    @Test
    void getAllCardsForUserTest() throws Exception {
        List<CardViewDto> cards = List.of(CardViewDto.builder()
                .cardNumber("1234567890123456")
                .expiryDate(LocalDateTime.now().plusYears(1))
                .status(CardStatus.ACTIVE)
                .build());

        Pageable pageable = PageRequest.of(0, 10);
        Page<CardViewDto> response = new PageImpl<>(cards, pageable, cards.size());

        when(service.getAllCardsForUser(0, 10)).thenReturn(response);

        mockMvc.perform(get("/cards/user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }
}

