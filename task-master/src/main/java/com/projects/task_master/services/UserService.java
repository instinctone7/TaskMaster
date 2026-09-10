package com.projects.task_master.services;

import com.projects.task_master.dtos.requests.UpdateUserDto;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.User;
import com.projects.task_master.exceptions.UserNotFound;
import com.projects.task_master.mappers.UserMapper;
import com.projects.task_master.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDto userProfile(User user){
            return userMapper.FromUsertoUserResponseDto(user);
    }

    public UserResponseDto updateUser(User user, UpdateUserDto updateUserDto) {
        if (updateUserDto.name() != null){
            user.setName(updateUserDto.name());
        }
        if (updateUserDto.bio() != null){
            user.setBio(updateUserDto.bio());
        }
        userRepository.save(user);
        return userMapper.FromUsertoUserResponseDto(user);
    }

    public void deleteUser(User user,Long id) {
        User retard = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFound("User with the id "+id+" is not found")
                );
        userRepository.delete(retard);
    }
}
