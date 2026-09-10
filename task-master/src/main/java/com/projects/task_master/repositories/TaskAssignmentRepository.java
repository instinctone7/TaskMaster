package com.projects.task_master.repositories;

import com.projects.task_master.entities.Task;
import com.projects.task_master.entities.TaskAssignment;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.TaskStatus;
import com.projects.task_master.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    TaskAssignment findByTask_IdAndUser_Id(Long taskId, Long userId);

    Page<TaskAssignment> findByUser(
            User user,
            Pageable pageable
    );

    Page<TaskAssignment> findByUserAndTask_Status(
            User user,
            TaskStatus status,
            Pageable pageable
    );

    TaskAssignment findByUserAndTask_Title(User user, String taskName);

    boolean existsByTaskAndUser(Task task, User assignee);
}
