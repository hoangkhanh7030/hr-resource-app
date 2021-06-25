package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;

import java.rmi.AlreadyBoundException;
import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects(Integer idAccount,Integer idWorkspace);
    void createProject(ProjectRequest projectRequest,Integer idAccount,Integer idWorkspace);
    List<ResourceResponse> getListPM(Integer idAccount,Integer idWorkspace);
    List<ResourceResponse> getListAM(Integer idAccount,Integer idWorkspace);


}
