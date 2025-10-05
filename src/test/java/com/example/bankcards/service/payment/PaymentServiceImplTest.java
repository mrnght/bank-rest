package com.example.bankcards.service.payment;

import com.example.bankcards.config.security.JWTFilter;
import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Payment;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.PaymentMapperImpl;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardRepository cardRepository;

    @Spy
    private PaymentMapperImpl paymentMapper;

    @Mock
    private JWTFilter jwtFilter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentCreateDto createDto;
    private User owner;
    private Card senderCard;
    private Card payeeCard;
    private Payment expectedPayment;

    @Test
    void createPaymentPositive() {
        setupDataForPositive();
        setupMocksForPositive();
        performAndVerifyPositive();
    }

    private void setupDataForPositive() {
        createDto = new PaymentCreateDto(
                BigDecimal.valueOf(50),
                "1234567890123456",
                "9876543210987654"
        );

        owner = new User();
        owner.setUsername("testUser");

        senderCard = new Card();
        senderCard.setCardNumber("1234567890123456");
        senderCard.setBalance(BigDecimal.valueOf(100));
        senderCard.setOwner(owner);

        payeeCard = new Card();
        payeeCard.setCardNumber("9876543210987654");
        payeeCard.setBalance(BigDecimal.valueOf(20));
        payeeCard.setOwner(owner);
    }

    private void setupMocksForPositive() {
        when(cardRepository.findByCardNumberOrElseThrow("1234567890123456")).thenReturn(senderCard);
        when(cardRepository.findByCardNumberOrElseThrow("9876543210987654")).thenReturn(payeeCard);
        when(jwtFilter.getUsername()).thenReturn("testUser");

        expectedPayment = Payment.builder()
                .sum(BigDecimal.valueOf(50))
                .cardSenderNumber("1234567890123456")
                .cardPayeeNumber("9876543210987654")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);
    }

    private void performAndVerifyPositive() {
        PaymentViewDto resultDto = paymentService.createPayment(createDto);

        assertNotNull(resultDto);
        assertEquals(BigDecimal.valueOf(50), resultDto.sum());
        assertEquals("1234567890123456", resultDto.cardSenderNumber());
        assertEquals("9876543210987654", resultDto.cardPayeeNumber());

        verify(cardRepository, times(1)).findByCardNumberOrElseThrow("1234567890123456");
        verify(cardRepository, times(1)).findByCardNumberOrElseThrow("9876543210987654");
        verify(jwtFilter, times(1)).getUsername();
        verify(cardRepository, times(1)).save(senderCard);
        verify(cardRepository, times(1)).save(payeeCard);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentMapper).toViewDto(any(Payment.class));
    }

    @Test
    void createPaymentNegative() {
        setupDataForNegative();
        setupMocksForNegative();
        performAndVerifyNegative();
    }

    private void setupDataForNegative() {
        createDto = new PaymentCreateDto(
                BigDecimal.valueOf(150),
                "1234567890123456",
                "9876543210987654"
        );

        owner = new User();
        owner.setUsername("testUser");

        senderCard = new Card();
        senderCard.setCardNumber("1234567890123456");
        senderCard.setBalance(BigDecimal.valueOf(100));
        senderCard.setOwner(owner);

        payeeCard = new Card();
        payeeCard.setCardNumber("9876543210987654");
        payeeCard.setBalance(BigDecimal.valueOf(20));
        payeeCard.setOwner(owner);
    }

    private void setupMocksForNegative() {
        when(cardRepository.findByCardNumberOrElseThrow("1234567890123456")).thenReturn(senderCard);
        when(cardRepository.findByCardNumberOrElseThrow("9876543210987654")).thenReturn(payeeCard);
        when(jwtFilter.getUsername()).thenReturn("testUser");
    }

    private void performAndVerifyNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(createDto);
        });

        assertEquals("На балансе отправителя недостаточно средств.", exception.getMessage());

        verify(cardRepository, times(1)).findByCardNumberOrElseThrow("1234567890123456");
        verify(cardRepository, times(1)).findByCardNumberOrElseThrow("9876543210987654");
        verify(jwtFilter, times(1)).getUsername();
        verify(cardRepository, never()).save(any(Card.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentMapper, never()).toViewDto(any());
    }
}
