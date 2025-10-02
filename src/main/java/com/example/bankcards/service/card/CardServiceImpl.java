package com.example.bankcards.service.card;

import com.example.bankcards.config.context.UserContext;
import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardStatus;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Override
    @Transactional
    public CardViewDto createCard(CardCreateDto createDto) {
        Card card = Card.builder().build();

        Long min = 100_000_000_000L;
        Long max = 999_000_000_000L;
        Long randomNum = min + (long) (new SecureRandom().nextDouble() * (max - min + 1));

        User user = userRepository.findByIdOrElseThrow(createDto.ownerId());

        card.setCardNumber(String.valueOf(randomNum));
        card.setOwner(user);

        cardRepository.save(card);
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public CardViewDto changeCardStatus(Long id, CardUpdateDto updateDto) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        mapper.update(updateDto, card);
        cardRepository.save(card);
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public CardViewDto deleteCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        cardRepository.delete(card);
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public List<CardViewDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public CardViewDto getCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public List<CardViewDto> getAllCardsForUser() {
        Long userId = userContext.getUserId();
        List<Card> cards = cardRepository.findByOwner_Id(userId);
        return cards.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public CardViewDto blockCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return mapper.toViewDto(card);
    }
}
