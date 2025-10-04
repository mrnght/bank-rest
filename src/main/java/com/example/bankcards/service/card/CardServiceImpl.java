package com.example.bankcards.service.card;

import com.example.bankcards.config.security.JWTFilter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final UserRepository userRepository;
    private final JWTFilter jwtFilter;

    @Override
    @Transactional
    public CardViewDto createCard(CardCreateDto createDto) {
        Card card = new Card();

        Long min = 100_000_000_000L;
        Long max = 999_000_000_000L;
        Long randomNum = min + (long) (new SecureRandom().nextDouble() * (max - min + 1));
        card.setCardNumber(String.valueOf(randomNum));

        User user = userRepository.findByIdOrElseThrow(createDto.ownerId());
        card.setOwner(user);

        cardRepository.save(card);
        log.info("Карта с номером {} была создана", card.getCardNumber());
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public CardViewDto changeCardStatus(Long id, CardUpdateDto updateDto) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        mapper.update(updateDto, card);
        cardRepository.save(card);
        log.info("Статус карты с номером: {} был изменен на {}", card.getCardNumber(), updateDto.status());
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public CardViewDto deleteCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        cardRepository.delete(card);
        log.info("Карта с номером {} была удалена", card.getCardNumber());
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public List<CardViewDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        log.info("Получение списка всех карт");
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
        String username = jwtFilter.getUsername();
        Long userId = userRepository.findIdByUsernameOrElseThrow(username);
        List<Card> cards = cardRepository.findByOwner_Id(userId);
        log.info("Получения всех карт для юзера с id: {}", userId);
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
        log.info("Блокировка карты с номером: {}", card.getCardNumber());
        return mapper.toViewDto(card);
    }
}
