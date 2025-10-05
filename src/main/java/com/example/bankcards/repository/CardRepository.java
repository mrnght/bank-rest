package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    default Card findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такой карты не существует"));
    }

    Page<Card> findByOwner_Id(Long userId, Pageable pageable);

    Optional<Card> findByCardNumber(String cardNumber);

    default Card findByCardNumberOrElseThrow(String cardNumber) {
        return findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Такой карты не существует"));
    }

    boolean existsByCardNumber(String username);
}
