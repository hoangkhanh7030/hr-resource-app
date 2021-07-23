package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.TeamResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping(value = "/{idWorkspace}/teamAndPosition")
    private List<TeamResponse> getTeams(@PathVariable Integer idWorkspace){
        return teamService.getTeams(idWorkspace);
    }

    @PutMapping("/{idWorkspace}/team/{idTeam}/{idResource}")
    private MessageResponse addTeamToMember(@RequestHeader("AccountId") Integer idAccount,
                                            @PathVariable Integer idWorkspace,
                                            @PathVariable Integer idTeam,
                                            @PathVariable Integer idResource) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            teamService.addResourceToTeam(idTeam, idResource);
            if (resourceRepository.findByPositionEntity_TeamEntity_IdAndId(idTeam, idResource).isPresent()) {
                return new MessageResponse(ResponseMessage.ADD_SUCCESS, Status.SUCCESS.getCode());
            } else return new MessageResponse(ResponseMessage.ADD_FAIL, Status.FAIL.getCode());
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping("/{idWorkspace}/team/{idTeam}")
    private MessageResponse deleteTeam(@RequestHeader("AccountId") Integer idAccount,
                                       @PathVariable Integer idWorkspace,
                                       @PathVariable Integer idTeam) {
            teamService.deleteTeam(idTeam);
            if (teamRepository.findById(idTeam).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());

    }

    @PutMapping("/{idWorkspace}/team/{idTeam}")
    private MessageResponse renameTeam(@RequestHeader("AccountId") Integer idAccount,
                                       @PathVariable Integer idWorkspace,
                                       @PathVariable Integer idTeam,
                                       @RequestBody String name) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            if ( name == null||name.isEmpty() ) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                teamService.renameTeam(idTeam, name);
                if (teamRepository.findByNameAndidWorkspace(name, idWorkspace).isPresent()) {
                    return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
            }


        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }


    @PostMapping(value = "/{idWorkspace}/team")
    private MessageResponse created(@PathVariable Integer idWorkspace,
                            @RequestBody List<TeamRequest> teamRequests){
        if (teamRequests.isEmpty()){
            teamRepository.deleteAll();
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS,Status.SUCCESS.getCode());
        }else {
            for (TeamRequest teamRequest:teamRequests){
                if (teamRequest.getName().isEmpty()){
                    return new MessageResponse(ResponseMessage.IS_EMPTY,Status.FAIL.getCode());
                }else {
                    for (PositionRequest positionRequest:teamRequest.getPositions()){
                        if (positionRequest.getName().isEmpty()) {
                            return new MessageResponse(ResponseMessage.IS_EMPTY,Status.FAIL.getCode());
                        }

                    }
                }
            }
            teamService.created(teamRequests,idWorkspace);
            return new MessageResponse(ResponseMessage.CREATE_SUCCESS,Status.SUCCESS.getCode());
        }
    }


    private static boolean listEquals(List<TeamDTO> list1, List<TeamDTO> list2) {
        if (list1.size() != list2.size())
            return true;

        for (TeamDTO teamDTO : list1) {
            if (!list2.contains(teamDTO))
                return true;
        }
        return false;
    }

}
