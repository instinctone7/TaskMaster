package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TaskAssignmentResponse;
import com.projects.task_master.entities.TaskAssignment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-14T16:13:07+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class AssignmentMapperImpl implements AssignmentMapper {

    @Override
    public TaskAssignmentResponse toTaskAssignmentResponse(TaskAssignment assignment) {
        if ( assignment == null ) {
            return null;
        }

        Long id = null;
        String status = null;
        String assignedAt = null;
        String completedAt = null;

        id = assignment.getId();
        if ( assignment.getStatus() != null ) {
            status = assignment.getStatus().name();
        }
        if ( assignment.getAssignedAt() != null ) {
            assignedAt = assignment.getAssignedAt().toString();
        }
        if ( assignment.getCompletedAt() != null ) {
            completedAt = assignment.getCompletedAt().toString();
        }

        String taskTitle = null;
        String taskDescription = null;
        String assignedTo = null;

        TaskAssignmentResponse taskAssignmentResponse = new TaskAssignmentResponse( id, taskTitle, taskDescription, assignedTo, status, assignedAt, completedAt );

        return taskAssignmentResponse;
    }
}
