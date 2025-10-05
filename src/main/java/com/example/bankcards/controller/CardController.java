package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
@Tag(name = "Карты", description = "Операции с картами")
public class CardController {
    private final CardService service;

    @PostMapping
    @Operation(summary = "Создать карту (admin)",
            description = "Создаёт новую карту на основе переданных данных",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardViewDto> createCard(@Valid @RequestBody CardCreateDto createDto) {
        return ResponseEntity.ok(service.createCard(createDto));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Изменить статус карты (admin)",
            description = "Изменяет статус карты на основе переданных данных",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardViewDto> changeCardStatus(@PathVariable Long id,
                                                        @Valid @RequestBody CardUpdateDto updateDto) {
        return ResponseEntity.ok(service.changeCardStatus(id, updateDto));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Удалить карту (admin)",
            description = "Удаляет карту по ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardViewDto> deleteCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteCard(id));
    }

    @GetMapping
    @Operation(summary = "Получить карты (admin)",
            description = "Получает все созданные в системе карты",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CardViewDto>> getAllCards() {
        return ResponseEntity.ok(service.getAllCards());
    }


    @GetMapping("/user/{id}")
    @Operation(summary = "Получить карту (user)",
            description = "Позволяет пользователю посмотреть карту по ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardViewDto> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCard(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Получить все карты пользователя (user)",
            description = "Позволяет пользователю посмотреть все свои карты",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CardViewDto>> getAllCardsForUser() {
        return ResponseEntity.ok(service.getAllCardsForUser());
    }

    @PutMapping("/user/{id}/block")
    @Operation(summary = "Заблокировать карту (user)",
            description = "Позволяет пользователю заблокировать карту по ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardViewDto> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.blockCard(id));
    }
}
