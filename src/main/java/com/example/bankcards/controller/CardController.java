package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.service.card.CardService;
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
public class CardController {
    private final CardService service;

    //Админ
    @PostMapping
    public ResponseEntity<CardViewDto> createCard(@RequestBody CardCreateDto createDto) {
        return ResponseEntity.ok(service.createCard(createDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CardViewDto> changeCardStatus(@PathVariable Long id,
                                                        @RequestBody CardUpdateDto updateDto) {
        return ResponseEntity.ok(service.changeCardStatus(id, updateDto));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<CardViewDto> deleteCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteCard(id));
    }

    @GetMapping()
    public ResponseEntity<List<CardViewDto>> getAllCards() {
        return ResponseEntity.ok(service.getAllCards());
    }



    //Обычный челик
    @GetMapping("/{id}")
    public ResponseEntity<CardViewDto> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCard(id));
    }

    @GetMapping()
    public ResponseEntity<List<CardViewDto>> getAllCardsForUser() {
        return ResponseEntity.ok(service.getAllCardsForUser());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CardViewDto> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(service.blockCard(id));
    }

}
