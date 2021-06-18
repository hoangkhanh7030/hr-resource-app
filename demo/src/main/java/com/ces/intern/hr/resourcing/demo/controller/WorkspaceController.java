package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;

import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    @Autowired
    public WorkspaceController(WorkspaceService workspaceService,
                               WorkspaceRepository workspaceRepository) {

        this.workspaceService = workspaceService;
        this.workspaceRepository=workspaceRepository;
    }





    @GetMapping(value = "/getWorkspaces/{idAccount}")
    private List<WorkspaceDTO> getWorkspaceByIdAccount(@PathVariable Integer idAccount){
        return workspaceService.getWorkspaceByIdAccount(idAccount);
    }

    @PostMapping(value = "/createdWorkspace/{idAccount}")
    private WorkspaceDTO createWorkspaceByIdAccount(@PathVariable Integer idAccount,@RequestBody WorkspaceDTO workspaceDTO){
        return workspaceService.createdWorkspaceByIdAccount(workspaceDTO,idAccount);
    }

    @PutMapping(value = "/updateWorkspace/{idWorkspace}/{idAccount}")
    private ResponseEntity<Object> updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestBody WorkspaceDTO workspaceDTO,
                                                                @PathVariable Integer idAccount){
        workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO,idWorkspace,idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()){
           return ResponseEntity.ok("update Successing");
        }else return ResponseEntity.unprocessableEntity().body("update Fail");
    }
    @DeleteMapping(value = "/deleteWorkspaceByIdWorkspace/{idWorkspace}")
    private ResponseEntity<String> deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace){
        workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace);
        if(workspaceRepository.findById(idWorkspace).isPresent()){
           return ResponseEntity.unprocessableEntity().body("Delete Fail");
        }else return ResponseEntity.ok("Delete Successing");
    }

    @GetMapping(value = "/searchWorkspace/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name){
        return workspaceService.searchWorkspaceByName(name);
    }



}
