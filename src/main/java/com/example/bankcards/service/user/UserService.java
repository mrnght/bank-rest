package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;

import java.util.Map;

public interface UserService {

    Map<String, String> createUser(UserAuthenticationDto authenticationDto);

    Map<String, String> performLogin(UserAuthenticationDto authenticationDto);

    UserViewDto getUser(Long id);
}
