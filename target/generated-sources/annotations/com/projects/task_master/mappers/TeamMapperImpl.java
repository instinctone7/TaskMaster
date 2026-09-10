package com.projects.task_master.mappers;

import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.dtos.responses.UserResponseDto;
import com.projects.task_master.entities.Team;
import com.projects.task_master.entities.User;
import com.projects.task_master.enums.Roles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-08T15:13:41+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class TeamMapperImpl implements TeamMapper {

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
        List<UserResponseDto> members = null;

        id = team.getId();
        name = team.getName();
        description = team.getDescription();
        createdBy = team.getCreatedBy();
        ownersId = team.getOwnersId();
        members = userListToUserResponseDtoList( team.getMembers() );

        Instant createdAt = null;

        TeamResponse teamResponse = new TeamResponse( id, name, description, createdBy, ownersId, createdAt, members );

        return teamResponse;
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

    protected List<UserResponseDto> userListToUserResponseDtoList(List<User> list) {
        if ( list == null ) {
            return null;
        }

        List<UserResponseDto> list1 = new ArrayList<UserResponseDto>( list.size() );
        for ( User user : list ) {
            list1.add( userToUserResponseDto( user ) );
        }

        return list1;
    }
}
