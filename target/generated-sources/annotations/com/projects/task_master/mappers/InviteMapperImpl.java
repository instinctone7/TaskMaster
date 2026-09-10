package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.InviteList;
import com.projects.task_master.entities.Invite;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-09T00:03:18+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class InviteMapperImpl implements InviteMapper {

    @Override
    public InviteList toInviteList(Invite invite) {
        if ( invite == null ) {
            return null;
        }

        String email = null;
        String token = null;

        email = invite.getEmail();
        token = invite.getToken();

        Long teamName = null;

        InviteList inviteList = new InviteList( email, token, teamName );

        return inviteList;
    }
}
