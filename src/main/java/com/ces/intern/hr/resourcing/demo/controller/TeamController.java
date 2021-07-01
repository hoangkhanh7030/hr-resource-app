package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/team")
public class TeamController {
    private final TeamService teamService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ResourceRepository resourceRepository;
    @Autowired
    public TeamController(TeamService teamService,
                          AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                          ResourceRepository resourceRepository) {
        this.teamService = teamService;
        this.accoutWorkspaceRoleRepository=accoutWorkspaceRoleRepository;
        this.resourceRepository=resourceRepository;
    }

    @GetMapping(value = "")
    private List<TeamDTO> getAll(){
        return teamService.getAll();
    }
    @PutMapping("/idWorkspace/{idTeam}/{idResource}")
    private MessageResponse addTeamToMember(@RequestHeader("AccountId") Integer idAccount,
                                            @PathVariable Integer idWorkspace,
                                            @PathVariable Integer idTeam,
                                            @PathVariable Integer idResource){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity =accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            teamService.addResourceToTeam(idTeam,idResource);
            if (resourceRepository.findByIdTeamandIdResource(idTeam,idResource).isPresent()){
                return new MessageResponse(ResponseMessage.ADD_SUCCESS, Status.SUCCESS.getCode());
            }else return new MessageResponse(ResponseMessage.ADD_FAIL,Status.FAIL.getCode());
        }else return new MessageResponse(ResponseMessage.ROLE,Status.FAIL.getCode());
    }
}
