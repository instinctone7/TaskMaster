package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TaskResponse;
import com.projects.task_master.entities.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponse TaskToTaskResponse(Task task);
}
