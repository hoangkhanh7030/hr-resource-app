package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;

import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;

import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.project.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.ApacheCommonsCsvUtil;
import com.ces.intern.hr.resourcing.demo.importCSV.CsvFileService;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Message;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final CsvFileService csvFileService;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ModelMapper modelMapper,
                              CsvFileService csvFileService,
                              WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.csvFileService = csvFileService;
        this.workspaceRepository = workspaceRepository;
    }

    private static final String ACTIVE = "active";
    public static final String IS_ACTIVATE = "isActivate";
    public static final String CREATED_DATE = "createdDate";

    @Override
    public List<ProjectDTO> getAllProjects(Integer idWorkspace, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(CREATED_DATE).descending());
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
    }


    @Override
    public MessageResponse createdProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace) {
        if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {

            return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
        } else {
            if (projectRequest.validate()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            }
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            ProjectEntity projectEntity = modelMapper.map(projectRequest, ProjectEntity.class);
            projectEntity.setWorkspaceEntityProject(workspaceEntity);
            projectEntity.setIsActivate(true);
            projectEntity.setCreatedBy(idAccount);
            projectEntity.setCreatedDate(new Date());
            projectRepository.save(projectEntity);
            if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_PROJECT_SUCCESS, Status.SUCCESS.getCode());
            }
            return new MessageResponse(ResponseMessage.CREATE_PROJECT_FAIL, Status.FAIL.getCode());
        }


    }



    @Override
    public MessageResponse updateProject(ProjectRequest projectRequest, Integer idAccount, Integer idProject, Integer idWorkspace) {

        if (projectRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            ProjectEntity projectEntity = projectRepository.findById(idProject)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            projectEntity.setName(projectRequest.getName());
            projectEntity.setClientName(projectRequest.getClientName());
            projectEntity.setColor(projectRequest.getColor());
            projectEntity.setIsActivate(projectRequest.getIsActivate());
            projectEntity.setTextColor(projectRequest.getTextColor());
            projectEntity.setColorPattern(projectRequest.getColorPattern());
            projectEntity.setModifiedBy(idAccount);
            projectEntity.setModifiedDate(new Date());
            projectRepository.save(projectEntity);
        }
        if (projectRepository.findByNameAndWorkspaceEntityProject_Id(projectRequest.getName(), idWorkspace).isPresent()) {
            return new MessageResponse(ResponseMessage.UPDATE_PROJECT_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.UPDATE_PROJECT_FAIL, Status.FAIL.getCode());
    }


    @Override
    public List<ProjectDTO> searchParameter(String name, Boolean isActivate, Integer idWorkspace, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameAndClientNameAndIsActivate(idWorkspace, name, isActivate, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(
                projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> searchParameterWithoutStatus(String name, Integer idWorkspace, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameAndClientName(idWorkspace, name, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(
                projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getAll(Integer idWorkspace, String searchName) {
        List<ProjectEntity> projectEntities = projectRepository.findAll(idWorkspace, searchName);
        return projectEntities.stream().map(
                projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public NumberSizeResponse ListProject(Integer idWorkspace, int page, int size, String sortName, String searchName, String type, String isActivate) {
        sortName = sortName == null ? "" : sortName;
        searchName = searchName == null ? "" : searchName;
        type = type == null ? "" : type;


        int sizeListProject;
        if (sortName.isEmpty() && searchName.isEmpty() && type.equals(SortPara.DESC.getName()) && isActivate.isEmpty()) {
            sizeListProject = listAll(idWorkspace).size();

            return new NumberSizeResponse(getAllProjects(idWorkspace, page, size), numberSize(sizeListProject, size));
        } else if (searchName.isEmpty() && isActivate.isEmpty()) {
            sizeListProject = listAll(idWorkspace).size();

            return new NumberSizeResponse(sortProject(page, size, idWorkspace, sortName, type), numberSize(sizeListProject, size));
        } else if (!sortName.isEmpty() && !searchName.isEmpty() && !type.isEmpty()) {
            if (isActivate.isEmpty()) {

                sizeListProject = listSearch(idWorkspace, searchName).size();
                return new NumberSizeResponse(sortAndSearchListProject(idWorkspace, page, size, searchName, sortName, type), numberSize(sizeListProject, size));
            } else {
                Boolean is_Activate = isActivate.equals(ACTIVE);
                sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
                return new NumberSizeResponse(sortAndSearchProjectListWithStatusFilter(idWorkspace, page, size, is_Activate, searchName, sortName, type),
                        numberSize(sizeListProject, size));
            }
        } else if (!sortName.isEmpty() && searchName.isEmpty() && !type.isEmpty()) {
            Boolean is_Activate = isActivate.equals(ACTIVE);
            sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
            return new NumberSizeResponse(sortAndSearchProjectListWithStatusFilter(idWorkspace, page, size, is_Activate, searchName, sortName, type),
                    numberSize(sizeListProject, size));
        } else {
            if (isActivate.isEmpty()) {
                sizeListProject = listSearch(idWorkspace, searchName).size();
                return new NumberSizeResponse(searchParameterWithoutStatus(searchName, idWorkspace, page, size), numberSize(sizeListProject, size));
            } else {
                Boolean is_Activate = isActivate.equals(ACTIVE);
                sizeListProject = listSearchIsActivate(idWorkspace, searchName, is_Activate).size();
                return new NumberSizeResponse(searchParameter(searchName, is_Activate, idWorkspace, page, size), numberSize(sizeListProject, size));
            }
        }
    }

    @Override
    public MessageResponse export(HttpServletResponse response, Integer idWorkspace) {
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

    @Override
    public Response importCSV(Integer idAccount, Integer idWorkspace, MultipartFile file) {
        Response response = new Response();
        if (Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            response.addMessage(new Message(file.getOriginalFilename(),
                    CSVFile.NO_SELECTED_FILE, Status.FAIL.getCode()));

            return response;
        }
        if (!ApacheCommonsCsvUtil.isCSVFile(file)) {
            response.addMessage(new Message(file.getOriginalFilename(), CSVFile.ERROR, Status.FAIL.getCode()));
            return response;
        }


        try {

            csvFileService.store(file.getInputStream(), idWorkspace, idAccount);
            response.addMessage(new Message(file.getOriginalFilename(), CSVFile.UPLOAD_FILE, Status.SUCCESS.getCode()));
        } catch (Exception e) {
            response.addMessage(new Message(file.getOriginalFilename(), e.getMessage(), Status.FAIL.getCode()));
        }

        return response;
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


    @Override
    public List<ProjectDTO> sortProject(int page, int size, Integer idWorkspace, String sortColumn, String type) {

        if (type.equals(SortPara.ASC.getName())) {
            if (sortColumn.equals(IS_ACTIVATE)) {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortDESC(page, size, sortColumn));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(
                        projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                        .collect(Collectors.toList());
            } else {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortASC(page, size, sortColumn));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(
                        projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                        .collect(Collectors.toList());
            }
        } else {
            if (sortColumn.equals(IS_ACTIVATE)) {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortASC(page, size, sortColumn));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(
                        projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                        .collect(Collectors.toList());
            } else {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortDESC(page, size, sortColumn));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(
                        projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class))
                        .collect(Collectors.toList());
            }
        }


    }

    private Pageable sortDESC(int page, int size, String name) {
        return PageRequest.of(page, size, Sort.by(name).descending());
    }

    private Pageable sortASC(int page, int size, String name) {
        return PageRequest.of(page, size, Sort.by(name));

    }

    @Override
    public List<ProjectDTO> sortAndSearchProjectListWithStatusFilter(Integer idWorkspace, int page, int size,
                                                                     Boolean isActivate, String nameSearch,
                                                                     String sortColumn, String type) {
        Page<ProjectEntity> projectEntityPage;
        Pageable pageable;
        pageable = PageRequest.of(page, size, Sort.by(getSortDirection(type), sortColumn));
        projectEntityPage = projectRepository.findAllByNameAndClientNameAndIsActivate(idWorkspace, nameSearch, isActivate, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toList());

    }


    @Override
    public MessageResponse isActivate(Integer idProject, Integer idWorkspace) {
        if (projectRepository.findByIdAndWorkspaceEntityProject_Id(idProject, idWorkspace).isPresent()) {
            ProjectEntity projectEntity = projectRepository.findById(idProject).
                    orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            projectEntity.setIsActivate(!projectEntity.getIsActivate());
            projectRepository.save(projectEntity);

            if (projectEntity.getIsActivate()) {
                return new MessageResponse(ResponseMessage.ENABLE_PROJECT, Status.SUCCESS.getCode());

            } else {
                return new MessageResponse(ResponseMessage.ARCHIVED_PROJECT, Status.SUCCESS.getCode());
            }
        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }

    }

    @Override
    public List<ProjectDTO> sortAndSearchListProject(Integer idWorkspace, int page, int size, String nameSearch, String sortColumn, String type) {
        Pageable pageable;
        pageable = PageRequest.of(page, size, Sort.by(getSortDirection(type), sortColumn));
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameAndClientName(idWorkspace, nameSearch, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(projectEntity -> modelMapper.map(
                projectEntity, ProjectDTO.class
        )).collect(Collectors.toList());
    }


    @Override
    public MessageResponse deleteProject(Integer idProject, Integer idWorkspace) {
        if (projectRepository.findByIdAndWorkspaceEntityProject_Id(idProject, idWorkspace).isPresent()) {
            ProjectEntity projectEntity = projectRepository.findById(idProject)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            projectRepository.delete(projectEntity);
            if (projectRepository.findById(idProject).isPresent()) {
                return new MessageResponse(ResponseMessage.DELETE_PROJECT_FAIL, Status.FAIL.getCode());
            } else {
                return new MessageResponse(ResponseMessage.DELETE_PROJECT_SUCCESS, Status.SUCCESS.getCode());
            }
        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }

    }

    private Sort.Direction getSortDirection(String type) {
        Sort.Direction direction;
        if (type.equals(SortPara.ASC.getName())) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        return direction;
    }


}
