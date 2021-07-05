package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ActivateRequest;
import com.ces.intern.hr.resourcing.demo.http.request.PageSizeRequest;
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

    @Override
    public List<ProjectDTO> getAllProjects(Integer idWorkspace, PageSizeRequest pageSizeRequest) {
        Pageable pageable = PageRequest.of(pageSizeRequest.getPage(), pageSizeRequest.getSize());
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllById(idWorkspace, pageable);
        List<ProjectEntity> projectEntityList = projectEntityPage.getContent();
        return projectEntityList.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
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
    public List<ProjectDTO> search(String name, Integer idWorkspace, PageSizeRequest pageSizeRequest) {
        Pageable pageable = PageRequest.of(pageSizeRequest.getPage(), pageSizeRequest.getSize());
        Page<ProjectEntity> projectEntityPage = projectRepository.findAllByNameContainingIgnoreCaseAndWorkspaceEntityProject_Id(name, idWorkspace, pageable);
        List<ProjectEntity> projectEntityList = projectEntityPage.getContent();

        return projectEntityList.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteProject(Integer idProject) {
        ProjectEntity projectEntity = projectRepository.findById(idProject)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        projectRepository.delete(projectEntity);
    }


}
