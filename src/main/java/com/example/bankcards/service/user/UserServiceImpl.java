package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.UserCreateDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserViewDto createUser(UserCreateDto createDto) {
        User user = mapper.toEntity(createDto);
        repository.save(user);
        return mapper.toViewDto(user);
    }

    @Override
    @Transactional
    public UserViewDto getUser(Long id) {
        User user = repository.findByIdOrElseThrow(id);
        return mapper.toViewDto(user);
    }
}
