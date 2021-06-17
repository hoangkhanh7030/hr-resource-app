package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }




    @GetMapping(value = "/getWorkspaces")
    private List<WorkspaceDTO> getWorkspaces(){
        return workspaceService.getWorkspaces();
    }
    @PostMapping(value = "/createWorkspace")
    private WorkspaceDTO createWorkspace(@RequestBody WorkspaceDTO workspaceDTO){
        return workspaceService.createWorkspace(workspaceDTO);
    }

    @GetMapping(value = "/getWorkspaceByName")
    private WorkspaceDTO getWorkspaceByName(@RequestParam String name){
        return workspaceService.getWorkspaceByName(name);
    }
    @PutMapping(value = "/updateWorkspace")
    private WorkspaceDTO updateWorkspace(@RequestParam String name,@RequestBody WorkspaceDTO workspaceDTO){
        return workspaceService.updateWorkspace(workspaceDTO,name);
    }
    @GetMapping(value = "/getWorkspaces/{idAccount}")
    private List<WorkspaceDTO> getWorkspaceByIdAccount(@PathVariable Integer idAccount){
        return workspaceService.getWorkspaceByIdAccount(idAccount);
    }
//    @PostMapping(value = "/create")
//    private ResponseEntity<Object> create(@RequestBody WorkspaceEntity model){
//        return workspaceService.create(model);
//    }
//    @DeleteMapping(value = "/delete")
//    private ResponseEntity<Object> delete(@RequestParam String name){
//        return workspaceService.deleteWorkspace(name);
//    }


}
