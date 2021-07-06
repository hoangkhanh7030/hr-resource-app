package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PageSizeRequest;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.ApacheCommonsCsvUtil;
import com.ces.intern.hr.resourcing.demo.importCSV.CsvFileSerivce;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Message;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/workspaces")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ModelMapper modelMapper;
    private final CsvFileSerivce csvFileSerivce;

    @Autowired
    public ProjectController(ProjectService projectService,
                             ProjectRepository projectRepository,
                             AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                             ModelMapper modelMapper,
                             CsvFileSerivce csvFileSerivce) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;
        this.csvFileSerivce=csvFileSerivce;
    }

    @GetMapping(value = "/{idWorkspace}/project")
    private List<ProjectDTO> getAll(@PathVariable Integer idWorkspace,
                                    @RequestBody PageSizeRequest pageRequest) {
        return projectService.getAllProjects(idWorkspace, pageRequest);
    }

    @GetMapping(value = "/{idWorkspace}/project/export")
    public void exportToCSV(HttpServletResponse response,
                            @PathVariable Integer idWorkspace) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=project_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        List<ProjectEntity> projectEntityList = projectRepository.findAllByWorkspaceEntityProject_Id(idWorkspace);
        List<ProjectDTO> projectDTOList = projectEntityList.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Project ID", "Name", "Client Name", "Color", "Text Color", "Color Pattern", "Activate"};
        String[] nameMapping = {"id", "name", "clientName", "color", "textColor", "colorPattern", "isActivate"};

        csvWriter.writeHeader(csvHeader);

        for (ProjectDTO projectDTO : projectDTOList) {
            csvWriter.write(projectDTO, nameMapping);
        }

        csvWriter.close();
    }
    @PostMapping(value = "/{idWorkspace}/project/import")
    public Response importCsvFile(@RequestHeader("AccountId")Integer idAccount,
                                  @PathVariable Integer idWorkspace,
                                  @RequestParam("csvfile") MultipartFile csvfile){
        Response response = new Response();
        if (Objects.requireNonNull(csvfile.getOriginalFilename()).isEmpty()) {
            response.addMessage(new Message(csvfile.getOriginalFilename(),
                    "No selected file to upload! Please do the checking", Status.FAIL.getCode()));

            return response;
        }
        if(!ApacheCommonsCsvUtil.isCSVFile(csvfile)) {
            response.addMessage(new Message(csvfile.getOriginalFilename(), "Error: this is not a CSV file!", Status.FAIL.getCode()));
            return response;
        }


        try {

            csvFileSerivce.store(csvfile.getInputStream(),idWorkspace,idAccount);
            response.addMessage(new Message(csvfile.getOriginalFilename(), "Upload File Successfully!", Status.SUCCESS.getCode()));
        } catch (Exception e) {
            response.addMessage(new Message(csvfile.getOriginalFilename(), e.getMessage(), Status.FAIL.getCode()));
        }

        return response;
    }

    @PostMapping(value = "/{idWorkspace}/project")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                           @PathVariable Integer idWorkspace,
                                           @RequestBody ProjectRequest projectRequest) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {

            if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {

                return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
            } else {
                if (projectRequest.getName().isEmpty() || projectRequest.getColor().isEmpty()
                        || projectRequest.getClientName().isEmpty() || projectRequest.getTextColor().isEmpty()
                        || projectRequest.getColorPattern().isEmpty()) {
                    return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                }
                projectService.createProject(projectRequest, idAccount, idWorkspace);
                if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
                    return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @PutMapping(value = "/{idWorkspace}/project/{idProject}")
    private MessageResponse updateProject(@RequestHeader("AccountId") Integer idAccount,
                                          @PathVariable Integer idWorkspace,
                                          @PathVariable Integer idProject,
                                          @RequestBody ProjectRequest projectRequest) {
        if (projectRequest.getName().isEmpty() || projectRequest.getColor().isEmpty() || projectRequest.getClientName().isEmpty()
                || projectRequest.getTextColor().isEmpty() || projectRequest.getColorPattern().isEmpty()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            projectService.updateProject(projectRequest, idAccount, idProject);
        }
        if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());

    }

    @DeleteMapping(value = "/project/{idProject}")
    private MessageResponse deleteProject(@PathVariable Integer idProject) {
        projectService.deleteProject(idProject);
        if (projectRepository.findById(idProject).isPresent()) {
            return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
        } else {
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        }
    }


    @GetMapping(value = "/{idWorkspace}/project/searchParam")
    private List<ProjectDTO> searchPara(@PathVariable Integer idWorkspace,
                                        @RequestParam String name,
                                        @RequestParam String param,
                                        @RequestParam Boolean isActivate,
                                        @RequestBody PageSizeRequest pageSizeRequest
    ) {
        if (param.equals(SearchMessage.PROJECT_NAME.getName())) {
            return projectService.searchParameter(name, "", isActivate, idWorkspace, pageSizeRequest);
        } else {
            return projectService.searchParameter("", name, isActivate, idWorkspace, pageSizeRequest);
        }
    }
}
