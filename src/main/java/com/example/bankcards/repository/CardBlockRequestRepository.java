package com.example.bankcards.repository;

import com.example.bankcards.dto.card.RequestStatus;
import com.example.bankcards.entity.CardBlockRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, Long> {
    boolean existsByCardIdAndStatus(Long cardId, RequestStatus status);

    default CardBlockRequest findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса на блокировку не существует"));
    }
}
