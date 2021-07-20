package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class WorkSpaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final AccoutRepository accoutRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ModelMapper modelMapper;
    private final ResourceRepository resourceRepository;



    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                ProjectRepository projectRepository,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                ModelMapper modelMapper,
                                ResourceRepository resourceRepository

    ) {
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;
        this.resourceRepository= resourceRepository;


    }


    @Override
    public List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer idAccount) {
        List<AccountWorkspaceRoleEntity> accountWorkspaceRoleEntityList = accoutWorkspaceRoleRepository.findAllByAccountEntity_Id(idAccount);

        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();

            for (AccountWorkspaceRoleEntity accountWorkspaceRoleEntity : accountWorkspaceRoleEntityList) {
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(accountWorkspaceRoleEntity.getWorkspaceEntity().getId())
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            WorkspaceResponse workspaceResponse = modelMapper.map(workspaceEntity, WorkspaceResponse.class);
            List<ProjectEntity> projectEntities=projectRepository.findAllByWorkspaceEntityProject_Id(accountWorkspaceRoleEntity.getWorkspaceEntity().getId());
            List<ProjectDTO> projectDTOS = projectEntities.stream().map(s->modelMapper.map(s,ProjectDTO.class)).collect(Collectors.toList());
            List<ResourceEntity> resourceEntities =resourceRepository.findAllByIdWorkspace(accountWorkspaceRoleEntity.getWorkspaceEntity().getId());
            List<ResourceDTO> resourceDTOS = resourceEntities.stream().map(s->modelMapper.map(s,ResourceDTO.class)).collect(Collectors.toList());
            if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
                workspaceResponse.setRole(Role.EDIT.getName());
            } else {
                workspaceResponse.setRole(Role.VIEW.getName());
            }
            workspaceResponse.setProjectListLength(projectDTOS.size());
            workspaceResponse.setResourceListLength(resourceDTOS.size());
            workspaceResponses.add(workspaceResponse);
        }

        return workspaceResponses;
    }

    @Override
    public WorkspaceDTO getWorkspace(Integer idWorkspace, Integer idAccount) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(accountWorkspaceRoleEntity.getWorkspaceEntity().getId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity,WorkspaceDTO.class);
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceDTO.setRole(Role.EDIT.getName());
        } else {
            workspaceDTO.setRole(Role.VIEW.getName());
        }
        return workspaceDTO;
    }


    @Override
    public void createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id) {

        AccountEntity accountEntity = accoutRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + "with" + id));
        WorkspaceEntity workspaceEntity = modelMapper.map(workspaceDTO,WorkspaceEntity.class);
        Date date = new Date();
        workspaceEntity.setCreatedDate(date);
        workspaceEntity.setCreatedBy(id);
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
        accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
        accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
        accountWorkspaceRoleEntity.setCodeRole(Role.EDIT.getCode());
        workspaceRepository.save(workspaceEntity);
        accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);


    }

    @Override
    public void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " with " + idWorkspace));
        workspaceEntity.setName(workspaceDTO.getName());
        Date date = new Date();
        workspaceEntity.setModifiedDate(date);
        workspaceEntity.setModifiedBy(idAccount);
        workspaceRepository.save(workspaceEntity);


    }

    @Override
    public void deleteWorkspaceByIdWorkspace(Integer idWorkspace, Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " with " + idWorkspace));

        workspaceRepository.delete(workspaceEntity);

    }

    @Override
    public List<WorkspaceDTO> searchWorkspaceByName(String name) {
        List<WorkspaceEntity> workspaceEntities = workspaceRepository.findAllByNameContainingIgnoreCase(name);
        return workspaceEntities.stream().map(s->modelMapper.map(s,WorkspaceDTO.class)).collect(Collectors.toList());
    }


}
