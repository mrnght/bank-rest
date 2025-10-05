package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для представления карты")
public class CardViewDto {
    @Schema(description = "Номер карты", example = "123456789101")
    private String cardNumber;
    @Schema(description = "Дата истечения карты", example = "2026-10-05T10:58:08.930335")
    private LocalDateTime expiryDate;
    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;
}
