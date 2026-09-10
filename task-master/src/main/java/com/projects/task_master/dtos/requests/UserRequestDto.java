package com.projects.task_master.dtos.requests;

import com.projects.task_master.enums.Roles;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserRequestDto(
    @NotBlank
    @Length(min = 3, max = 50)
    String name,
    @NotBlank
    @Email
    String email,
    @Length(max = 200)
    String bio,
    @NotBlank
    @Length(min = 8, max = 32)
    String password,
    @NotBlank
    @Enumerated(EnumType.STRING)
    Roles role
) {}
