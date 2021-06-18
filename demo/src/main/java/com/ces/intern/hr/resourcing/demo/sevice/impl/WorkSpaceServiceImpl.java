package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.http.exception.AlreadyExistException;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.RoleEnum;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.RoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.RoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class WorkSpaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceConverter workspaceConverter;
    private final AccoutRepository accoutRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                WorkspaceConverter workspaceConverter,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                                RoleRepository roleRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceConverter = workspaceConverter;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.roleRepository = roleRepository;
    }





    @Override
    public List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+id));
        List<WorkspaceDTO> list=new ArrayList<>();
        if(accountEntity.getEntityAccoutWorkspaceRoleList().size()>0){
        for (int i=0;i<accountEntity.getEntityAccoutWorkspaceRoleList().size();i++){
                WorkspaceDTO workspaceDTO = new WorkspaceDTO();
                workspaceDTO.setId(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getId());
                workspaceDTO.setName(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getName());
                workspaceDTO.setProjectList(workspaceConverter.projectDTOList(workspaceRepository.findByName(workspaceDTO.getName())
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+workspaceDTO.getName()))));
                workspaceDTO.setResourceList(workspaceConverter.resourceDTOList(workspaceRepository.findByName(workspaceDTO.getName())
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+workspaceDTO.getName()))));
                list.add(workspaceDTO);
        }
        return list;
        }else return list;
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
        if(workspaceRepository.findByName(workspaceEntity.getName()).isPresent()){

            return null;
        }else {
            RoleEntity roleEntity = roleRepository.findByName(RoleEnum.EDIT.getName())
                    .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                    +" with "+RoleEnum.EDIT.getName()));
            AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = new AccountWorkspaceRoleEntity();
            accountWorkspaceRoleEntity.setAccountEntity(accountEntity);
            accountWorkspaceRoleEntity.setWorkspaceEntity(workspaceEntity);
            accountWorkspaceRoleEntity.setRoleEntity(roleEntity);
            workspaceRepository.save(workspaceEntity);
            accoutWorkspaceRoleRepository.save(accountWorkspaceRoleEntity);
            return workspaceConverter.toDTO(workspaceEntity);
        }


    }

    @Override
    public WorkspaceDTO updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace,Integer idAccount) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+idWorkspace));
        workspaceEntity.setName(workspaceDTO.getName());
        Date date = new Date();
        workspaceEntity.setModifiedDate(date);
        workspaceEntity.setModifiedBy(idAccount);
        workspaceRepository.save(workspaceEntity);
        return workspaceConverter.toDTO(workspaceEntity);
    }

    @Override
    public void deleteWorkspaceByIdWorkspace(Integer id) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()
                +" with "+id));
        workspaceRepository.delete(workspaceEntity);
    }

    @Override
    public List<WorkspaceDTO> searchWorkspaceByName(String name) {
        List<WorkspaceEntity> workspaceEntityList = workspaceRepository.findAllByNameContainingIgnoreCase(name);
        List<WorkspaceDTO> workspaceDTOList = workspaceEntityList.stream().map(s->workspaceConverter.toDTO(s)).collect(Collectors.toList());
        return workspaceDTOList;
    }


}
