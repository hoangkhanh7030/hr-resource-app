package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;

import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
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
        this.workspaceRepository=workspaceRepository;
    }





    @GetMapping(value = "/getAll")
    private List<WorkspaceDTO> getWorkspaceByIdAccount(@RequestHeader(value = "AccountId") Integer idAccount){


        return workspaceService.getWorkspaceByIdAccount(idAccount);
    }

    @PostMapping(value = "/created")
    private WorkspaceDTO createWorkspaceByIdAccount(@RequestHeader("AccountId") Integer idAccount,@RequestBody WorkspaceDTO workspaceDTO){
        return workspaceService.createdWorkspaceByIdAccount(workspaceDTO,idAccount);
    }

    @PutMapping(value = "/update/{idWorkspace}")
    private ResponseEntity<Object> updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestBody WorkspaceDTO workspaceDTO,
                                                                @RequestHeader("AccountId") Integer idAccount){
        workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO,idWorkspace,idAccount);
        if (workspaceRepository.findByName(workspaceDTO.getName()).isPresent()){
           return ResponseEntity.ok("update Successing");
        }else return ResponseEntity.unprocessableEntity().body("update Fail");
    }
    @DeleteMapping(value = "/delete/{idWorkspace}")
    private ResponseEntity<String> deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                                @RequestHeader("AccountId") Integer idAccount){
        workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace,idAccount);
        if(workspaceRepository.findById(idWorkspace).isPresent()){
           return ResponseEntity.unprocessableEntity().body("Delete Fail");
        }else return ResponseEntity.ok("Delete Successing");
    }

    @GetMapping(value = "/search/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name){
        return workspaceService.searchWorkspaceByName(name);
    }



}
