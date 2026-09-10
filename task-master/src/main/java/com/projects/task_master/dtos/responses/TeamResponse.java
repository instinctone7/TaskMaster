package com.projects.task_master.dtos.responses;


import java.time.Instant;
import java.util.List;

public record TeamResponse(
    Long id,
    String name,
    String description,
    String createdBy,
    Long ownersId,
    Instant createdAt,
    List<UserResponseDto> members
) {
}
