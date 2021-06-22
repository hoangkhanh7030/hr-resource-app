package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ProjectConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final AccoutRepository accoutRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectConverter projectConverter;
    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              AccoutRepository accoutRepository,
                              WorkspaceRepository workspaceRepository,
                              ProjectConverter projectConverter) {
        this.projectRepository = projectRepository;
        this.accoutRepository = accoutRepository;
        this.workspaceRepository = workspaceRepository;
        this.projectConverter = projectConverter;
    }

    @Override
    public List<ProjectDTO> getProjects(Integer idAccount, Integer idWorkspace) {
        List<ProjectEntity> projectEntities = projectRepository.findAll();
        return projectEntities.stream().map(s->projectConverter.toDTO(s)).collect(Collectors.toList());



    }
}
