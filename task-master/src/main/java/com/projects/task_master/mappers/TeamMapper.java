package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.entities.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamResponse toTeamResponse(Team team);
}
