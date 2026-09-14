package com.projects.task_master.services;

import com.projects.task_master.dtos.requests.TeamNotificationRequest;
import com.projects.task_master.dtos.responses.TeamNotificationResponse;
import com.projects.task_master.entities.Team;
import com.projects.task_master.entities.TeamNotification;
import com.projects.task_master.entities.User;
import com.projects.task_master.exceptions.NotificationNotFound;
import com.projects.task_master.exceptions.NonAuthorized;
import com.projects.task_master.exceptions.TeamDoesNotExist;
import com.projects.task_master.exceptions.UserNotFound;
import com.projects.task_master.repositories.TeamNotificationRepository;
import com.projects.task_master.repositories.TeamRepository;
import com.projects.task_master.repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TeamNotificationService {

    private static final long SSE_TIMEOUT = 30L * 60L * 1000L;

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamNotificationRepository notificationRepository;
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public TeamNotificationService(UserRepository userRepository,
                                   TeamRepository teamRepository,
                                   TeamNotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public SseEmitter subscribe(User user) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.computeIfAbsent(user.getId(), key -> new CopyOnWriteArrayList<>());
        userEmitters.add(emitter);

        Runnable cleanup = () -> removeEmitter(user.getId(), emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            cleanup.run();
            emitter.complete();
        });
        emitter.onError(throwable -> cleanup.run());

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to team notifications", MediaType.TEXT_PLAIN));
        } catch (IOException ex) {
            cleanup.run();
            emitter.completeWithError(ex);
        }

        return emitter;
    }

    @Transactional
    public TeamNotificationResponse sendNotification(User sender,
                                                     Long teamId,
                                                     String recipientEmail,
                                                     TeamNotificationRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamDoesNotExist("Team with id " + teamId + " does not exist"));
        User recipient = userRepository.findByEmail(recipientEmail);
        if (recipient == null) {
            throw new UserNotFound("User with email " + recipientEmail + " does not exist");
        }
        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("You cannot send a notification to yourself");
        }
        ensureTeamParticipant(team, sender);
        ensureTeamParticipant(team, recipient);

        TeamNotification notification = TeamNotification.builder()
                .team(team)
                .sender(sender)
                .recipient(recipient)
                .message(request.message().trim())
                .createdAt(Instant.now())
                .build();

        TeamNotification savedNotification = notificationRepository.save(notification);
        TeamNotificationResponse response = toResponse(savedNotification);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    pushToRecipient(response);
                }
            });
        } else {
            pushToRecipient(response);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<TeamNotificationResponse> getNotifications(User user, Long teamId, boolean unreadOnly) {
        if (teamId == null) {
            List<TeamNotification> notifications = unreadOnly
                    ? notificationRepository.findByRecipientIdAndReadAtIsNullOrderByCreatedAtDesc(user.getId())
                    : notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
            return notifications.stream().map(this::toResponse).toList();
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamDoesNotExist("Team with id " + teamId + " does not exist"));
        ensureTeamParticipant(team, user);

        List<TeamNotification> notifications = unreadOnly
                ? notificationRepository.findByRecipientIdAndTeamIdAndReadAtIsNullOrderByCreatedAtDesc(user.getId(), team.getId())
                : notificationRepository.findByRecipientIdAndTeamIdOrderByCreatedAtDesc(user.getId(), team.getId());
        return notifications.stream().map(this::toResponse).toList();
    }

    @Transactional
    public TeamNotificationResponse markAsRead(User user, Long notificationId) {
        TeamNotification notification = notificationRepository.findByIdAndRecipientId(notificationId, user.getId())
                .orElseThrow(() -> new NotificationNotFound("Notification with id " + notificationId + " does not exist"));
        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
            notification = notificationRepository.save(notification);
        }
        return toResponse(notification);
    }

    private void pushToRecipient(TeamNotificationResponse response) {
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.get(response.recipientId());
        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("team-notification")
                        .id(String.valueOf(response.id()))
                        .data(response));
            } catch (IOException ex) {
                removeEmitter(response.recipientId(), emitter);
                emitter.completeWithError(ex);
            }
        }
    }

    private void ensureTeamParticipant(Team team, User user) {
        boolean isOwner = Objects.equals(team.getOwnersId(), user.getId());
        boolean isMember = team.getMembers() != null && team.getMembers().stream().anyMatch(member -> Objects.equals(member.getId(), user.getId()));
        if (!isOwner && !isMember) {
            throw new NonAuthorized("User is not a member of the team");
        }
    }

    private TeamNotificationResponse toResponse(TeamNotification notification) {
        return new TeamNotificationResponse(
                notification.getId(),
                notification.getTeam().getId(),
                notification.getTeam().getName(),
                notification.getSender().getId(),
                notification.getSender().getName(),
                notification.getSender().getEmail(),
                notification.getRecipient().getId(),
                notification.getRecipient().getName(),
                notification.getRecipient().getEmail(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getReadAt() != null
        );
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }
}



