package com.example.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для создания карты")
public record CardCreateDto(
        @NotNull(message = "Не указан владелец карты")
        @Schema(description = "ID владельца", example = "3")
        Long ownerId
) {
}
