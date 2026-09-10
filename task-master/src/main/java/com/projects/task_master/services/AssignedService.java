package com.projects.task_master.services;

import com.projects.task_master.dtos.responses.TaskAssignmentResponse;
import com.projects.task_master.dtos.responses.TaskResponse;
import com.projects.task_master.entities.TaskAssignment;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.TaskStatus;
import com.projects.task_master.exceptions.TaskNotFound;
import com.projects.task_master.mappers.AssignmentMapper;
import com.projects.task_master.mappers.TaskMapper;
import com.projects.task_master.repositories.TaskAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AssignedService {


    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskMapper taskMapper;
    private final int PAGE_SIZE = 10;
    private final AssignmentMapper assignmentMapper;
    public AssignedService(TaskAssignmentRepository taskAssignmentRepository,
                           TaskMapper taskMapper,AssignmentMapper assignmentMapper) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskMapper = taskMapper;
        this.assignmentMapper = assignmentMapper;
    }


    public Page<TaskResponse> getAssignedTasks(
            User user,
            TaskStatus status,
            int page,
            String sort
    ) {
        Sort.Direction sortDirection = sort.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by(sortDirection, "task.dueDate")
        );

        Page<TaskAssignment> assignments = status != null
                ? taskAssignmentRepository
                .findByUserAndTask_Status(user, status, pageable)
                : taskAssignmentRepository
                .findByUser(user, pageable);

        return assignments.map(
                assignment -> taskMapper.TaskToTaskResponse(assignment.getTask())
        );
    }

    public TaskAssignmentResponse updateTaskStatus(User user, String taskName, TaskStatus status) {
        TaskAssignment assignment = taskAssignmentRepository.findByUserAndTask_Title(user, taskName);
        if (assignment == null) {
            throw new TaskNotFound("Task " + taskName + " doesnt exist");
        }
        assignment.setStatus(status);
        assignment.setUser(user);
        assignment.setCompletedAt(status == TaskStatus.COMPLETED ? Instant.now() : null);
        taskAssignmentRepository.save(assignment);
        return assignmentMapper.toTaskAssignmentResponse(assignment);
    }

    public Page<TaskAssignmentResponse> getAllAssignedTasks(User user, TaskStatus status, int page, String sort) {
        Sort.Direction sortDirection = sort.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,PAGE_SIZE,
                Sort.by(sortDirection, "task.dueDate")
        );

        Page<TaskAssignment> assignments = status != null
                ? taskAssignmentRepository
                .findByUserAndTask_Status(user, status, pageable)
                : taskAssignmentRepository
                .findByUser(user, pageable);
        return assignments.map(
                assignmentMapper::toTaskAssignmentResponse
        );
    }
}
