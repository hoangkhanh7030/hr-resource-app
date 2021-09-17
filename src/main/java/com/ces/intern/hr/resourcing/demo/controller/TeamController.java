package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.TeamResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class TeamController {
    private final TeamService teamService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ResourceRepository resourceRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamController(TeamService teamService,
                          AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                          ResourceRepository resourceRepository,
                          TeamRepository teamRepository,
                          ModelMapper modelMapper) {
        this.teamService = teamService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.resourceRepository = resourceRepository;
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/{idWorkspace}/team")
    private List<TeamDTO> getAll(@PathVariable Integer idWorkspace) {
        return teamService.getAll(idWorkspace);
    }

    @GetMapping(value = "/{idWorkspace}/teams")
    private List<TeamResponse> getTeams(@PathVariable Integer idWorkspace) {
        return teamService.getTeams(idWorkspace);
    }


    @DeleteMapping("/team/{idTeam}")
    private MessageResponse deleteTeam(@PathVariable Integer idTeam) {
        return teamService.deleteTeam(idTeam);
    }

    @PutMapping("/{idWorkspace}/team/{idTeam}")
    private MessageResponse renameTeam(@PathVariable Integer idWorkspace,
                                       @PathVariable Integer idTeam,
                                       @RequestBody TeamDTO teamDTO) {

        return teamService.renameTeam(idWorkspace, idTeam, teamDTO);
    }


    @PostMapping(value = "/{idWorkspace}/teams")
    private MessageResponse created(@PathVariable Integer idWorkspace,
                                    @RequestBody List<TeamRequest> teamRequests) {
        return teamService.created(teamRequests, idWorkspace);
    }

    @PutMapping(value = "/{idWorkspace}/teams")
    private MessageResponse update(@PathVariable Integer idWorkspace,
                                   @RequestBody List<TeamRequest> teamRequests) {
        return teamService.update(teamRequests, idWorkspace);

    }


}
