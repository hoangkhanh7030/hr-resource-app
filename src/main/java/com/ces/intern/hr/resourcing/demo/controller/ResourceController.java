package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/workspaces")
public class ResourceController {
    private final ResourceService resourceService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;


    @Autowired
    private ResourceController(ResourceService resourceService,
                               AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository
    ) {
        this.resourceService = resourceService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }


    @GetMapping("/{workspaceId}/resources")
    public List<ResourceDTO> showResourceList(@PathVariable Integer workspaceId,
                                              @RequestParam Integer page,
                                              @RequestParam Integer size) {
        return resourceService.getResourcesOfWorkSpace(workspaceId, page, size);
    }

    @GetMapping("/{workspaceId}/resources/filterByTeam")
    public List<ResourceDTO> showResourceByTeam(@PathVariable Integer workspaceId,
                                                @RequestParam String teamName,
                                                @RequestParam Integer page,
                                                @RequestParam Integer size) {
        return resourceService.filterByTeam(workspaceId, teamName, page, size);
    }

    @GetMapping("/{workspaceId}/resources/filterByPosition")
    public List<ResourceDTO> showResourceByPosition(@PathVariable Integer workspaceId,
                                                    @RequestParam String posName,
                                                    @RequestParam Integer page,
                                                    @RequestParam Integer size) {
        return resourceService.filterByPosition(workspaceId, posName, page, size);
    }

    @GetMapping("/{workspaceId}/resources/filterByTeamAndPosition")
    public List<ResourceDTO> showResourceByTeamAndPosition(@PathVariable Integer workspaceId,
                                                           @RequestParam String teamName,
                                                           @RequestParam String posName,
                                                           @RequestParam Integer page,
                                                           @RequestParam Integer size) {
        return resourceService.filterByTeamAndPosition(workspaceId, teamName, posName, page, size);
    }

    @PostMapping("/{workspaceId}/resources")
    public MessageResponse createResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            //resourceService.addNewResource(resourceRequest, workspaceId, accountId);
            return resourceService.addNewResource(resourceRequest, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @GetMapping("/{workspaceId}/resources/search")
    public List<ResourceDTO> searchResource(@RequestParam String name,
                                            @RequestParam String posName,
                                            @RequestParam String teamName,
                                            @PathVariable Integer workspaceId,
                                            @RequestParam Integer page,
                                            @RequestParam Integer size) {
        return resourceService.searchByName(name, posName, teamName, workspaceId, page, size);
    }

    @GetMapping("/{workspaceId}/resources/{resourceId}")
    public ResourceDTO getOneResourceInfo(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId) {
        return resourceService.getResourceInfo(resourceId, workspaceId);
    }

    @PutMapping("/{workspaceId}/resources/{resourceId}")
    public MessageResponse updateResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @PathVariable Integer resourceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return resourceService.updateResource(resourceRequest, resourceId, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping("/{workspaceId}/resources/{resourceId}")
    public MessageResponse deleteResource(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return resourceService.deleteResource(resourceId, workspaceId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }


    @GetMapping("/changeTeam")
    public void changeTeamForm() {

    }
}
