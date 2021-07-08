package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class TeamController {
    private final TeamService teamService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ResourceRepository resourceRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public TeamController(TeamService teamService,
                          AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                          ResourceRepository resourceRepository,
                          TeamRepository teamRepository) {
        this.teamService = teamService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.resourceRepository = resourceRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping(value = "/team")
    private List<TeamDTO> getAll() {
        return teamService.getAll();
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
            if (resourceRepository.findByIdTeamandIdResource(idTeam, idResource).isPresent()) {
                return new MessageResponse(ResponseMessage.ADD_SUCCESS, Status.SUCCESS.getCode());
            } else return new MessageResponse(ResponseMessage.ADD_FAIL, Status.FAIL.getCode());
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping("/{idWorkspace}/team/{idTeam}")
    private MessageResponse deleteTeam(@RequestHeader("AccountId") Integer idAccount,
                                       @PathVariable Integer idWorkspace,
                                       @PathVariable Integer idTeam) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            teamService.deleteTeam(idTeam);
            if (teamRepository.findById(idTeam).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @PutMapping("/{idWorkspace}/team/{idTeam}")
    private MessageResponse renameTeam(@RequestHeader("AccountId") Integer idAccount,
                                       @PathVariable Integer idWorkspace,
                                       @PathVariable Integer idTeam,
                                       @RequestBody String name) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            if (name.isEmpty()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                teamService.renameTeam(idTeam, name);
                if (teamRepository.findByName(name).isPresent()) {
                    return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
            }


        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }
}
