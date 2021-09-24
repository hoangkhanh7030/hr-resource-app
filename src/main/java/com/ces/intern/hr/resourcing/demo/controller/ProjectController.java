package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.project.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.CSVFile;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProjectController(ProjectService projectService,
                             ProjectRepository projectRepository,
                             ModelMapper modelMapper) {
        this.projectService = projectService;
        this.projectRepository=projectRepository;
        this.modelMapper=modelMapper;

    }


    @GetMapping(value = "/{idWorkspace}/projects/export")
    public MessageResponse exportToCSV(HttpServletResponse response,
                                       @PathVariable Integer idWorkspace) {
        try {
            response.setContentType(CSVFile.CONTENT_TYPE);
            DateFormat dateFormat = new SimpleDateFormat(CSVFile.DATE);
            String currentDateTime = dateFormat.format(new Date());

            String headerKey = CSVFile.HEADER_KEY;
            String headerValue = CSVFile.HEADER_VALUE + currentDateTime + CSVFile.FILE_TYPE;
            response.setHeader(headerKey, headerValue);
            List<ProjectEntity> projectEntityList = projectRepository.findAllByWorkspaceEntityProject_Id(idWorkspace);
            List<ProjectDTO> projectDTOList = projectEntityList.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());

            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            String[] csvHeader = CSVFile.CSV_HEADER;
            String[] nameMapping = CSVFile.NAME_MAPPING;

            csvWriter.writeHeader(csvHeader);

            for (ProjectDTO projectDTO : projectDTOList) {
                csvWriter.write(projectDTO, nameMapping);
            }
            csvWriter.close();
            return new MessageResponse(ResponseMessage.EXPORT_PROJECT_SUCCESS, Status.SUCCESS.getCode());
        } catch (Exception e) {
            return new MessageResponse(ResponseMessage.EXPORT_PROJECT_FAIL, Status.FAIL.getCode());
        }
    }

    @PostMapping(value = "/{idWorkspace}/projects/import")
    public Response importCsvFile(@RequestHeader("AccountId") Integer idAccount,
                                  @PathVariable Integer idWorkspace,
                                  @RequestParam("csvfile") MultipartFile csvfile) {
        return projectService.importCSV(idAccount, idWorkspace, csvfile);
    }

    @PostMapping(value = "/{idWorkspace}/projects")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                           @PathVariable Integer idWorkspace,
                                           @RequestBody ProjectRequest projectRequest) {

        return projectService.createdProject(projectRequest, idAccount, idWorkspace);

    }

    @PutMapping(value = "/{idWorkspace}/projects/{idProject}")
    private MessageResponse updateProject(@RequestHeader("AccountId") Integer idAccount,
                                          @PathVariable Integer idWorkspace,
                                          @PathVariable Integer idProject,
                                          @RequestBody ProjectRequest projectRequest) {
        return projectService.updateProject(projectRequest, idAccount, idProject, idWorkspace);
    }

    @DeleteMapping(value = "{idWorkspace}/projects/{idProject}")
    private MessageResponse deleteProject(@PathVariable Integer idProject,
                                          @PathVariable Integer idWorkspace) {
        return projectService.deleteProject(idProject, idWorkspace);

    }


    @PutMapping(value = "/{idWorkspace}/projects/{idProject}/isActivate")
    private MessageResponse isActivate(@PathVariable Integer idWorkspace,
                                       @PathVariable Integer idProject) {
        return projectService.isActivate(idProject, idWorkspace);


    }

    @GetMapping(value = "/{idWorkspace}/projects")
    private NumberSizeResponse getAll(@PathVariable Integer idWorkspace,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      @RequestParam String sortName,
                                      @RequestParam String searchName,
                                      @RequestParam String type,
                                      @RequestParam String isActivate) {

        return projectService.ListProject(idWorkspace, page, size, sortName, searchName, type, isActivate);

    }
}