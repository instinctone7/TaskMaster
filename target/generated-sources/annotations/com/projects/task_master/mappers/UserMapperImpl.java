package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.Roles;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-08T15:13:41+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto FromUsertoUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        String name = null;
        String email = null;
        String bio = null;
        Roles role = null;

        name = user.getName();
        email = user.getEmail();
        bio = user.getBio();
        role = user.getRole();

        UserResponseDto userResponseDto = new UserResponseDto( name, email, bio, role );

        return userResponseDto;
    }
}
