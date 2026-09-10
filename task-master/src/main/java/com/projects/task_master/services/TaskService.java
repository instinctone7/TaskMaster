package com.projects.task_master.services;

import com.projects.task_master.dtos.requests.TaskRequest;
import com.projects.task_master.dtos.requests.TaskUpdateReq;
import com.projects.task_master.dtos.responses.TaskAssignmentResponse;
import com.projects.task_master.dtos.responses.TaskResponse;
import com.projects.task_master.entities.*;
import com.projects.task_master.enums.TaskStatus;
import com.projects.task_master.exceptions.NonAuthorized;
import com.projects.task_master.exceptions.TaskNotFound;
import com.projects.task_master.exceptions.TeamDoesNotExist;
import com.projects.task_master.exceptions.UserNotFound;
import com.projects.task_master.mappers.AssignmentMapper;
import com.projects.task_master.mappers.TaskMapper;
import com.projects.task_master.repositories.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final int PAGE_SIZE = 10;
    private final AssignmentMapper assignmentMapper;
    private final TeamRepository teamRepository;
    private final CommentRepository commentRepository;

    public TaskService(UserRepository userRepository, TaskRepository taskRepository, TaskMapper taskMapper, TaskAssignmentRepository taskAssignmentRepository, AssignmentMapper assignmentMapper, TeamRepository teamRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.assignmentMapper = assignmentMapper;
        this.teamRepository = teamRepository;
        this.commentRepository = commentRepository;
    }


    public TaskResponse createTask(@Valid TaskRequest request, User user) {
        Task task = Task.builder()
                        .title(request.title())
                                .description(request.description())
                                        .dueDate(request.dueDate())
                                                .owner(user)
                                                        .status(TaskStatus.TODO)
                                                                .build();
        taskRepository.save(task);
        return taskMapper.TaskToTaskResponse(task);
    }
    public TaskResponse updateTask(User user,@Valid TaskUpdateReq req,String originalTitle){
        Task task = taskRepository.findByTitle(originalTitle);
        if (task == null){
            throw new TaskNotFound("Task "+originalTitle+" doesnt exist");
        }
        if(!task.getOwner().getId().equals(user.getId())){
            throw new NonAuthorized("You have to be the owner to update the task");
        }
        if (req.title() != null) task.setTitle(req.title());
        if (req.description() != null) task.setDescription(req.description());
        if (req.dueDate() != null) task.setDueDate(req.dueDate());
        taskRepository.save(task);
        return taskMapper.TaskToTaskResponse(task);
    }

    public TaskAssignmentResponse assign(User user, String taskName,
                                         Long TeamId,String userEmail) {
            Task task = taskRepository.findByTitle(taskName);
            if (task == null) {
                throw new TaskNotFound("Task " + taskName + " doesnt exist");
            }
            Team team = teamRepository.findById(TeamId).orElseThrow(
                    () -> new TeamDoesNotExist("Team with id " + TeamId + " doesnt exist")
            );
            if (!task.getOwner().getId().equals(user.getId())) {
                throw new NonAuthorized("You have to be the owner to assign the task");
            }
            User assignee = userRepository.findByEmail(userEmail);
            if (assignee == null) {
                throw new UserNotFound("User with email " + userEmail + " doesnt exist");
            }
            if (!team.getMembers().contains(assignee)) {
                throw new NonAuthorized("User with email " + userEmail + " is not a member of the team");
            }
            if (taskAssignmentRepository.existsByTaskAndUser(task, assignee)) {
                throw new NonAuthorized("Task is already assigned to this user");
            }
        TaskAssignment taskAssignment = TaskAssignment.builder()
                .task(task)
                .user(assignee)
                .status(TaskStatus.TODO)
                .assignedAt(Instant.now())
                .build();
            taskAssignmentRepository.save(taskAssignment);
            return assignmentMapper.toTaskAssignmentResponse(taskAssignment);
    }

    public String deleteTask(User user, String taskName) {
        Task task = taskRepository.findByTitle(taskName);
        if (task == null) {
            throw new TaskNotFound("Task " + taskName + " doesnt exist");
        }
        if (!task.getOwner().getId().equals(user.getId())) {
            throw new NonAuthorized("You have to be the owner to delete the task");
        }
        taskRepository.delete(task);
        return "Task deleted successfully";
    }

    public TaskResponse getTask(User user, String taskName) {
        Task task = taskRepository.findByTitle(taskName);
        if (task == null) {
            throw new TaskNotFound("Task " + taskName + " doesnt exist");
        }
        if (!task.getOwner().getId().equals(user.getId())) {
            throw new NonAuthorized("You have to be the owner to view the task");
        }
        return taskMapper.TaskToTaskResponse(task);
    }

    public Page<TaskResponse> getAllTasks(
            User user,
            TaskStatus status,
            int page,
            String sort
    ) {
        if (user == null) {
            throw new UserNotFound("User not found");
        }
        if (page < 0) {
            page = 0;
        }
        if (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc")) {
            sort = "asc";
        }
        if (status != null && !status.equals(TaskStatus.TODO) && !status.equals(TaskStatus.IN_PROGRESS) && !status.equals(TaskStatus.COMPLETED)) {
            status = TaskStatus.TODO;
        }
        Sort.Direction direction = sort.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(direction, "dueDate")
        );

        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findAllByOwnerAndStatus(user, status, pageable);
        } else {
            tasks = taskRepository.findAllByOwner(user, pageable);
        }
        return tasks.map(taskMapper::TaskToTaskResponse);
    }

    public String addComment(User user, Long userId, Long taskId, String comment) {
        User forUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("User with id " + userId + " doesnt exist"));
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new TaskNotFound("Task with id " + taskId + " doesnt exist"));
        if (!task.getOwner().getId().equals(user.getId()) && !forUser.getId().equals(user.getId())) {
            throw new NonAuthorized("You have to be the owner or the user to add a comment");
        }
        Comments comments = Comments.builder()
                .comment(comment)
                .task(task)
                .written(forUser)
                .build();
        commentRepository.save(comments);
        return "Comment added successfully";
    }
}
