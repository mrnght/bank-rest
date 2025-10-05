package com.example.bankcards.controller;

import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards/payment")
@RequiredArgsConstructor
@Tag(name = "Переводы", description = "Операции перевода средств")
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @Operation(summary = "Перевод с карты на карту",
            description = "Создаёт платеж на основе переданных данных",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PaymentViewDto> createPayment(@Valid @RequestBody PaymentCreateDto createDto) {
        return ResponseEntity.ok(service.createPayment(createDto));
    }
}
