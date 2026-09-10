package com.projects.task_master.entities;


import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Invite{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
    private Long teamId;
    private boolean accepted;
    private Instant invitedAt;

    @PrePersist
    protected void onCreate() {
        invitedAt = Instant.now();
    }
}
