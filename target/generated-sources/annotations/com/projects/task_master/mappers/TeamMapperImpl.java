package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.Team;
import com.projects.task_master.entities.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-14T16:13:07+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class TeamMapperImpl implements TeamMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public TeamResponse toTeamResponse(Team team) {
        if ( team == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        String createdBy = null;
        Long ownersId = null;
        Instant createdAt = null;
        List<UserResponseDto> members = null;

        id = team.getId();
        name = team.getName();
        description = team.getDescription();
        createdBy = team.getCreatedBy();
        ownersId = team.getOwnersId();
        createdAt = team.getCreatedAt();
        members = userListToUserResponseDtoList( team.getMembers() );

        TeamResponse teamResponse = new TeamResponse( id, name, description, createdBy, ownersId, createdAt, members );

        return teamResponse;
    }

    protected List<UserResponseDto> userListToUserResponseDtoList(List<User> list) {
        if ( list == null ) {
            return null;
        }

        List<UserResponseDto> list1 = new ArrayList<UserResponseDto>( list.size() );
        for ( User user : list ) {
            list1.add( userMapper.FromUsertoUserResponseDto( user ) );
        }

        return list1;
    }
}
