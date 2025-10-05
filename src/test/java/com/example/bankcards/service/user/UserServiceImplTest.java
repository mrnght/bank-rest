package com.example.bankcards.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapperImpl;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Spy
    private UserMapperImpl mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserAuthenticationDto authDto;

    @BeforeEach
    void setUp() {
        authDto = new UserAuthenticationDto("testUser", "testPass");
    }

    @Test
    void createUserPositive() {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(repository.save(any(User.class))).thenReturn(new User());
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-token");

        Map<String, String> result = userService.createUser(authDto);

        assertNotNull(result);
        assertEquals("jwt-token", result.get("jwt-token"));
    }

    @Test
    void createUserNegative() {
        when(repository.existsByUsername(authDto.username())).thenReturn(true);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userService.createUser(authDto));
        assertEquals("Пользователь с таким именем уже существует", e.getMessage());
        verify(repository).existsByUsername(authDto.username());
    }


    @Test
    void performLoginPositive() {
        String token = "jwt-token";
        when(jwtUtil.generateToken(authDto.username())).thenReturn(token);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password()));

        Map<String, String> result = userService.performLogin(authDto);

        assertNotNull(result);
        assertEquals(token, result.get("jwt-token"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(authDto.username());
    }

    @Test
    void performLoginNegative() {
        doThrow(new BadCredentialsException("Auth failed")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> userService.performLogin(authDto));
        assertEquals("Auth failed", ex.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void getUserPositive() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setRole("ROLE_USER");

        when(repository.findByIdOrElseThrow(userId)).thenReturn(user);

        UserViewDto userDto = new UserViewDto(user.getId(), user.getUsername(), user.getRole());
        when(mapper.toViewDto(user)).thenReturn(userDto);

        UserViewDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("testUser", result.username());
        assertEquals("ROLE_USER", result.role());

        verify(repository).findByIdOrElseThrow(userId);
        verify(mapper).toViewDto(user);
    }

    @Test
    void getUserNegative() {
        Long userId = 1L;
        when(repository.findByIdOrElseThrow(userId)).thenThrow(new IllegalArgumentException("Пользователь не найден"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userService.getUser(userId));
        assertEquals("Пользователь не найден", e.getMessage());

        verify(repository).findByIdOrElseThrow(userId);
    }
}

