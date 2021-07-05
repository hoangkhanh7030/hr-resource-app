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
    List<ResourceResponse> getListPM(Integer idWorkspace);
    List<ResourceResponse> getListAM(Integer idWorkspace);
    void updateProject(ProjectRequest projectRequest,Integer idAccount,Integer idWorkspace,Integer idProject);
    List<ProjectDTO> search(String name,Integer idWorkspace,PageSizeRequest pageSizeRequest);
    void Activate(ActivateRequest activateRequest,Integer idWorkspace,Integer idProject);



}
