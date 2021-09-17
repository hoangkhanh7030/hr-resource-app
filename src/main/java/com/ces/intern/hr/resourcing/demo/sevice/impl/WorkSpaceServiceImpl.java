package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.workspace.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
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
    private final WorkspaceConverter workspaceConverter;


    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                ProjectRepository projectRepository,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                ModelMapper modelMapper,
                                ResourceRepository resourceRepository,
                                WorkspaceConverter workspaceConverter

    ) {
        this.workspaceRepository = workspaceRepository;
        this.projectRepository = projectRepository;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.modelMapper = modelMapper;
        this.resourceRepository = resourceRepository;
        this.workspaceConverter = workspaceConverter;

    }


    @Override
    public List<WorkspaceResponse> getAllWorkspaceByIdAccount(Integer idAccount) {
        List<AccountWorkspaceRoleEntity> accountWorkspaceRoleEntityList = accoutWorkspaceRoleRepository.findAllByAccountEntity_Id(idAccount);

        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();

        for (AccountWorkspaceRoleEntity accountWorkspaceRoleEntity : accountWorkspaceRoleEntityList) {
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(accountWorkspaceRoleEntity.getWorkspaceEntity().getId())
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            WorkspaceResponse workspaceResponse = modelMapper.map(workspaceEntity, WorkspaceResponse.class);
            List<ProjectEntity> projectEntities = projectRepository.findAllByWorkspaceEntityProject_Id(accountWorkspaceRoleEntity.getWorkspaceEntity().getId());
            List<ProjectDTO> projectDTOS = projectEntities.stream().map(s -> modelMapper.map(s, ProjectDTO.class)).collect(Collectors.toList());
            List<ResourceEntity> resourceEntities = resourceRepository.findAllByidWorkspace(accountWorkspaceRoleEntity.getWorkspaceEntity().getId());
            List<ResourceDTO> resourceDTOS = resourceEntities.stream().map(s -> modelMapper.map(s, ResourceDTO.class)).collect(Collectors.toList());
            if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
                workspaceResponse.setRole(Role.EDIT.getName());
            } else if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.INACTIVE.getCode())) {
                workspaceResponse.setRole(Role.INACTIVE.getName());
            } else {
                workspaceResponse.setRole(Role.VIEW.getName());
            }
            workspaceResponse.setProjectListLength(projectDTOS.size());
            workspaceResponse.setResourceListLength(resourceDTOS.size());
            //workspaceResponse.setEmailSuffix(workspaceEntity.getEmailSuffix());
            List<Boolean> workDays = new ArrayList<>();
            String[] arrayWorkDays = workspaceEntity.getWorkDays().split(",");
            String[] arrayEmailSuffixes = workspaceEntity.getEmailSuffix().split(",");
            for (String string : arrayWorkDays) {
                workDays.add(Boolean.parseBoolean(string));
            }
            if (!workspaceEntity.getEmailSuffix().isEmpty()) {
                List<String> emailSuffixes = new ArrayList<>(Arrays.asList(arrayEmailSuffixes));
                workspaceResponse.setEmailSuffixes(emailSuffixes);
            } else {
                workspaceResponse.setEmailSuffixes(new ArrayList<>());
            }
            workspaceResponse.setWorkDays(workDays);
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
        //WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity, WorkspaceDTO.class);
        WorkspaceDTO workspaceDTO = workspaceConverter.convertToDTO(workspaceEntity);
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceDTO.setRole(Role.EDIT.getName());
        } else {
            workspaceDTO.setRole(Role.VIEW.getName());
        }
        return workspaceDTO;
    }


    @Override
    public MessageResponse createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer idAccount) {


        if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {

            return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
        } else {
            if (workspaceDTO.getName() == null || workspaceDTO.getName().isEmpty()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                AccountEntity accountEntity = accoutRepository.findById(idAccount).orElseThrow(
                        () -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                                + "with" + idAccount));
                WorkspaceEntity workspaceEntity = workspaceConverter.convertToEntity(workspaceDTO);
                Date date = new Date();
                workspaceEntity.setCreatedDate(date);
                workspaceEntity.setCreatedBy(idAccount);
                AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
                accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
                accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
                accountWorkspaceRoleEntity.setCodeRole(Role.EDIT.getCode());
                workspaceRepository.save(workspaceEntity);
                accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
            }
            if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_WORKSPACE_SUCCESS, Status.SUCCESS.getCode());

            }
            return new MessageResponse(ResponseMessage.CREATE_WORKSPACE_FAIL, Status.FAIL.getCode());
        }
    }

    @Override
    public MessageResponse updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount) {


        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));

        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()
                    && !accoutWorkspaceRoleRepository
                    .findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount)
                    .get().getWorkspaceEntity().getId().equals(idWorkspace)) {
                return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
            } else {
                if (workspaceDTO.getName() == null || workspaceDTO.getName().isEmpty()) {
                    return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                } else {

                    WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                            .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                                    + " with " + idWorkspace));
                    WorkspaceEntity workspaceEntityUpdated = workspaceConverter.convertToEntity(workspaceDTO);
                    workspaceEntity.setWorkDays(workspaceEntityUpdated.getWorkDays());
                    workspaceEntity.setName(workspaceEntityUpdated.getName());
                    workspaceEntity.setEmailSuffix(workspaceEntityUpdated.getEmailSuffix());
                    Date date = new Date();
                    workspaceEntity.setModifiedDate(date);
                    workspaceEntity.setModifiedBy(idAccount);
                    workspaceRepository.save(workspaceEntity);
                }

                if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {
                    return new MessageResponse(ResponseMessage.UPDATE_WORKSPACE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.UPDATE_WORKSPACE_FAIL, Status.FAIL.getCode());

            }

        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());

    }

    @Override
    public MessageResponse deleteWorkspaceByIdWorkspace(Integer idWorkspace, Integer idAccount) {


        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                            + " with " + idWorkspace));

            workspaceRepository.delete(workspaceEntity);
            if (workspaceRepository.findById(idWorkspace).isPresent()) {
                return new MessageResponse(ResponseMessage.DELETE_WORKSPACE_FAIL, Status.FAIL.getCode());

            }
            return new MessageResponse(ResponseMessage.DELETE_WORKSPACE_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());

    }

    @Override
    public List<WorkspaceDTO> searchWorkspaceByName(String name) {
        List<WorkspaceEntity> workspaceEntities = workspaceRepository.findAllByNameContainingIgnoreCase(name);
        List<WorkspaceDTO> workspaceDTOS = new ArrayList<>();
        for (WorkspaceEntity workspaceEntity : workspaceEntities) {
            workspaceDTOS.add(workspaceConverter.convertToDTO(workspaceEntity));
        }
        return workspaceDTOS;
    }


}
