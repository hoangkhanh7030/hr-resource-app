package com.ces.intern.hr.resourcing.demo.sevice.impl;



import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
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

            return null;
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
    public void updateWorkspaceByIdWorkspace(WorkspaceDTO workspaceDTO, Integer idWorkspace, Integer idAccount) {
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
