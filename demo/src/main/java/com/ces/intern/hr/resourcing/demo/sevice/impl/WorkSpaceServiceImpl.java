package com.ces.intern.hr.resourcing.demo.sevice.impl;



import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
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


    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                WorkspaceConverter workspaceConverter,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository
                                ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceConverter = workspaceConverter;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;

    }





    @Override
    public List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+id));

        List<WorkspaceDTO> list=new ArrayList<>();
        if(accountEntity.getEntityAccoutWorkspaceRoleList().size()>0){
        for (int i=0;i<accountEntity.getEntityAccoutWorkspaceRoleList().size();i++){
                List<ProjectDTO> projectDTOS= new ArrayList<>();
                List<ResourceDTO> resourceDTOS = new ArrayList<>();
                WorkspaceDTO workspaceDTO = new WorkspaceDTO();
                workspaceDTO.setCreatedBy(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getCreatedBy());
                workspaceDTO.setCreatedDate(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getCreatedDate());
                workspaceDTO.setModifiedBy(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getModifiedBy());
                workspaceDTO.setModifiedDate(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getModifiedDate());
                workspaceDTO.setId(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getId());
                workspaceDTO.setName(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getName());
                projectDTOS = workspaceConverter.projectDTOList(workspaceRepository.findByName(workspaceDTO.getName())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                    +" with "+workspaceDTO.getName())));
                resourceDTOS=workspaceConverter.resourceDTOList(workspaceRepository.findByName(workspaceDTO.getName())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                    +" with "+workspaceDTO.getName())));
                AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository
                        .findByNameWorkspace(workspaceDTO.getName())
                        .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
                if (accountWorkspaceRoleEntity.getCodeRole().equals(Utils.EDIT.getCode())){
                    workspaceDTO.setRole(Utils.EDIT.getName());
                }else {
                        workspaceDTO.setRole(Utils.VIEW.getName());
                }
                workspaceDTO.setProjectListLength(projectDTOS.size());
                workspaceDTO.setResourceListLength(resourceDTOS.size());
                list.add(workspaceDTO);
        }}
         return list;
    }

    @Override
    public WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id).orElseThrow(
                        ()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        +"with"+id));
        WorkspaceEntity workspaceEntity = workspaceConverter.toEntity(workspaceDTO);
        Date date = new Date();
        workspaceEntity.setCreatedDate(date);
        workspaceEntity.setCreatedBy(id);
        if(workspaceRepository.findByName(workspaceEntity.getName()).isPresent()) {

        }
            AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
            accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
            accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
            accountWorkspaceRoleEntity.setCodeRole(Utils.EDIT.getCode());
            workspaceRepository.save(workspaceEntity);
            accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
            return workspaceConverter.toDTO(workspaceEntity);
    }

    @Override
    public void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount){
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+idWorkspace));
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" With idWorkspace "+idWorkspace+" and idAccount "+idAccount));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Utils.EDIT.getCode())){
            workspaceEntity.setName(workspaceDTO.getName());
            Date date = new Date();
            workspaceEntity.setModifiedDate(date);
            workspaceEntity.setModifiedBy(idAccount);
            workspaceRepository.save(workspaceEntity);
            workspaceConverter.toDTO(workspaceEntity);
        }

    }

    @Override
    public void deleteWorkspaceByIdWorkspace(Integer idWorkspace,Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+idWorkspace));
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace,idAccount)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                        +" With idWorkspace "+idWorkspace+" and idAccount "+idAccount));
        if(accountWorkspaceRoleEntity.getCodeRole().equals(Utils.EDIT.getCode())){
            workspaceRepository.delete(workspaceEntity);
        }

    }

    @Override
    public List<WorkspaceDTO> searchWorkspaceByName(String name) {
        List<WorkspaceEntity> workspaceEntityList = workspaceRepository.findAllByNameContainingIgnoreCase(name);
        return workspaceEntityList.stream().map(workspaceConverter::toDTO).collect(Collectors.toList());
    }


}
