package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;


import java.util.List;

public interface WorkspaceService {


    List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id);
    WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO ,Integer id);
    void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount);
    void deleteWorkspaceByIdWorkspace(Integer idWorkspace,Integer idAccount);
    List<WorkspaceDTO> searchWorkspaceByName(String name);


}
