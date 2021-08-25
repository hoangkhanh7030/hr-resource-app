package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.response.workspace.WorkspaceResponse;


import java.util.List;

public interface WorkspaceService {


    List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer idAccount);
    void createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id);
    void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount);
    void deleteWorkspaceByIdWorkspace(Integer idWorkspace,Integer idAccount);
    List<WorkspaceDTO> searchWorkspaceByName(String name);
    WorkspaceDTO getWorkspace(Integer idWorkspace,Integer idAccount);




}
