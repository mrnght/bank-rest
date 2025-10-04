package com.example.bankcards.dto.payment;

import java.math.BigDecimal;

public record PaymentViewDto(
        BigDecimal sum,
        String cardSenderNumber,
        String cardPayeeNumber
) {
}
