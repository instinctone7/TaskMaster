package com.projects.task_master.repositories;

import com.projects.task_master.entities.Team;
import com.projects.task_master.entities.TeamNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamNotificationRepository extends JpaRepository<TeamNotification, Long> {
    List<TeamNotification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<TeamNotification> findByRecipientIdAndTeamIdOrderByCreatedAtDesc(Long recipientId, Long teamId);

    List<TeamNotification> findByRecipientIdAndReadAtIsNullOrderByCreatedAtDesc(Long recipientId);

    List<TeamNotification> findByRecipientIdAndTeamIdAndReadAtIsNullOrderByCreatedAtDesc(Long recipientId, Long teamId);

    Optional<TeamNotification> findByIdAndRecipientId(Long id, Long recipientId);
}


