package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;

import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects(Integer idWorkspace, int page ,int size);

    void createProject(ProjectRequest projectRequest,Integer idAccount, Integer idWorkspace);
    void updateProject(ProjectRequest projectRequest,Integer idAccount,Integer idProject);
    void deleteProject(Integer idProject);
    List<ProjectDTO> searchParameter(String name,Boolean isActivate,Integer idWorkspace,int page,int size);
    List<ProjectDTO> sortProject(int page,int size,Integer idWorkspace,String name,String type);



}
