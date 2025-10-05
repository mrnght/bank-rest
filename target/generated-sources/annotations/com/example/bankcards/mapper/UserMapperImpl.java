package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.UserAuthenticationDto;
import com.example.bankcards.dto.user.UserViewDto;
import com.example.bankcards.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-05T14:27:22+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserAuthenticationDto createDto) {
        if ( createDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( createDto.username() );
        user.password( createDto.password() );

        return user.build();
    }

    @Override
    public UserViewDto toViewDto(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String role = null;

        id = user.getId();
        username = user.getUsername();
        role = user.getRole();

        UserViewDto userViewDto = new UserViewDto( id, username, role );

        return userViewDto;
    }
}
