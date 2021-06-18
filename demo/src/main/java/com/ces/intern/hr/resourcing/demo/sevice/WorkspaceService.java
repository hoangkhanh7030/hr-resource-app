package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkspaceService {


    List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id);
    WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO ,Integer id);
    WorkspaceDTO updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO,Integer idWorkspace,Integer idAccount);
    void deleteWorkspaceByIdWorkspace(Integer id);
    List<WorkspaceDTO> searchWorkspaceByName(String name);
}
