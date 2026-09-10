package com.projects.task_master.repositories;

import com.projects.task_master.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);

    Team findByName(String name);
}
