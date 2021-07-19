package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;

import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;

import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ModelMapper modelMapper,
                              WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.workspaceRepository = workspaceRepository;
    }
    public static final String IS_ACTIVATE = "isActivate";
    public static final String CREATED_DATE="createdDate";

    @Override
    public List<ProjectDTO> getAllProjects(Integer idWorkspace, int page,int size) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(CREATED_DATE).descending());
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
    }


    @Override
    public void createProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        ProjectEntity projectEntity = modelMapper.map(projectRequest, ProjectEntity.class);
        projectEntity.setWorkspaceEntityProject(workspaceEntity);
        projectEntity.setIsActivate(true);
        projectEntity.setCreatedBy(idAccount);
        projectEntity.setCreatedDate(new Date());
        projectRepository.save(projectEntity);
    }


    @Override
    public void updateProject(ProjectRequest projectRequest, Integer idAccount, Integer idProject) {
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


    @Override
    public List<ProjectDTO> searchParameter(String name, Boolean isActivate, Integer idWorkspace,int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameAndClientNameAndIsActivate(idWorkspace, name, isActivate, pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
    }
    @Override
    public List<ProjectDTO> searchParameterNotIsActivate(String name, Integer idWorkspace, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameAndClientName(idWorkspace,name,pageable);
        List<ProjectEntity> projectEntities = projectEntityPage.getContent();
        return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
    }


    @Override
    public List<ProjectDTO> sortProject(int page, int size, Integer idWorkspace, String name, String type) {

        if (type.equals(SortPara.ASC.getName())) {
            if (name.equals(IS_ACTIVATE)) {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortDESC(page, size, name));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
            } else {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortASC(page, size, name));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
            }
        } else {
            if (name.equals(IS_ACTIVATE)) {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortASC(page, size, name));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
            } else {
                Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, sortDESC(page, size, name));
                List<ProjectEntity> projectEntities = projectEntityPage.getContent();
                return projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
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
    public void isActivate(Integer idProject) {
       ProjectEntity projectEntity = projectRepository.findById(idProject).
                orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        projectEntity.setIsActivate(!projectEntity.getIsActivate());
        projectRepository.save(projectEntity);
    }

    @Override
    public List<ProjectDTO> listSortAndSearch(Integer idWorkspace, int page, int size, Boolean isActivate, String nameSearch, String nameSort, String type) {
        return null;
    }




    @Override
    public void deleteProject(Integer idProject) {
        ProjectEntity projectEntity = projectRepository.findById(idProject)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        projectRepository.delete(projectEntity);
    }





}
