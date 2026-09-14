package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto FromUsertoUserResponseDto(User user);
}
