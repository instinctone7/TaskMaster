package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.InviteList;
import com.projects.task_master.entities.Invite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InviteMapper {
    InviteList toInviteList(Invite invite);
}
