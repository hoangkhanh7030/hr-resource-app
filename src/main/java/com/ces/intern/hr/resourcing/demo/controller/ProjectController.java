package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.ApacheCommonsCsvUtil;
import com.ces.intern.hr.resourcing.demo.importCSV.CsvFileService;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Message;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class ProjectController {
    private static final String ACTIVE = "active";
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final CsvFileService csvFileService;

    @Autowired
    public ProjectController(ProjectService projectService,
                             ProjectRepository projectRepository,
                             ModelMapper modelMapper,
                             CsvFileService csvFileService) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.csvFileService = csvFileService;
    }

    private List<ProjectEntity> listAll(Integer idWorkspace) {
        return projectRepository.findAllByWorkspaceEntityProject_Id(idWorkspace);
    }

    private List<ProjectEntity> listSearch(Integer idWorkspace, String searchName) {
        return projectRepository.findAllByidWorkspaceAndSearchName(idWorkspace, searchName);
    }

    private List<ProjectEntity> listSearchIsActivate(Integer idWorkspace, String searchName, Boolean isActivate) {
        return projectRepository.findAllByNameAndClientNameAndActivate(idWorkspace, searchName, isActivate);
    }


    private int numberSize(int sizeListProject, int size) {
        int numberSize;
        if (sizeListProject % size == 0) {
            numberSize = sizeListProject / size;
        } else {
            numberSize = (sizeListProject / size) + 1;
        }
        return numberSize;
    }


    @GetMapping(value = "/{idWorkspace}/projects/export")
    public void exportToCSV(HttpServletResponse response,
                            @PathVariable Integer idWorkspace) throws IOException {
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
    }

    @PostMapping(value = "/{idWorkspace}/projects/import")
    public Response importCsvFile(@RequestHeader("AccountId") Integer idAccount,
                                  @PathVariable Integer idWorkspace,
                                  @RequestParam("csvfile") MultipartFile csvfile) {
        Response response = new Response();
        if (Objects.requireNonNull(csvfile.getOriginalFilename()).isEmpty()) {
            response.addMessage(new Message(csvfile.getOriginalFilename(),
                    CSVFile.NO_SELECTED_FILE, Status.FAIL.getCode()));

            return response;
        }
        if (!ApacheCommonsCsvUtil.isCSVFile(csvfile)) {
            response.addMessage(new Message(csvfile.getOriginalFilename(), CSVFile.ERROR, Status.FAIL.getCode()));
            return response;
        }


        try {

            csvFileService.store(csvfile.getInputStream(), idWorkspace, idAccount);
            response.addMessage(new Message(csvfile.getOriginalFilename(), CSVFile.UPLOAD_FILE, Status.SUCCESS.getCode()));
        } catch (Exception e) {
            response.addMessage(new Message(csvfile.getOriginalFilename(), e.getMessage(), Status.FAIL.getCode()));
        }

        return response;
    }

    @PostMapping(value = "/{idWorkspace}/projects")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                           @PathVariable Integer idWorkspace,
                                           @RequestBody ProjectRequest projectRequest) {

        if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {

            return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
        } else {
            if (projectRequest.validate()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            }
            projectService.createProject(projectRequest, idAccount, idWorkspace);
            if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
            }
            return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
        }

    }

    @PutMapping(value = "/{idWorkspace}/projects/{idProject}")
    private MessageResponse updateProject(@RequestHeader("AccountId") Integer idAccount,
                                          @PathVariable Integer idWorkspace,
                                          @PathVariable Integer idProject,
                                          @RequestBody ProjectRequest projectRequest) {
        if (projectRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            projectService.updateProject(projectRequest, idAccount, idProject);
        }
        if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());

    }

    @DeleteMapping(value = "{idWorkspace}/projects/{idProject}")
    private MessageResponse deleteProject(@PathVariable Integer idProject,
                                          @PathVariable Integer idWorkspace) {
        if (projectRepository.findByIdAndWorkspaceEntityProject_Id(idProject, idWorkspace).isPresent()) {
            projectService.deleteProject(idProject);
            if (projectRepository.findById(idProject).isPresent()) {
                return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
            } else {
                return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
            }
        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }

    }


    @PutMapping(value = "/{idWorkspace}/projects/{idProject}/isActivate")
    private MessageResponse isActivate(@PathVariable Integer idWorkspace,
                                       @PathVariable Integer idProject) {
        if (projectRepository.findByIdAndWorkspaceEntityProject_Id(idProject, idWorkspace).isPresent()) {
            projectService.isActivate(idProject);
            ProjectEntity projectEntity = projectRepository.findById(idProject)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            if (projectEntity.getIsActivate()) {
                return new MessageResponse(ResponseMessage.IS_ACTIVATE, Status.SUCCESS.getCode());

            } else {
                return new MessageResponse(ResponseMessage.ARCHIEVED, Status.SUCCESS.getCode());
            }
        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }


    }

    @GetMapping(value = "/{idWorkspace}/projects")
    private NumberSizeResponse getAll(@PathVariable Integer idWorkspace,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      @RequestParam String sortName,
                                      @RequestParam String searchName,
                                      @RequestParam String type,
                                      @RequestParam String isActivate) {
        sortName = sortName == null ? "" : sortName;
        searchName = searchName == null ? "" : searchName;
        type = type == null ? "" : type;


        int sizeListProject;
        if (sortName.isEmpty() && searchName.isEmpty() && type.equals(SortPara.DESC.getName()) && isActivate.isEmpty()) {
            sizeListProject = listAll(idWorkspace).size();

            return new NumberSizeResponse(projectService.getAllProjects(idWorkspace, page, size), numberSize(sizeListProject, size));
        } else if (searchName.isEmpty() && isActivate.isEmpty()) {
            sizeListProject = listAll(idWorkspace).size();

            return new NumberSizeResponse(projectService.sortProject(page, size, idWorkspace, sortName, type), numberSize(sizeListProject, size));
        } else if (!sortName.isEmpty() && !searchName.isEmpty() && !type.isEmpty()) {
            if (isActivate.isEmpty()) {

                sizeListProject = listSearch(idWorkspace, searchName).size();
                return new NumberSizeResponse(projectService.listSortAndSearch(idWorkspace, page, size, searchName, sortName, type), numberSize(sizeListProject, size));
            } else {
                Boolean is_Activate = isActivate.equals(ACTIVE);
                sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
                return new NumberSizeResponse(projectService.listSortAndSearchAndIsActivate(idWorkspace, page, size, is_Activate, searchName, sortName, type),
                        numberSize(sizeListProject, size));
            }
        } else if (!sortName.isEmpty() && searchName.isEmpty() && !type.isEmpty()) {
            Boolean is_Activate = isActivate.equals(ACTIVE);
            sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
            return new NumberSizeResponse(projectService.listSortAndSearchAndIsActivate(idWorkspace, page, size, is_Activate, searchName, sortName, type),
                    numberSize(sizeListProject, size));
        } else {
            if (isActivate.isEmpty()) {
                sizeListProject = listSearch(idWorkspace, searchName).size();
                return new NumberSizeResponse(projectService.searchParameterNotIsActivate(searchName, idWorkspace, page, size), numberSize(sizeListProject, size));
            } else {
                Boolean is_Activate = isActivate.equals(ACTIVE);
                sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
                return new NumberSizeResponse(projectService.searchParameter(searchName, is_Activate, idWorkspace, page, size), numberSize(sizeListProject, size));
            }
        }
    }


}
