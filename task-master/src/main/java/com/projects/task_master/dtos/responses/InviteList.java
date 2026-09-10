package com.projects.task_master.dtos.responses;

public record InviteList(
        String email,
        String token,
        Long teamId
) {
}
