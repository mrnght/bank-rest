package com.example.bankcards.service.card;

import com.example.bankcards.config.security.JWTFilter;
import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardStatus;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.dto.card.RequestStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CardBlockRequestRepository requestRepository;
    private final JWTFilter jwtFilter;
    private static final SecureRandom random = new SecureRandom();
    private static final long MIN = 100_000_000_000L;
    private static final long MAX = 999_000_000_000L;
    private static final int MAX_ATTEMPTS = 5;

    @Override
    @Transactional
    public CardViewDto createCard(CardCreateDto createDto) {
        User user = userRepository.findByIdOrElseThrow(createDto.ownerId());
        String cardNumber = generateUniqueCardNumber();

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setOwner(user);

        cardRepository.save(card);
        log.info("Карта с номером {} была создана", card.getCardNumber());
        return mapper.toViewDto(card);
    }

    private String generateUniqueCardNumber() {
        int attempts = 0;
        String cardNumber;
        do {
            if (++attempts > MAX_ATTEMPTS) {
                throw new IllegalStateException("Не удалось сгенерировать уникальный номер карты после "
                        + MAX_ATTEMPTS + " попыток");
            }
            long randomNum = MIN + (long) (random.nextDouble() * (MAX - MIN + 1));
            cardNumber = String.valueOf(randomNum);
        } while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }

    @Transactional
    @Override
    public void requestBlockCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);

        if (card.getStatus().equals(CardStatus.EXPIRED))
            throw new IllegalArgumentException("Невозможно заблокировать просроченную карту");

        String username = jwtFilter.getUsername();
        if (!card.getOwner().getUsername().equals(username))
            throw new ForbiddenException("Карта принадлежит другому пользователю");

        if (requestRepository.existsByCardIdAndStatus(card.getId(), RequestStatus.PENDING))
            throw new IllegalStateException("Запрос на блокировку уже существует");

        CardBlockRequest request = new CardBlockRequest();
        request.setCard(card);
        request.setRequesterUsername(username);
        requestRepository.save(request);

        log.info("Пользователь {} создал запрос на блокировку карты {}", username, card.getCardNumber());
    }

    @Transactional
    @Override
    public CardViewDto changeCardStatus(Long id, CardUpdateDto updateDto) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        if (card.getStatus().equals(CardStatus.EXPIRED))
            throw new IllegalArgumentException("Невозможно поменять статус просроченной карты");

        mapper.update(updateDto, card);
        cardRepository.save(card);

        log.info("Статус карты с номером: {} был изменён на {}", card.getCardNumber(), updateDto.status());
        return mapper.toViewDto(card);
    }

    @Transactional
    @Override
    public CardViewDto approveBlockRequest(Long requestId) {
        CardBlockRequest request = requestRepository.findByIdOrElseThrow(requestId);
        if (request.getStatus().equals(RequestStatus.APPROVED))
            throw new IllegalStateException("Заявка уже обработана");

        Card card = request.getCard();
        if (card.getStatus().equals(CardStatus.EXPIRED))
            throw new IllegalArgumentException("Невозможно заблокировать просроченную карту");

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        log.info("Заявка {} одобрена, карта {} заблокирована", requestId, card.getCardNumber());
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public CardViewDto deleteCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        cardRepository.delete(card);
        card.setStatus(CardStatus.DELETED);
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
        String username = jwtFilter.getUsername();
        Card card = cardRepository.findByIdOrElseThrow(id);
        if (!card.getOwner().getUsername().equals(username))
            throw new ForbiddenException("Карта принадлежит другом пользователю");
        return mapper.toViewDto(card);
    }

    @Override
    @Transactional
    public Page<CardViewDto> getAllCardsForUser(int page, int size) {
        String username = jwtFilter.getUsername();
        Long userId = userRepository.findIdByUsernameOrElseThrow(username);

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cardPage = cardRepository.findByOwner_Id(userId, pageable);

        log.info("Получение карт для пользователя с id: {} - страница {}, размер {}", userId, page, size);


        var dtoPage = cardPage.map(mapper::toViewDto);
        return dtoPage;
    }

    @Override
    @Transactional
    public CardViewDto blockCard(Long id) {
        Card card = cardRepository.findByIdOrElseThrow(id);
        if (card.getStatus().equals(CardStatus.EXPIRED))
            throw new IllegalArgumentException("Невозможно заблокировать просроченную карту");

        String username = jwtFilter.getUsername();
        if (!card.getOwner().getUsername().equals(username))
            throw new ForbiddenException("Карта принадлежит другом пользователю");

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        log.info("Блокировка карты с номером: {}", card.getCardNumber());
        return mapper.toViewDto(card);
    }
}
