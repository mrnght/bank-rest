package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для обновления статуса карты")
public record CardUpdateDto(
        @NotNull(message = "Не указан измененный статус")
        @Schema(description = "Новый статус", example = "EXPIRED")
        CardStatus status
) {
}
