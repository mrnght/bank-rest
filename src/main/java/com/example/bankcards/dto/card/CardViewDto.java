package com.example.bankcards.dto.card;

import com.example.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardViewDto {
    private String cardNumber;
    private User owner;
    private LocalDateTime expiryDate;
    private CardStatus status;
}
