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
import com.example.bankcards.mapper.CardMapperImpl;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Spy
    private CardMapperImpl mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardBlockRequestRepository requestRepository;

    @Mock
    private JWTFilter jwtFilter;

    @InjectMocks
    private CardServiceImpl cardService;

    private User realUser;
    private Card createdCard;

    @BeforeEach
    void setUp() {
        realUser = new User();
        realUser.setId(1L);
        realUser.setUsername("user");

        createdCard = new Card();
        createdCard.setOwner(realUser);
        createdCard.setCardNumber("1234567890123456");
        createdCard.setExpiryDate(LocalDateTime.now().plusYears(3));
        createdCard.setStatus(CardStatus.ACTIVE);
    }

    private String getMaskedCardNumber(String fullCardNumber) {
        if (fullCardNumber == null || fullCardNumber.length() < 4) {
            return fullCardNumber;
        }
        String last4 = fullCardNumber.substring(fullCardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    @Test
    void createCardPositive() {
        CardCreateDto createDto = new CardCreateDto(1L);

        when(userRepository.findByIdOrElseThrow(1L)).thenReturn(realUser);
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(createdCard);

        cardService.createCard(createDto);

        verify(userRepository).findByIdOrElseThrow(1L);
        verify(cardRepository).existsByCardNumber(anyString());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void requestBlockCardPositive() {
        Long cardId = 10L;
        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);
        card.setOwner(realUser);

        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);
        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());
        when(requestRepository.existsByCardIdAndStatus(cardId, RequestStatus.PENDING)).thenReturn(false);

        cardService.requestBlockCard(cardId);

        verify(requestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void changeCardStatusPositive() {
        Long cardId = 5L;
        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        card.setCardNumber("1234567890123456");
        card.setExpiryDate(LocalDateTime.now().plusYears(2));

        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);

        CardUpdateDto updateDto = new CardUpdateDto(CardStatus.BLOCKED);

        Card updatedCard = new Card();
        updatedCard.setStatus(CardStatus.BLOCKED);
        updatedCard.setCardNumber(card.getCardNumber());
        updatedCard.setExpiryDate(card.getExpiryDate());

        when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);

        CardViewDto result = cardService.changeCardStatus(cardId, updateDto);

        assertEquals(CardStatus.BLOCKED, result.getStatus());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), result.getCardNumber());
        assertEquals(card.getExpiryDate(), result.getExpiryDate());

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void approveBlockRequestPositive() {
        Long requestId = 20L;

        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        card.setOwner(realUser);
        card.setCardNumber("1234567890123456");
        card.setExpiryDate(LocalDateTime.now().plusYears(4));

        CardBlockRequest request = new CardBlockRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.PENDING);
        request.setCard(card);

        when(requestRepository.findByIdOrElseThrow(requestId)).thenReturn(request);

        Card blockedCard = new Card();
        blockedCard.setStatus(CardStatus.BLOCKED);
        blockedCard.setCardNumber(card.getCardNumber());
        blockedCard.setExpiryDate(card.getExpiryDate());

        CardBlockRequest approvedRequest = new CardBlockRequest();
        approvedRequest.setStatus(RequestStatus.APPROVED);
        approvedRequest.setId(requestId);
        approvedRequest.setCard(card);

        when(cardRepository.save(any(Card.class))).thenReturn(blockedCard);
        when(requestRepository.save(any(CardBlockRequest.class))).thenReturn(approvedRequest);

        CardViewDto dto = cardService.approveBlockRequest(requestId);

        assertEquals(CardStatus.BLOCKED, dto.getStatus());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), dto.getCardNumber());
        assertEquals(card.getExpiryDate(), dto.getExpiryDate());

        verify(cardRepository).save(any(Card.class));
        verify(requestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void deleteCardPositive() {
        Long cardId = 7L;

        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        card.setOwner(realUser);
        card.setCardNumber("5555444433332222");
        card.setExpiryDate(LocalDateTime.now().plusYears(1));

        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);

        doNothing().when(cardRepository).delete(card);

        CardViewDto dto = cardService.deleteCard(cardId);

        verify(cardRepository).delete(card);
        assertEquals(CardStatus.DELETED, dto.getStatus());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), dto.getCardNumber());
        assertEquals(card.getExpiryDate(), dto.getExpiryDate());
    }

    @Test
    void getAllCardsPositive() {
        Card card = new Card();
        card.setOwner(realUser);
        card.setCardNumber("1234123412341234");
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDateTime.now().plusYears(5));

        when(cardRepository.findAll()).thenReturn(List.of(card));

        List<CardViewDto> cards = cardService.getAllCards();

        assertEquals(1, cards.size());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), cards.get(0).getCardNumber());
        assertEquals(card.getExpiryDate(), cards.get(0).getExpiryDate());
        assertEquals(card.getStatus(), cards.get(0).getStatus());
    }

    @Test
    void getCardPositive() {
        Long cardId = 8L;

        Card card = new Card();
        card.setOwner(realUser);
        card.setCardNumber("9999000011112222");
        card.setStatus(CardStatus.DELETED);
        card.setExpiryDate(LocalDateTime.now().plusMonths(6));

        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());
        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);

        CardViewDto dto = cardService.getCard(cardId);

        assertEquals(getMaskedCardNumber(card.getCardNumber()), dto.getCardNumber());
        assertEquals(card.getExpiryDate(), dto.getExpiryDate());
        assertEquals(card.getStatus(), dto.getStatus());
    }

    @Test
    void getCardNegative() {
        Long cardId = 9L;
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("otherUser");

        Card card = new Card();
        card.setOwner(anotherUser);
        card.setStatus(CardStatus.ACTIVE);

        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());
        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);

        assertThrows(ForbiddenException.class, () -> cardService.getCard(cardId));
    }

    @Test
    void getAllCardsForUserPositive() {
        int page = 0, size = 10;
        Long userId = realUser.getId();

        Card card = new Card();
        card.setOwner(realUser);
        card.setCardNumber("4444333322221111");
        card.setStatus(CardStatus.BLOCKED);
        card.setExpiryDate(LocalDateTime.now().plusDays(30));

        Page<Card> cardPage = new PageImpl<>(List.of(card), PageRequest.of(page, size), 1);

        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());
        when(userRepository.findIdByUsernameOrElseThrow(realUser.getUsername())).thenReturn(userId);
        when(cardRepository.findByOwner_Id(userId, PageRequest.of(page, size))).thenReturn(cardPage);

        Page<CardViewDto> resultPage = cardService.getAllCardsForUser(page, size);

        assertEquals(1, resultPage.getTotalElements());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), resultPage.getContent().get(0).getCardNumber());
        assertEquals(card.getExpiryDate(), resultPage.getContent().get(0).getExpiryDate());
        assertEquals(card.getStatus(), resultPage.getContent().get(0).getStatus());
    }

    @Test
    void blockCardPositive() {
        Long cardId = 15L;
        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        card.setOwner(realUser);
        card.setCardNumber("1111222233334444");
        card.setExpiryDate(LocalDateTime.now().plusWeeks(12));

        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);
        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());

        Card blockedCard = new Card();
        blockedCard.setStatus(CardStatus.BLOCKED);
        blockedCard.setCardNumber(card.getCardNumber());
        blockedCard.setExpiryDate(card.getExpiryDate());

        when(cardRepository.save(any(Card.class))).thenReturn(blockedCard);

        CardViewDto dto = cardService.blockCard(cardId);

        assertEquals(CardStatus.BLOCKED, dto.getStatus());
        assertEquals(getMaskedCardNumber(card.getCardNumber()), dto.getCardNumber());
        assertEquals(card.getExpiryDate(), dto.getExpiryDate());

        verify(cardRepository).save(any(Card.class));
    }
    @Test
    void blockCardNegative() {
        Long cardId = 16L;
        User anotherUser = new User();
        anotherUser.setUsername("otherUser");

        Card card = new Card();
        card.setOwner(anotherUser);
        card.setStatus(CardStatus.ACTIVE);

        when(jwtFilter.getUsername()).thenReturn(realUser.getUsername());
        when(cardRepository.findByIdOrElseThrow(cardId)).thenReturn(card);

        assertThrows(ForbiddenException.class, () -> cardService.blockCard(cardId));
    }
}
