package com.projects.task_master.controllers;

import com.projects.task_master.dtos.requests.TeamRequest;
import com.projects.task_master.dtos.responses.InviteList;
import com.projects.task_master.dtos.responses.TeamResponse;
import com.projects.task_master.entities.User;
import com.projects.task_master.handlers.ApiResponse;
import com.projects.task_master.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@AuthenticationPrincipal User user, @RequestBody TeamRequest request) {
        TeamResponse response = teamService.createTeam(user, request);
        return ResponseEntity.ok(ApiResponse.success("Team has been created Successfully", response));
    }

    @PostMapping("/invite/{teamName}/{email}")
    public ResponseEntity<ApiResponse<String>> inviteMember(@AuthenticationPrincipal User user,
                                                            @PathVariable String teamName,
                                                            @PathVariable String email) {
        teamService.inviteMember(user, teamName, email);
        return ResponseEntity.ok(ApiResponse.success("Invitation sent successful", "Sent to " + email));
    }

    @PostMapping("/accept/{token}")
    public ResponseEntity<ApiResponse<String>> acceptInvite(@AuthenticationPrincipal User user,
                                                            @PathVariable String token) {
        teamService.acceptInvite(user, token);
        return ResponseEntity.ok(ApiResponse.success("Invitation accepted", null));
    }

    @GetMapping("/get/{teamName}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(@AuthenticationPrincipal User user,
                                                             @PathVariable String teamName) {
        TeamResponse response = teamService.getTeam(user, teamName);
        return ResponseEntity.ok(ApiResponse.success("Team retrieved", response));
    }
    @GetMapping("/getInvites")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<InviteList>>> getInvites(@AuthenticationPrincipal User user,
                                                                                                    @RequestParam(required = false) String teamName,
                                                                                                    @RequestParam(required = false,defaultValue = "id") String sortBy,
                                                                                                    @RequestParam(required = false,defaultValue = "asc") String sortOrder){
        org.springframework.data.domain.Page<com.projects.task_master.dtos.responses.InviteList> response = teamService.getInvites(user,sortBy,sortOrder);
        return ResponseEntity.ok(ApiResponse.success("Invites retrieved", response));
    }

}