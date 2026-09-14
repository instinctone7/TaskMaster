package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.entities.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TeamMapper {
    TeamResponse toTeamResponse(Team team);
}
