package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/bookings")
public class TimeController {
    private final ProjectService projectService;
    private final ResourceService resourceService;
    private final TimeService timeService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;


    @Autowired
    private TimeController(ProjectService projectService,
            ResourceService resourceService,
            TimeService timeService,
            AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository){
        this.resourceService = resourceService;
        this.projectService = projectService;
        this.timeService = timeService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }

    @GetMapping("/{workspaceId}")
    public List<ResourceDTO> sendListResource(@PathVariable Integer workspaceId){
        return resourceService.getResourcesOfWorkSpace(workspaceId);
    }
//
//    @GetMapping("/{workspaceId}/add")
//    public List<ProjectDTO> sendListProject(@PathVariable Integer workspaceId, @RequestHeader Integer idAccount){
//        return projectService.getAllProjects(idAccount, workspaceId);
//    }

    @PostMapping("/{workspaceId}/{resourceId}")
    public MessageResponse addNewBooking(@RequestBody TimeRequest timeRequest,
                              @PathVariable Integer resourceId,
                              @PathVariable Integer workspaceId,
                              @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            timeRequest.setResourceId(resourceId);
            return timeService.addNewBooking(timeRequest);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @PutMapping("/{workspaceId}/{timeId}")
    public MessageResponse editBooking(@RequestBody TimeRequest timeRequest,
                            @PathVariable Integer timeId,
                            @PathVariable Integer workspaceId,
                            @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            return timeService.updateBooking(timeRequest, timeId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping("/{workspaceId}/{timeId}")
    public MessageResponse deleteBooking(@PathVariable Integer timeId,
                              @PathVariable Integer workspaceId,
                              @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            return timeService.deleteBooking(timeId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }
}
