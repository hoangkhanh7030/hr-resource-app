package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.workspace.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class WorkspaceController {


    private final WorkspaceService workspaceService;


    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {

        this.workspaceService = workspaceService;
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

        return workspaceService.createdWorkspaceByIdAccount(workspaceDTO,idAccount);
    }

    @PutMapping(value = "/{idWorkspace}")
    private MessageResponse updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                         @RequestBody WorkspaceDTO workspaceDTO,
                                                         @RequestHeader("AccountId") Integer idAccount) {
        return workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO, idWorkspace, idAccount);
    }

    @DeleteMapping(value = "/{idWorkspace}")
    private MessageResponse deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                         @RequestHeader("AccountId") Integer idAccount) {
        return workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace, idAccount);
    }

    @GetMapping(value = "/search/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name) {
        return workspaceService.searchWorkspaceByName(name);
    }


}
