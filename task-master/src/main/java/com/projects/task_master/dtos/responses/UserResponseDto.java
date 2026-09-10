package com.projects.task_master.dtos.responses;

import com.projects.task_master.enums.Roles;

public record UserResponseDto(
    String name,
    String email,
    String bio,
    Roles role
) {
}
