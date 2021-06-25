package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ActivateRequest;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects(Integer idWorkspace);

    void createProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace);
    List<ResourceResponse> getListPM(Integer idAccount,Integer idWorkspace);
    List<ResourceResponse> getListAM(Integer idAccount,Integer idWorkspace);
    void updateProject(ProjectRequest projectRequest,Integer idAccount,Integer idWorkspace,Integer idProject);
    List<ProjectDTO> search(String name);
    void Activate(ActivateRequest activateRequest,Integer idWorkspace,Integer idProject);


}
