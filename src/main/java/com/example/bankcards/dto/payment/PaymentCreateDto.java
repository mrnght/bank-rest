package com.example.bankcards.dto.payment;

import java.math.BigDecimal;

public record PaymentCreateDto(
        BigDecimal sum,
        String cardSenderNumber,
        String cardPayeeNumber
) {
}
