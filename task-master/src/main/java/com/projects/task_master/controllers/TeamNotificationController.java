package com.projects.task_master.controllers;

import com.projects.task_master.dtos.requests.TeamNotificationRequest;
import com.projects.task_master.dtos.responses.TeamNotificationResponse;
import com.projects.task_master.entities.User;
import com.projects.task_master.handlers.ApiResponse;
import com.projects.task_master.services.TeamNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/teams/notifications")
public class TeamNotificationController {

    private final TeamNotificationService notificationService;

    public TeamNotificationController(TeamNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal User user) {
        return notificationService.subscribe(user);
    }

    @PostMapping("/send/{teamId}/{recipientEmail}")
    public ResponseEntity<ApiResponse<TeamNotificationResponse>> sendNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long teamId,
            @PathVariable String recipientEmail,
            @Valid @RequestBody TeamNotificationRequest request
    ) {
        TeamNotificationResponse response = notificationService.sendNotification(user, teamId, recipientEmail, request);
        return ResponseEntity.ok(ApiResponse.success("Notification sent", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamNotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long teamId,
            @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        List<TeamNotificationResponse> response = notificationService.getNotifications(user, teamId, unreadOnly);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", response));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<TeamNotificationResponse>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId
    ) {
        TeamNotificationResponse response = notificationService.markAsRead(user, notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }
}

