package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CardService {
    CardViewDto createCard(CardCreateDto createDto);

    CardViewDto getCard(Long id);

    CardViewDto changeCardStatus(Long id, CardUpdateDto updateDto);

    CardViewDto deleteCard(Long id);

    List<CardViewDto> getAllCards();

    Page<CardViewDto> getAllCardsForUser(int page, int size);

    CardViewDto blockCard(Long id);

    void requestBlockCard(Long id);

    CardViewDto approveBlockRequest(Long requestId);
}
