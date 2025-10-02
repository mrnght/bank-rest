package com.example.bankcards.service.payment;

import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentStatus;
import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Payment;
import com.example.bankcards.mapper.PaymentMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentViewDto createPayment(PaymentCreateDto createDto) {
        String cardSenderNumber = createDto.cardSenderNumber();
        String cardPayeeNumber = createDto.cardPayeeNumber();

        Card senderCard = cardRepository.findByCardNumberOrElseThrow(cardSenderNumber);
        Card payeeCard = cardRepository.findByCardNumberOrElseThrow(cardPayeeNumber);

        BigDecimal sum = createDto.sum();
        sendSum(senderCard, payeeCard, sum);

        Payment payment = Payment.builder()
                .sum(sum)
                .cardSenderNumber(cardSenderNumber)
                .cardPayeeNumber(cardPayeeNumber)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        return paymentMapper.toViewDto(payment);
    }

    private void sendSum(Card senderCard, Card payeeCard, BigDecimal sum) {
        BigDecimal senderBalance = senderCard.getBalance().subtract(sum);
        if (senderBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("На балансе отправителя недостаточно средств.");
        }
        senderCard.setBalance(senderBalance);

        BigDecimal payeeBalance = payeeCard.getBalance().add(sum);
        payeeCard.setBalance(payeeBalance);

        cardRepository.save(senderCard);
        cardRepository.save(payeeCard);
    }
}
