package com.projects.task_master.controllers;

import com.projects.task_master.dtos.requests.TaskRequest;
import com.projects.task_master.dtos.requests.TaskUpdateReq;
import com.projects.task_master.dtos.responses.TaskAssignmentResponse;
import com.projects.task_master.dtos.responses.TaskResponse;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.TaskStatus;
import com.projects.task_master.handlers.ApiResponse;
import com.projects.task_master.services.AssignedService;
import com.projects.task_master.services.TaskService;
import com.projects.task_master.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AssignedService assignedService;

    public TaskController(TaskService taskService, UserService userService, AssignedService assignedService) {
        this.taskService = taskService;
        this.assignedService = assignedService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@RequestBody @Valid TaskRequest request, @AuthenticationPrincipal User user) {
        TaskResponse response = taskService.createTask(request, user);
        return ResponseEntity.ok(ApiResponse.success("Task is created", response));
    }

    @PutMapping("/update/{title}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@AuthenticationPrincipal User user, @RequestBody TaskUpdateReq taskUpdateReq,
                                                                @PathVariable String title) {
        TaskResponse response = taskService.updateTask(user, taskUpdateReq, title);
        return ResponseEntity.ok(ApiResponse.success("Updated the task", response));
    }

    @DeleteMapping("/delete/{taskName}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@AuthenticationPrincipal User user,
                                                          @PathVariable String taskName) {
        String response = taskService.deleteTask(user, taskName);
        return ResponseEntity.ok(ApiResponse.success("Task deleted", response));
    }

    @GetMapping("/get/{taskName}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@AuthenticationPrincipal User user,
                                                             @PathVariable String taskName) {
        TaskResponse response = taskService.getTask(user, taskName);

        return ResponseEntity.ok(ApiResponse.success("Task retrieved", response));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "asc",required = false) String sort
    ) {
        Page<TaskResponse> response =
                taskService.getAllTasks(user, status, page, sort);

        return ResponseEntity.ok(
                ApiResponse.success("All tasks retrieved", response)
        );
    }

    @PostMapping("/assign/{taskName}/{teamId}/{userEmail}")
    public ResponseEntity<ApiResponse<TaskAssignmentResponse>> assignTask(@AuthenticationPrincipal User user,
                                                                @PathVariable Long teamId,
                                                                @PathVariable String taskName,
                                                                @PathVariable String userEmail) {
        TaskAssignmentResponse response = taskService.assign(user, taskName,teamId, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Task assigned", response));
    }

    @GetMapping("/assigned")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAssignedTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        Page<TaskResponse> response =
                assignedService.getAssignedTasks(user, status, page, sort);

        return ResponseEntity.ok(
                ApiResponse.success("All assigned tasks retrieved", response)
        );
    }

    @PostMapping("/updateStatus/{taskName}/{status}")
    public ResponseEntity<ApiResponse<TaskAssignmentResponse>> updateTaskStatus(@AuthenticationPrincipal User user,
                                                                                @PathVariable String taskName,
                                                                                @PathVariable TaskStatus status) {
        TaskAssignmentResponse response = assignedService.updateTaskStatus(user, taskName, status);
        return ResponseEntity.ok(ApiResponse.success("Task status updated", response));
    }

    @GetMapping("/getAllAssigned")
    public ResponseEntity<ApiResponse<Page<TaskAssignmentResponse>>> getAllAssignedTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        Page<TaskAssignmentResponse> response =
                assignedService.getAllAssignedTasks(user, status, page, sort);
        return ResponseEntity.ok(
                ApiResponse.success("All assigned tasks retrieved", response)
        );
    }
    @PostMapping("/comment/{userId}/{taskId}")
    public ResponseEntity<ApiResponse<String>> addComment(@AuthenticationPrincipal User user,
                                                          @PathVariable Long userId,
                                                          @PathVariable Long taskId,
                                                          @RequestBody String comment) {
        String response = taskService.addComment(user, userId, taskId, comment);
        return ResponseEntity.ok(ApiResponse.success("Comment added", response));
    }
}
