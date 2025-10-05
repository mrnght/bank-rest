package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для создания нового пользователя и его дальнейшей авторизации")
public record UserAuthenticationDto(

        @NotBlank(message = "Имя не может быть пустым")
        @Schema(description = "Имя пользователя", example = "Aleksandr")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        @Schema(description = "Пароль пользователя", example = "password123")
        String password
) {
}
