package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardViewDto {
    private String cardNumber;
    private LocalDateTime expiryDate;
    private CardStatus status;
}
