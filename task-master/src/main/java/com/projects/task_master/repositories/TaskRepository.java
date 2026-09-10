package com.projects.task_master.repositories;

import com.projects.task_master.entities.Task;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Task findByTitle(String title);

    Page<Task> findAllByOwner(User owner, Pageable pageable);

    Page<Task> findAllByOwnerAndStatus(
            User owner,
            TaskStatus status,
            Pageable pageable
    );
}
