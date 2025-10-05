package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public Map<String, String> createUser(UserAuthenticationDto authenticationDto) {
        if (repository.existsByUsername(authenticationDto.username())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        User user = mapper.toEntity(authenticationDto);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setRole("ROLE_USER");
        repository.save(user);

        String token = jwtUtil.generateToken(authenticationDto.username());
        log.info("Создан пользователь с именем {}", user.getUsername());
        return Map.of("jwt-token", token);
    }

    @Override
    public Map<String, String> performLogin(UserAuthenticationDto authenticationDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDto.username(), authenticationDto.password());
        authenticationManager.authenticate(authenticationToken);
        String token = jwtUtil.generateToken(authenticationDto.username());
        return Map.of("jwt-token", token);
    }

    @Override
    @Transactional
    public UserViewDto getUser(Long id) {
        User user = repository.findByIdOrElseThrow(id);
        return mapper.toViewDto(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        if (!repository.existsByUsername("admin")) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_ADMIN");
            repository.save(user);
        }
    }
}
