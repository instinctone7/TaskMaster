package com.projects.task_master.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank
        String title,
        String description,
        @NotNull
        LocalDate dueDate) {
}
