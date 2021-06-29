package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.AlreadyBoundException;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/project")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;

    @Autowired
    public ProjectController(ProjectService projectService,
                             ProjectRepository projectRepository,
                             AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.accoutWorkspaceRoleRepository=accoutWorkspaceRoleRepository;
    }

    @GetMapping(value = "/{idWorkspace}")
    private List<ProjectDTO> getAll(@RequestHeader("AccountId") Integer idAccount, @PathVariable Integer idWorkspace) {
        return projectService.getAllProjects(idAccount, idWorkspace);
    }
    @PostMapping(value = "/{idWorkspace}")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                                  @PathVariable Integer idWorkspace,
                                                  @RequestBody ProjectRequest projectRequest){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity =accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            if (projectRepository.findByName(projectRequest.getName()).isPresent()) {
                return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
            } else {


                projectService.createProject(projectRequest, idAccount, idWorkspace);
                if (projectRepository.findByName(projectRequest.getName()).isPresent()) {
                    return new MessageResponse(ResponseMessage.CREATE_SUCCESS,Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.CREATE_FAIL,Status.FAIL.getCode());
            }
        }else return new MessageResponse(ResponseMessage.ROLE,Status.FAIL.getCode());
    }
    @GetMapping(value = "pm/{idWorkspace}")
    private List<ResourceResponse> getAllProjectManager(@RequestHeader("AccountId") Integer idAccount,
                                                        @PathVariable Integer idWorkspace){
        return projectService.getListPM(idAccount,idWorkspace);
    }
    @GetMapping(value = "am/{idWorkspace}")
    private List<ResourceResponse> getAllAccountManager(@RequestHeader("AccountId") Integer idAccount,
                                                        @PathVariable Integer idWorkspace){
        return projectService.getListAM(idAccount,idWorkspace);
    }
}
