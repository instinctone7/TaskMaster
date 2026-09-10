package com.projects.task_master.repositories;

import com.projects.task_master.entities.Invite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Invite findByToken(String token);

    Page<Invite> findByEmail(String email, Pageable pageable);
}
