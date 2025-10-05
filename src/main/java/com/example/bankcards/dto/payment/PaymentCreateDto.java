package com.example.bankcards.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "DTO для создания платежа")
public record PaymentCreateDto(

        @NotNull(message = "Не указана сумма перевода")
        @Schema(description = "Сумма перевода", example = "35")
        BigDecimal sum,

        @NotBlank(message = "Не указана карта для снятия средств")
        @Schema(description = "Номер карты-отправителя средств", example = "123456789101")
        String cardSenderNumber,

        @NotBlank(message = "Не указана карта для пополнения")
        @Schema(description = "Номер карты-получателя средств", example = "567812349101")
        String cardPayeeNumber
) {
}
