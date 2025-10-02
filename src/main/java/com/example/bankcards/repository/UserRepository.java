package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    default User findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    Optional<User> findByName(String name);

    default User findByNameOrElseThrow(String name) {
        return findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
