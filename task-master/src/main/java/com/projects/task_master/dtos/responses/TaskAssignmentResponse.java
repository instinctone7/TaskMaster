package com.projects.task_master.dtos.responses;

public record TaskAssignmentResponse(
        Long id,
        String taskTitle,
        String taskDescription,
        String assignedTo,
        String status,
        String assignedAt,
        String completedAt
) {
}
