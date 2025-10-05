package com.example.bankcards.mapper;

import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.entity.Payment;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T19:24:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentViewDto toViewDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        BigDecimal sum = null;
        String cardSenderNumber = null;
        String cardPayeeNumber = null;

        sum = payment.getSum();
        cardSenderNumber = payment.getCardSenderNumber();
        cardPayeeNumber = payment.getCardPayeeNumber();

        PaymentViewDto paymentViewDto = new PaymentViewDto( sum, cardSenderNumber, cardPayeeNumber );

        return paymentViewDto;
    }
}
