package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.AlreadyBoundException;
import java.util.List;

@RestController
@RequestMapping(value = "project")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectController(ProjectService projectService,
                             ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @GetMapping(value = "/getAll/{idWorkspace}")
    private List<ProjectDTO> getAll(@RequestHeader("AccountId") Integer idAccount, @PathVariable Integer idWorkspace) {
        return projectService.getAllProjects(idAccount, idWorkspace);
    }
    @PostMapping(value = "/create/{idWorkspace}")
    private ResponseEntity<Object> createdProject(@RequestHeader("AccountId") Integer idAccount,
                                                  @PathVariable Integer idWorkspace,
                                                  @RequestBody ProjectRequest projectRequest){
         projectService.createProject(projectRequest,idAccount,idWorkspace);
        if (projectRepository.findByName(projectRequest.getName()).isPresent()){
            return ResponseEntity.ok(ResponseMessage.CREATE_SUCCESS);
        }return ResponseEntity.unprocessableEntity().body(ResponseMessage.CREATE_FAIL);
    }
    @GetMapping(value = "getAllPM/{idWorkspace}")
    private List<ResourceResponse> getAllProjectManager(@RequestHeader("AccountId") Integer idAccount,
                                                        @PathVariable Integer idWorkspace){
        return projectService.getListPM(idAccount,idWorkspace);
    }
    @GetMapping(value = "getAllAM/{idWorkspace}")
    private List<ResourceResponse> getAllAccountManager(@RequestHeader("AccountId") Integer idAccount,
                                                        @PathVariable Integer idWorkspace){
        return projectService.getListAM(idAccount,idWorkspace);
    }
}
