package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ActivateRequest;
import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;

import com.ces.intern.hr.resourcing.demo.http.response.ProjectResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.utils.*;
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
    public List<ProjectDTO> getAllProjects( Integer idWorkspace) {

        List<ProjectEntity> projectEntities = projectRepository.findAllById(idWorkspace);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for (int i=0;i<projectEntities.size();i++){

            ProjectDTO projectDTO = modelMapper.map(projectEntities.get(i),ProjectDTO.class);
            TimeEntity timeEntityPM = timeRepository.findByIdProjectAndnamePosition(projectDTO.getId(),Position.PROJECTMANAGER.getName());
            ResourceEntity resourceEntity = resourceRepository.findById(timeEntityPM.getResourceEntity().getId())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            ResourceResponse resourceResponsePM =modelMapper.map(resourceEntity,ResourceResponse.class);
            resourceResponsePM.setPosition(resourceEntity.getPositionEntity().getName());
            resourceResponsePM.setTeam(resourceEntity.getTeamEntity().getName());
            projectDTO.setProjectManager(resourceResponsePM);

            TimeEntity timeEntityAM = timeRepository.findByIdProjectAndnamePosition(projectDTO.getId(),Position.ACCOUNTMANAGER.getName());
            ResourceEntity resourceAM = resourceRepository.findById(timeEntityAM.getResourceEntity().getId())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            ResourceResponse resourceResponseAM =modelMapper.map(resourceAM,ResourceResponse.class);
            resourceResponseAM.setPosition(resourceAM.getPositionEntity().getName());
            resourceResponseAM.setTeam(resourceAM.getTeamEntity().getName());
            projectDTO.setAccountManager(resourceResponseAM);
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
    public void updateProject(ProjectRequest projectRequest, Integer idAccount, Integer idWorkspace,Integer idProject) {
            ProjectEntity projectEntity = projectRepository.findById(idProject)
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            projectEntity.setName(projectRequest.getName());
            projectEntity.setColor(projectRequest.getColor());
            projectEntity.setModifiedBy(idAccount);
            projectEntity.setModifiedDate(new Date());

            ResourceEntity resourceEntityPM = resourceRepository.findById(projectRequest.getIdProjectManager())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            TimeEntity timeEntityPM =timeRepository.findByIdProjectAndnamePosition(projectEntity.getId(),Position.PROJECTMANAGER.getName());
            timeEntityPM.setResourceEntity(resourceEntityPM);
            timeRepository.save(timeEntityPM);

            ResourceEntity resourceEntityAM = resourceRepository.findById(projectRequest.getIdAccountManager())
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            TimeEntity timeEntityAM =timeRepository.findByIdProjectAndnamePosition(projectEntity.getId(),Position.ACCOUNTMANAGER.getName());
            timeEntityAM.setResourceEntity(resourceEntityAM);
            timeRepository.save(timeEntityAM);
            projectRepository.save(projectEntity);
    }


    @Override
    public List<ProjectDTO> search(String name) {
        List<ProjectEntity> projectEntities = projectRepository.findAllByNameContainingIgnoreCase(name);
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        for (int i=0;i<projectEntities.size();i++){

            ProjectDTO projectDTO = modelMapper.map(projectEntities.get(i),ProjectDTO.class);
            TimeEntity timeEntityPM = timeRepository.findByIdProjectAndnamePosition(projectDTO.getId(),Position.PROJECTMANAGER.getName());
            ResourceEntity resourceEntity = resourceRepository.findById(timeEntityPM.getResourceEntity().getId())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            ResourceResponse resourceResponsePM =modelMapper.map(resourceEntity,ResourceResponse.class);
            resourceResponsePM.setPosition(resourceEntity.getPositionEntity().getName());
            resourceResponsePM.setTeam(resourceEntity.getTeamEntity().getName());
            projectDTO.setProjectManager(resourceResponsePM);

            TimeEntity timeEntityAM = timeRepository.findByIdProjectAndnamePosition(projectDTO.getId(),Position.ACCOUNTMANAGER.getName());
            ResourceEntity resourceAM = resourceRepository.findById(timeEntityAM.getResourceEntity().getId())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            ResourceResponse resourceResponseAM =modelMapper.map(resourceAM,ResourceResponse.class);
            resourceResponseAM.setPosition(resourceAM.getPositionEntity().getName());
            resourceResponseAM.setTeam(resourceAM.getTeamEntity().getName());
            projectDTO.setAccountManager(resourceResponseAM);
            projectDTOList.add(projectDTO);
        }

        return projectDTOList;
    }

    @Override
    public void Activate(ActivateRequest activateRequest,Integer idWorkspace, Integer idProject) {
        ProjectEntity projectEntity = projectRepository.findByIdWorkspaceAndIdProject(idWorkspace,idProject)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        projectEntity.setIsActivate(activateRequest.isActivate());
        projectEntity.setModifiedDate(new Date());
        projectRepository.save(projectEntity);
    }
}
