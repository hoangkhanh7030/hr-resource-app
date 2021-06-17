package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkspaceService {
    List<WorkspaceDTO> getWorkspaces();
    WorkspaceDTO createWorkspace(WorkspaceDTO workspaceDTO);
    WorkspaceDTO getWorkspaceByName(String name);
    WorkspaceDTO updateWorkspace(WorkspaceDTO workspaceDTO,String name);

    List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id);
    WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO ,Integer id);
//    ResponseEntity<Object> create(WorkspaceEntity model);
//    ResponseEntity<Object> deleteWorkspace(String name);
}
