package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserCreateDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserViewDto> createUser(@RequestBody UserCreateDto createDto) {
        return ResponseEntity.ok(service.createUser(createDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserViewDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }
}
