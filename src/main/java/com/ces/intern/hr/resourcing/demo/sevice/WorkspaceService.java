package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;


import java.util.List;

public interface WorkspaceService {


    List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer id);
    void createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id);
    void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount);
    void deleteWorkspaceByIdWorkspace(Integer idWorkspace,Integer idAccount);
    List<WorkspaceDTO> searchWorkspaceByName(String name);
    WorkspaceDTO getWorkspace(Integer idWorkspace,Integer idAccount);
//    WorkspaceDTO getWorkspaceWithToDay(Integer idWorkspace,Integer idAccount);



}
