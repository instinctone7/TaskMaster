package com.projects.task_master.dtos.responses;


import java.time.LocalDate;

public record TaskResponse(
        String title,
        String description,
LocalDate dueDate,
        UserResponseDto owner) {
}
