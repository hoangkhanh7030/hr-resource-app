package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ActivateRequest;
import com.ces.intern.hr.resourcing.demo.http.request.PageSizeRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(value = "api/v1/workspaces")
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
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }

    @GetMapping(value = "/{idWorkspace}/project")
    private List<ProjectDTO> getAll(@PathVariable Integer idWorkspace,
                                       @RequestBody PageSizeRequest pageRequest) {
        return projectService.getAllProjects(idWorkspace, pageRequest);
    }

    @PostMapping(value = "/{idWorkspace}/project")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                           @PathVariable Integer idWorkspace,
                                           @RequestBody ProjectRequest projectRequest) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {

            if (projectRepository.findByName(projectRequest.getName()).isPresent()) {

                return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
            } else {
                if (projectRequest.getName().isEmpty() || projectRequest.getColor().isEmpty()) {
                    return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                }
                projectService.createProject(projectRequest, idAccount, idWorkspace);
                if (projectRepository.findByName(projectRequest.getName()).isPresent()) {
                    return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @PutMapping(value = "/{idWorkspace}/project/{idProject}")
    private MessageResponse updateProject(@RequestHeader("AccountId") Integer idAccount,
                                          @PathVariable Integer idWorkspace,
                                          @PathVariable Integer idProject,
                                          @RequestBody ProjectRequest projectRequest) {
            if (projectRequest.getName().isEmpty() || projectRequest.getColor().isEmpty()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                projectService.updateProject(projectRequest, idAccount, idWorkspace, idProject);
            }
            if (projectRepository.findByName(projectRequest.getName()).isPresent()) {
                return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
            } else return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());

    }

    @PutMapping(value = "/activate/{idWorkspace}/project/{idProject}")
    private MessageResponse activateProject(@PathVariable Integer idWorkspace,
                                            @PathVariable Integer idProject,
                                            @RequestBody ActivateRequest activateRequest) {

        projectService.Activate(activateRequest, idWorkspace, idProject);
        if (projectRepository.findByIdAndIsActivate(activateRequest.isActivate(), idProject).isPresent()) {
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());

    }

    @GetMapping(value = "/{idWorkspace}/pm/project")
    private List<ResourceResponse> getAllProjectManager(@PathVariable Integer idWorkspace) {
        return projectService.getListPM(idWorkspace);
    }

    @GetMapping(value = "/{idWorkspace}/am/project")
    private List<ResourceResponse> getAllAccountManager(@PathVariable Integer idWorkspace) {
        return projectService.getListAM(idWorkspace);
    }

    @GetMapping(value = "{idWorkspace}/project/search/{name}")
    private List<ProjectDTO> search(@PathVariable String name,
                                    @PathVariable Integer idWorkspace,
                                    @RequestBody PageSizeRequest pageSizeRequest) {
        return projectService.search(name,idWorkspace,pageSizeRequest);

    }
}
