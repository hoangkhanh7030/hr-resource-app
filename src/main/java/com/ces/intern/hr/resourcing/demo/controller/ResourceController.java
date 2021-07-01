package com.ces.intern.hr.resourcing.demo.controller;
import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceConverter resourceConverter;
    private final AccountService accountService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;


    @Autowired
    private ResourceController(ResourceService resourceService,
                                                  AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                                  ResourceConverter resourceConverter,
                                                  AccountService accountService){
        this.resourceService = resourceService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.resourceConverter = resourceConverter;
        this.accountService = accountService;
    }


    @GetMapping("/{workspaceId}")
    public List<ResourceDTO> showResourceList(@PathVariable Integer workspaceId){
        return resourceService.getResourcesOfWorkSpace(workspaceId);
    }



    @PostMapping("/{workspaceId}")
    public MessageResponse createResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            //resourceService.addNewResource(resourceRequest, workspaceId, accountId);
            return resourceService.addNewResource(resourceRequest, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE,Status.FAIL.getCode());
    }

    @GetMapping("/{workspaceId}/search")
    public List<ResourceDTO> searchResource(@RequestParam String name, @PathVariable Integer workspaceId){
        return resourceService.searchByName(name, workspaceId);
    }

    @GetMapping("/{workspaceId}/{resourceId}")
    public ResourceDTO getOneResourceInfo(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId){
        return resourceService.getResourceInfo(resourceId, workspaceId);
    }

    @PutMapping("/{workspaceId}/{resourceId}")
    public MessageResponse updateResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @PathVariable Integer resourceId,
                                          @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            return resourceService.updateResource(resourceRequest, resourceId, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE,Status.FAIL.getCode());
    }

    @DeleteMapping("/{workspaceId}/{resourceId}")
    public MessageResponse deleteResource(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId){
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            return resourceService.deleteResource(resourceId, workspaceId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }



    @GetMapping("/changeTeam")
    public void changeTeamForm(){

    }
}
