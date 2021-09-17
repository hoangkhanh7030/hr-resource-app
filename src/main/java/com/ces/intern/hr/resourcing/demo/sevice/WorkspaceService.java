package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.workspace.WorkspaceResponse;



import java.util.List;

public interface WorkspaceService {


    List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer idAccount);
    MessageResponse createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO,Integer idAccount);
    MessageResponse updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount);
    MessageResponse deleteWorkspaceByIdWorkspace(Integer idWorkspace,Integer idAccount);
    List<WorkspaceDTO> searchWorkspaceByName(String name);
    WorkspaceDTO getWorkspace(Integer idWorkspace,Integer idAccount);




}
