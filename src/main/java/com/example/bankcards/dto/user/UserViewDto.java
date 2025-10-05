package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO с информацией о пользователе")
public record UserViewDto(

        @Schema(description = "Идентификатор пользователя", example = "123")
        Long id,

        @Schema(description = "Имя пользователя", example = "Aleksandr")
        String username,

        @Schema(description = "Роль пользователя", example = "ROLE_USER")
        String role
) {
}