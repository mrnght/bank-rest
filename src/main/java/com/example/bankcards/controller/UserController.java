package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Операции над пользователями")
public class UserController {
    private final UserService service;

    @PostMapping("/registration")
    @Operation(summary = "Зарегистрировать пользователя",
            description = "Создаёт нового пользователя на основе переданных данных")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserAuthenticationDto authenticationDto) {
        return ResponseEntity.ok(service.createUser(authenticationDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Войти в аккаунт",
            description = "Авторизуется на основе переданных данных")
    public ResponseEntity<Map<String, String>> performLogin(@Valid @RequestBody UserAuthenticationDto authenticationDto) {
        return ResponseEntity.ok(service.performLogin(authenticationDto));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Получить пользователя",
            description = "Возвращает данные пользователя по ID")
    public ResponseEntity<UserViewDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }
}
