package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
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
    private final WorkspaceConverter workspaceConverter;
    private final AccoutRepository accoutRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                WorkspaceConverter workspaceConverter,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                ModelMapper modelMapper
    ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceConverter = workspaceConverter;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;

    }


    @Override
    public List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " with " + id));

        List<WorkspaceResponse> list = new ArrayList<>();
        if (accountEntity.getEntityAccoutWorkspaceRoleList().size() > 0) {
            for (int i = 0; i < accountEntity.getEntityAccoutWorkspaceRoleList().size(); i++) {


                WorkspaceResponse workspaceResponse = modelMapper.map(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity(), WorkspaceResponse.class);
                List<ProjectDTO> projectDTOS = workspaceConverter.projectDTOList(workspaceRepository.findByName(workspaceResponse.getName())
                        .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                                + " with " + workspaceResponse.getName())));
                List<ResourceDTO> resourceDTOS = workspaceConverter.resourceDTOList(workspaceRepository.findByName(workspaceResponse.getName())
                        .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                                + " with " + workspaceResponse.getName())));
                AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository
                        .findByNameWorkspace(workspaceResponse.getName())
                        .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
                if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
                    workspaceResponse.setRole(Role.EDIT.getName());
                } else {
                    workspaceResponse.setRole(Role.VIEW.getName());
                }
                workspaceResponse.setProjectListLength(projectDTOS.size());
                workspaceResponse.setResourceListLength(resourceDTOS.size());
                list.add(workspaceResponse);
            }
        }
        return list;
    }

    @Override
    public WorkspaceDTO getWorkspace(Integer idWorkspace, Integer idAccount) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(accountWorkspaceRoleEntity.getWorkspaceEntity().getId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        WorkspaceDTO workspaceDTO = workspaceConverter.toDTO(workspaceEntity);
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceDTO.setRole(Role.EDIT.getName());
        } else {
            workspaceDTO.setRole(Role.VIEW.getName());
        }
        return workspaceDTO;
    }

    @Override
    public WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + "with" + id));
        WorkspaceEntity workspaceEntity = workspaceConverter.toEntity(workspaceDTO);

        if (workspaceRepository.findByName(workspaceEntity.getName()).isPresent()) {
        }
        Date date = new Date();
        workspaceEntity.setCreatedDate(date);
        workspaceEntity.setCreatedBy(id);
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
        accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
        accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
        accountWorkspaceRoleEntity.setCodeRole(Role.EDIT.getCode());
        workspaceRepository.save(workspaceEntity);
        accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
        return workspaceConverter.toDTO(workspaceEntity);
    }

    @Override
    public void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " with " + idWorkspace));
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " With idWorkspace " + idWorkspace + " and idAccount " + idAccount));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceEntity.setName(workspaceDTO.getName());
            Date date = new Date();
            workspaceEntity.setModifiedDate(date);
            workspaceEntity.setModifiedBy(idAccount);
            workspaceRepository.save(workspaceEntity);
            workspaceConverter.toDTO(workspaceEntity);
        }

    }

    @Override
    public void deleteWorkspaceByIdWorkspace(Integer idWorkspace, Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " with " + idWorkspace));
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        + " With idWorkspace " + idWorkspace + " and idAccount " + idAccount));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceRepository.delete(workspaceEntity);
        }

    }

    @Override
    public List<WorkspaceDTO> searchWorkspaceByName(String name) {
        List<WorkspaceEntity> workspaceEntityList = workspaceRepository.findAllByNameContainingIgnoreCase(name);
        return workspaceEntityList.stream().map(workspaceConverter::toDTO).collect(Collectors.toList());
    }


}
