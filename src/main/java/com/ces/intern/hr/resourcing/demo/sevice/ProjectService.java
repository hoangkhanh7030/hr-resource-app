package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.project.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects(Integer idWorkspace, int page, int size);

    MessageResponse createdProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace);

    MessageResponse updateProject(ProjectRequest projectRequest, Integer idAccount, Integer idProject,Integer idWorkspace);

    MessageResponse deleteProject(Integer idProject,Integer idWorkspace);

    List<ProjectDTO> searchParameter(String name, Boolean isActivate, Integer idWorkspace, int page, int size);

    List<ProjectDTO> sortProject(int page, int size, Integer idWorkspace, String sortColumn, String type);

    MessageResponse isActivate(Integer idProject,Integer idWorkspace);

    List<ProjectDTO> sortAndSearchListProject(Integer idWorkspace, int page, int size, String nameSearch, String sortColumn, String type);

    List<ProjectDTO> sortAndSearchProjectListWithStatusFilter(Integer idWorkspace, int page, int size, Boolean isActivate, String nameSearch, String sortColumn, String type);

    List<ProjectDTO> searchParameterWithoutStatus(String name, Integer idWorkspace, int page, int size);

    List<ProjectDTO> getAll(Integer idWorkspace, String searchName);

    NumberSizeResponse ListProject(Integer idWorkspace,int page,int size,String sortName,
                                   String searchName,String type,String isActivate);

//    MessageResponse export(HttpServletResponse response,Integer idWorkspace);

    Response importCSV(Integer idAccount, Integer idWorkspace, MultipartFile file);
}
