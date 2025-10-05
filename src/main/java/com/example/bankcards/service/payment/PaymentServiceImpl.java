package com.example.bankcards.service.payment;

import com.example.bankcards.config.security.JWTFilter;
import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Payment;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.mapper.PaymentMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final PaymentMapper paymentMapper;
    private final JWTFilter jwtFilter;

    @Override
    @Transactional
    public PaymentViewDto createPayment(PaymentCreateDto createDto) {
        String username = jwtFilter.getUsername();
        String cardSenderNumber = createDto.cardSenderNumber();
        String cardPayeeNumber = createDto.cardPayeeNumber();

        Card senderCard = cardRepository.findByCardNumberOrElseThrow(cardSenderNumber);
        Card payeeCard = cardRepository.findByCardNumberOrElseThrow(cardPayeeNumber);

        BigDecimal sum = createDto.sum();
        validateAndSend(senderCard, payeeCard, sum, username);

        Payment payment = Payment.builder()
                .sum(sum)
                .cardSenderNumber(cardSenderNumber)
                .cardPayeeNumber(cardPayeeNumber)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        log.info("Выполнен платеж на сумму {} с карты {} на карту {}", sum, cardSenderNumber, cardPayeeNumber);
        return paymentMapper.toViewDto(payment);
    }

    private void validateAndSend(Card senderCard, Card payeeCard, BigDecimal sum, String username) {
        BigDecimal senderBalance = senderCard.getBalance().subtract(sum);

        if (!senderCard.getOwner().getUsername().equals(username))
            throw new ForbiddenException("Карта принадлежит другому пользователю");

        if (senderBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("На балансе отправителя недостаточно средств.");

        if (senderCard.getCardNumber().equals(payeeCard.getCardNumber()))
            throw new IllegalArgumentException("Невозможно выполнить перевод на ту же карту.");

        senderCard.setBalance(senderBalance);

        BigDecimal payeeBalance = payeeCard.getBalance().add(sum);
        payeeCard.setBalance(payeeBalance);

        cardRepository.save(senderCard);
        cardRepository.save(payeeCard);
    }
}
