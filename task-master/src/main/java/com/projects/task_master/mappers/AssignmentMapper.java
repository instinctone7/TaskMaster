package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TaskAssignmentResponse;
import com.projects.task_master.entities.TaskAssignment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    TaskAssignmentResponse toTaskAssignmentResponse(TaskAssignment assignment);
}
