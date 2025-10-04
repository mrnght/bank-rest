package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.util.JWTUtil;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserAuthenticationDto authenticationDto) {
        return ResponseEntity.ok(service.createUser(authenticationDto));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody UserAuthenticationDto authenticationDto) {
        return ResponseEntity.ok(service.performLogin(authenticationDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserViewDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }
}
