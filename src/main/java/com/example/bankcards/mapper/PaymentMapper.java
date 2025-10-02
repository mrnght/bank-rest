package com.example.bankcards.mapper;

import com.example.bankcards.dto.payment.PaymentViewDto;
import com.example.bankcards.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    PaymentViewDto toViewDto(Payment payment);
}
