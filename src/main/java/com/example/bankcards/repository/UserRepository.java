package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    default User findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    Optional<User> findByUsername(String name);

    default User findByUsernameOrElseThrow(String name) {
        return findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);

    default Long findIdByUsernameOrElseThrow(String name) {
        return findIdByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
