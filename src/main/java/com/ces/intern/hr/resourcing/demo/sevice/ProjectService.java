package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.http.request.ActivateRequest;
import com.ces.intern.hr.resourcing.demo.http.request.PageSizeRequest;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects(Integer idWorkspace, PageSizeRequest pageSizeRequest);

    void createProject(ProjectRequest projectRequest,Integer idAccount, Integer idWorkspace);
    void updateProject(ProjectRequest projectRequest,Integer idAccount,Integer idProject);
    void deleteProject(Integer idProject);
    List<ProjectDTO> searchParameter(String name,String clientName,Boolean isActivate,Integer idWorkspace,PageSizeRequest pageSizeRequest);



}
