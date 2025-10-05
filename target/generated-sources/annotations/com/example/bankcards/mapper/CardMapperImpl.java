package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardCreateDto;
import com.example.bankcards.dto.card.CardUpdateDto;
import com.example.bankcards.dto.card.CardViewDto;
import com.example.bankcards.entity.Card;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T19:24:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public Card toEntity(CardCreateDto createDto) {
        if ( createDto == null ) {
            return null;
        }

        Card.CardBuilder card = Card.builder();

        return card.build();
    }

    @Override
    public CardViewDto toViewDto(Card card) {
        if ( card == null ) {
            return null;
        }

        CardViewDto cardViewDto = new CardViewDto();

        cardViewDto.setExpiryDate( card.getExpiryDate() );
        cardViewDto.setStatus( card.getStatus() );

        cardViewDto.setCardNumber( maskCardNumber(card.getCardNumber()) );

        return cardViewDto;
    }

    @Override
    public void update(CardUpdateDto updateDto, Card card) {
        if ( updateDto == null ) {
            return;
        }

        card.setStatus( updateDto.status() );
    }
}
