package com.projects.task_master.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record TeamNotificationRequest(
        @NotBlank(message = "Notification message is required") String message
) {
}

