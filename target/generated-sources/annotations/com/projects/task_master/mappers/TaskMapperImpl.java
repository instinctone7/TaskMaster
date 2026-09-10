package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TaskResponse;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.Task;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.Roles;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-08T15:13:41+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskResponse TaskToTaskResponse(Task task) {
        if ( task == null ) {
            return null;
        }

        String title = null;
        String description = null;
        LocalDate dueDate = null;
        UserResponseDto owner = null;

        title = task.getTitle();
        description = task.getDescription();
        dueDate = task.getDueDate();
        owner = userToUserResponseDto( task.getOwner() );

        TaskResponse taskResponse = new TaskResponse( title, description, dueDate, owner );

        return taskResponse;
    }

    protected UserResponseDto userToUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        String name = null;
        String email = null;
        String bio = null;
        Roles role = null;

        name = user.getName();
        email = user.getEmail();
        bio = user.getBio();
        role = user.getRole();

        UserResponseDto userResponseDto = new UserResponseDto( name, email, bio, role );

        return userResponseDto;
    }
}
