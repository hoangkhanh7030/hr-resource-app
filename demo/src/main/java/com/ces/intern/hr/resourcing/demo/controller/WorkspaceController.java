package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;

import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService,
                               WorkspaceRepository workspaceRepository) {

        this.workspaceService = workspaceService;
        this.workspaceRepository = workspaceRepository;
    }


    @GetMapping(value = "/getAll")
    private List<WorkspaceResponse> getWorkspaceByIdAccount(@RequestHeader(value = "AccountId") Integer idAccount) {


        return workspaceService.getAllWorkspaceByIdAccount(idAccount);
    }

    @GetMapping(value = "/getOne/{idWorkspace}")
    private WorkspaceDTO getWorkspace(@RequestHeader("AccountId") Integer idAccount, @PathVariable Integer idWorkspace) {
        return workspaceService.getWorkspace(idWorkspace, idAccount);
    }

    @PostMapping(value = "/created")
    private ResponseEntity<Object> createWorkspaceByIdAccount(@RequestHeader("AccountId") Integer idAccount, @RequestBody WorkspaceDTO workspaceDTO) {


        workspaceService.createdWorkspaceByIdAccount(workspaceDTO, idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()) {
            return ResponseEntity.ok(ResponseMessage.CREATE_SUCCESS);

        }
        return ResponseEntity.unprocessableEntity().body(ResponseMessage.CREATE_FAIL);
    }

    @PutMapping(value = "/update/{idWorkspace}")
    private ResponseEntity<Object> updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestBody WorkspaceDTO workspaceDTO,
                                                                @RequestHeader("AccountId") Integer idAccount) {
        workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO, idWorkspace, idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()) {
            return ResponseEntity.ok(ResponseMessage.UPDATE_SUCCESS);

        }
        return ResponseEntity.unprocessableEntity().body(ResponseMessage.UPDATE_FAIL);
    }

    @DeleteMapping(value = "/delete/{idWorkspace}")
    private ResponseEntity<Object> deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestHeader("AccountId") Integer idAccount) {
        workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace, idAccount);
        if (workspaceRepository.findById(idWorkspace).isPresent()) {
            return ResponseEntity.unprocessableEntity().body(ResponseMessage.DELETE_FAIL);

        }
        return ResponseEntity.ok(ResponseMessage.DELETE_SUCCESS);
    }

    @GetMapping(value = "/search/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name) {
        return workspaceService.searchWorkspaceByName(name);
    }


}
