package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;


import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService,
                               WorkspaceRepository workspaceRepository) {

        this.workspaceService = workspaceService;
        this.workspaceRepository = workspaceRepository;
    }

    @GetMapping(value = "/{idWorkspace}")
    private WorkspaceDTO getWorkspace(@RequestHeader("AccountId") Integer idAccount, @PathVariable Integer idWorkspace) {
        return workspaceService.getWorkspace(idWorkspace, idAccount);
    }

    @GetMapping(value = "")
    private List<WorkspaceResponse> getAllWorkspace(@RequestHeader(value = "AccountId") Integer idAccount) {


        return workspaceService.getAllWorkspaceByIdAccount(idAccount);
    }



    @PostMapping(value = "")
    private MessageResponse createWorkspaceByIdAccount(@RequestHeader("AccountId") Integer idAccount, @RequestBody WorkspaceDTO workspaceDTO) {


        workspaceService.createdWorkspaceByIdAccount(workspaceDTO, idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()) {
            return new MessageResponse(ResponseMessage.CREATE_SUCCESS);

        }
        return new MessageResponse(ResponseMessage.CREATE_FAIL);

    }

    @PutMapping(value = "/{idWorkspace}")
    private MessageResponse updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestBody WorkspaceDTO workspaceDTO,
                                                                @RequestHeader("AccountId") Integer idAccount) {
        workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO, idWorkspace, idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()) {
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS);

        }
        return new MessageResponse(ResponseMessage.UPDATE_FAIL);

    }

    @DeleteMapping(value = "/{idWorkspace}")
    private MessageResponse deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestHeader("AccountId") Integer idAccount) {
        workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace, idAccount);
        if (workspaceRepository.findById(idWorkspace).isPresent()) {
            return new MessageResponse(ResponseMessage.DELETE_FAIL);

        }
        return new MessageResponse(ResponseMessage.DELETE_SUCCESS);

    }

    @GetMapping(value = "/search/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name) {
        return workspaceService.searchWorkspaceByName(name);
    }


}
