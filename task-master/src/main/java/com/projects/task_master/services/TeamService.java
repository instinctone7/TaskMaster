package com.projects.task_master.services;

import com.projects.task_master.dtos.requests.TeamRequest;
import com.projects.task_master.dtos.responses.InviteList;
import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.entities.Invite;
import com.projects.task_master.entities.Team;
import com.projects.task_master.entities.User;
import com.projects.task_master.exceptions.InvalidTokenException;
import com.projects.task_master.exceptions.TeamDoesNotExist;
import com.projects.task_master.exceptions.TeamDuplicate;
import com.projects.task_master.mappers.InviteMapper;
import com.projects.task_master.mappers.TeamMapper;
import com.projects.task_master.repositories.InviteRepository;
import com.projects.task_master.repositories.TeamRepository;
import com.projects.task_master.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final int PAGE_SIZE = 10;
    private final InviteMapper inviteMapper;

    public TeamService(TeamMapper teamMapper, TeamRepository teamRepository, UserRepository userRepository, InviteRepository inviteRepository, InviteMapper inviteMapper) {
        this.teamMapper = teamMapper;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.inviteRepository = inviteRepository;
        this.inviteMapper = inviteMapper;
    }

    public TeamResponse createTeam(User user, TeamRequest request) {
        if (teamRepository.existsByName(request.name())) {
            throw new TeamDuplicate("Team with the same name already exists");
        }
        Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .createdBy(user.getName())
                .ownersId(user.getId())
                .build();
        Team savedTeam = teamRepository.save(team);
        return teamMapper.toTeamResponse(savedTeam);
    }

    public void inviteMember(User user, String teamName, String email) {
        User receiver = userRepository.findByEmail(email);
        if (user.getEmail().equals(email)) {
            throw new IllegalArgumentException("You cannot invite yourself to the team");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("User with email " + email + " does not exist");
        }
        Team team = teamRepository.findByName(teamName);
        if (team == null) {
            throw new TeamDoesNotExist("Team with name " + teamName + " does not exist");
        }
        if (team.getMembers() != null && team.getMembers().contains(receiver)) {
            throw new IllegalArgumentException("User with email " + email + " is already a member of the team");
        }

        // Ensure team has an id (persisted). If it's not, save it explicitly and use persisted id.
        if (team.getId() == null) {
            team = teamRepository.save(team);
        }

        if (team.getId() == null) {
            throw new IllegalStateException("Unable to determine team id for team: " + teamName);
        }

        Invite invite = Invite.builder()
                .email(email)
                .token(java.util.UUID.randomUUID().toString())
                .teamId(team.getId())
                .accepted(false) 
                .build();
        inviteRepository.save(invite);
    }

    public void acceptInvite(User user, String token) {
        Invite invite = inviteRepository.findByToken(token);
        if (invite == null) {
            throw new InvalidTokenException("Invalid invitation token");
        }
        if (!invite.getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("This invitation is not for the authenticated user");
        }
        Team team = teamRepository.findById(invite.getTeamId()).orElseThrow(() -> new
                TeamDoesNotExist("Team with id " + invite.getTeamId() + " does not exist"));
        team.getMembers().add(user);
        teamRepository.save(team);
        invite.setAccepted(true);
        inviteRepository.save(invite);
    }

    public TeamResponse getTeam(User user, String teamName) {
        Team team = teamRepository.findByName(teamName);
        if (team == null) {
            throw new TeamDoesNotExist("Team with name " + teamName + " does not exist");
        }
        if (!team.getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not a member of the team");
        }
        return teamMapper.toTeamResponse(team);
    }

    public Page<InviteList> getInvites(User user, String sortBy, String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(direction, sortBy));
        return inviteRepository.findByEmail(user.getEmail(), pageable).map(inviteMapper::toInviteList);
    }
}
