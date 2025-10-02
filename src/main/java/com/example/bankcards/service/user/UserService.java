package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserCreateDto;
import com.example.bankcards.dto.user.UserViewDto;

public interface UserService {
    UserViewDto createUser(UserCreateDto createDto);

    UserViewDto getUser(Long id);
}
