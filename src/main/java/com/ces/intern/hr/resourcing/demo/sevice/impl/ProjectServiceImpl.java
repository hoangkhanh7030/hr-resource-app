package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Position;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final TimeRepository timeRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ResourceRepository resourceRepository;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ModelMapper modelMapper,
                              TimeRepository timeRepository,
                              AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                              ResourceRepository resourceRepository,
                              WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.timeRepository=timeRepository;
        this.accoutWorkspaceRoleRepository=accoutWorkspaceRoleRepository;
        this.resourceRepository= resourceRepository;
        this.workspaceRepository=workspaceRepository;
    }

    @Override
    public List<ProjectDTO> getAllProjects(Integer idAccount, Integer idWorkspace) {
        List<ProjectEntity> projectEntities = projectRepository.findAllById(idWorkspace);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for (ProjectEntity projectEntity : projectEntities) {
            ProjectDTO projectDTO = modelMapper.map(projectEntity, ProjectDTO.class);
            List<TimeEntity> timeEntityList = timeRepository.findAllByIdProject(projectDTO.getId());
            for (TimeEntity timeEntity : timeEntityList) {
                if (timeEntity.getResourceEntity().getPositionEntity().getName().equals(Position.PROJECTMANAGER.getName())) {
                    projectDTO.setProjectManager(timeEntity.getResourceEntity().getName());
                } else if (timeEntity.getResourceEntity().getPositionEntity().getName().equals(Position.ACCOUNTMANAGER.getName())) {
                    projectDTO.setAccountManager(timeEntity.getResourceEntity().getName());
                }
            }
            projectDTOS.add(projectDTO);
        }
        return projectDTOS;


    }

    @Override
    public void createProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace) {

                WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                        .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
                ProjectEntity projectEntity = new ProjectEntity();
                projectEntity.setName(projectRequest.getName());
                projectEntity.setColor(projectRequest.getColor());
                projectEntity.setIsActivate(true);
                projectEntity.setCreatedDate(new Date());
                projectEntity.setCreatedBy(idAccount);
                projectEntity.setWorkspaceEntityProject(workspaceEntity);
                projectRepository.save(projectEntity);


                projectEntity=projectRepository.findByName(projectRequest.getName()).orElse(null);
                TimeEntity timeEntity = new TimeEntity();
                timeEntity.setProjectEntity(projectEntity);
                ResourceEntity resourceEntity = resourceRepository.findById(projectRequest.getIdProjectManager()).orElse(null);
                timeEntity.setResourceEntity(resourceEntity);
                timeRepository.save(timeEntity);

                TimeEntity time = new TimeEntity();
                time.setProjectEntity(projectEntity);
                resourceEntity=resourceRepository.findById(projectRequest.getIdAccountManager()).orElse(null);
                time.setResourceEntity(resourceEntity);
                timeRepository.save(time);


    }

    @Override
    public List<ResourceResponse> getListPM(Integer idAccount, Integer idWorkspace) {
        if (accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount).isPresent()){
            List<ResourceEntity> list = resourceRepository.findAllByIdWorkspaceAndNamePosition(idWorkspace,Position.PROJECTMANAGER.getName());

            return list.stream().map(s->modelMapper.map(s,ResourceResponse.class)).collect(Collectors.toList());
        }return null;


    }

    @Override
    public List<ResourceResponse> getListAM(Integer idAccount, Integer idWorkspace) {
        if (accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount).isPresent()){
            List<ResourceEntity> list = resourceRepository.findAllByIdWorkspaceAndNamePosition(idWorkspace,Position.ACCOUNTMANAGER.getName());

            return list.stream().map(s->modelMapper.map(s,ResourceResponse.class)).collect(Collectors.toList());
        }return null;
    }

    @Override
    public void updateProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace, Integer idProject) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())){
            ProjectEntity projectEntity = projectRepository.findById(idProject)
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            projectEntity.setName(projectRequest.getName());
            projectEntity.setColor(projectRequest.getColor());
            ResourceEntity resourceEntity = resourceRepository.findById(projectRequest.getIdProjectManager()).orElse(null);

        }
    }

    @Override
    public List<ProjectDTO> search(String name) {
        return null;
    }
}
