package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    Card toEntity(CardCreateDto createDto);

    @Mapping(target = "cardNumber", expression = "java(maskCardNumber(card.getCardNumber()))")
    CardViewDto toViewDto(Card card);

    void update(CardUpdateDto updateDto, @MappingTarget Card card);

    default String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() <= 12) {
            return cardNumber;
        }
        String maskedPart = "*".repeat(12);
        String remainingPart = cardNumber.substring(12);
        return maskedPart + remainingPart;
    }
}