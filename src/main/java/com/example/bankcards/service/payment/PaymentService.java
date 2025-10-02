package com.example.bankcards.service.payment;

import com.example.bankcards.dto.payment.PaymentCreateDto;
import com.example.bankcards.dto.payment.PaymentViewDto;

public interface PaymentService {
    PaymentViewDto createPayment(PaymentCreateDto createDto);
}
