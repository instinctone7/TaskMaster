package com.projects.task_master.dtos.responses;

import java.time.Instant;

public record TeamNotificationResponse(
        Long id,
        Long teamId,
        String teamName,
        Long senderId,
        String senderName,
        String senderEmail,
        Long recipientId,
        String recipientName,
        String recipientEmail,
        String message,
        Instant createdAt,
        Instant readAt,
        boolean read
) {
}

