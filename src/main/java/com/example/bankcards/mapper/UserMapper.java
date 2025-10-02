package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.UserCreateDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toEntity(UserCreateDto createDto);

    UserViewDto toViewDto(User user);
}
