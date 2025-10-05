package com.example.bankcards.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO для отображения платежа")
public record PaymentViewDto(

        @Schema(description = "Сумма перевода", example = "35")
        BigDecimal sum,

        @Schema(description = "Номер карты-отправителя средств", example = "123456789101111")
        String cardSenderNumber,

        @Schema(description = "Номер карты-получателя средств", example = "5678123491011111")
        String cardPayeeNumber
) {
}
