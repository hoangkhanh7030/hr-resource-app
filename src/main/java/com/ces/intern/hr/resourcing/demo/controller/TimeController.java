package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/booking")
public class TimeController {
    private final ProjectService projectService;
    private final ResourceService resourceService;
    private final TimeService timeService;


    @Autowired
    private TimeController(ProjectService projectService,
            ResourceService resourceService,
            TimeService timeService){
        this.resourceService = resourceService;
        this.projectService = projectService;
        this.timeService = timeService;
    }

    @GetMapping("/{workspaceId}/add")
    public List<ResourceDTO> sendListResource(@PathVariable Integer workspaceId){
        return resourceService.getResourcesOfWorkSpace(workspaceId);
    }

    @GetMapping("/{workspaceId}/add")
    public List<ProjectDTO> sendListProject(@PathVariable Integer workspaceId, @RequestHeader Integer idAccount){
        return projectService.getAllProjects(idAccount, workspaceId);
    }

    @PostMapping("/{resourceId}/")
    public void addNewBooking(@RequestBody TimeRequest timeRequest,
                              @PathVariable Integer resourceId){
        timeRequest.setResourceId(resourceId);
        timeService.addNewBooking(timeRequest);
    }

    @PutMapping("/{timeId}")
    public void editBooking(@RequestBody TimeRequest timeRequest,
                            @PathVariable Integer timeId){
        //timeRequest
    }
}
