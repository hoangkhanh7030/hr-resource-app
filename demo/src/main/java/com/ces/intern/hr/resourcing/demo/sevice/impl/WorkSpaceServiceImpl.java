package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class WorkSpaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceConverter workspaceConverter;
    private final AccoutRepository accoutRepository;
    @Autowired
    public WorkSpaceServiceImpl(WorkspaceRepository workspaceRepository,
                                WorkspaceConverter workspaceConverter,
                                AccoutRepository accoutRepository){
        this.workspaceRepository=workspaceRepository;
        this.workspaceConverter=workspaceConverter;
        this.accoutRepository=accoutRepository;
    }
    @Override
    public List<WorkspaceDTO> getWorkspaces() {
        List<WorkspaceEntity> workspaceEntityList = workspaceRepository.findAll();
        if (workspaceEntityList.size()>0){
            List<WorkspaceDTO> workspaceDTOList =workspaceEntityList.stream()
                    .map(s->workspaceConverter.toDTO(s)).collect(Collectors.toList());
            return workspaceDTOList;
        }else return null;


    }

    @Override
    public WorkspaceDTO createWorkspace(WorkspaceDTO workspaceDTO) {
        WorkspaceEntity workspaceEntity = workspaceConverter.toEntity(workspaceDTO);
        if (workspaceRepository.findByName(workspaceEntity.getName()).isPresent()){
            return null;
        }else {
            workspaceEntity = workspaceRepository.save(workspaceEntity);
            return workspaceConverter.toDTO(workspaceEntity);
        }

    }

    @Override
    public WorkspaceDTO getWorkspaceByName(String name) {
        if (workspaceRepository.findByName(name).isPresent()){
            WorkspaceEntity workspaceEntity = workspaceRepository.findByName(name).orElse(null);
            return workspaceConverter.toDTO(workspaceEntity);
        }else return null;

    }

    @Override
    public WorkspaceDTO updateWorkspace(WorkspaceDTO workspaceDTO,String name) {
            WorkspaceEntity workspaceEntity = workspaceRepository.findByName(name).orElse(null);
            workspaceEntity.setName(workspaceDTO.getName());
            return workspaceConverter.toDTO(workspaceEntity);



    }

    @Override
    public List<WorkspaceDTO> getWorkspaceByIdAccount(Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id).orElse(null);
        List<WorkspaceDTO> list=new ArrayList<>();
        if(accountEntity.getEntityAccoutWorkspaceRoleList().size()>0){
        for (int i=0;i<accountEntity.getEntityAccoutWorkspaceRoleList().size();i++){
                WorkspaceDTO workspaceDTO = new WorkspaceDTO();
                workspaceDTO.setId(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getId());
                workspaceDTO.setName(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getName());
                workspaceDTO.setProjectList(workspaceConverter.projectDTOList(workspaceRepository.findByName(workspaceDTO.getName()).orElse(null)));
                workspaceDTO.setResourceList(workspaceConverter.resourceDTOList(workspaceRepository.findByName(workspaceDTO.getName()).orElse(null)));
                list.add(workspaceDTO);
        }
        return list;
        }else return list;
    }

    @Override
    public WorkspaceDTO createdWorkspaceByIdAccount(WorkspaceDTO workspaceDTO, Integer id) {
        AccountEntity accountEntity = accoutRepository.findById(id).orElse(null);
        WorkspaceEntity workspaceEntity = workspaceConverter.toEntity(workspaceDTO);
        if(workspaceRepository.findByName(workspaceEntity.getName()).isPresent()){
            return null;
        }else {

        }

    }


//    @Override
//    public ResponseEntity<Object> create(WorkspaceEntity model) {
//        WorkspaceEntity workspaceEntity = new WorkspaceEntity();
//        if(workspaceRepository.findByName(model.getName()).isPresent()){
//            return ResponseEntity.badRequest().body("The Workspace is already present");
//        }else {
//            workspaceEntity.setName(model.getName());
//            WorkspaceEntity saveWorkspace= workspaceRepository.save(workspaceEntity);
//            if(workspaceRepository.findByName(saveWorkspace.getName()).isPresent()){
//                return ResponseEntity.ok("Workspace created Successfully");
//            }else return ResponseEntity.unprocessableEntity().body("Failed creating Workspace");
//        }
//
//    }
//
//    @Override
//    public ResponseEntity<Object> deleteWorkspace(String name) {
//        if(workspaceRepository.findByName(name).isPresent()){
//            Optional<WorkspaceEntity> workspaceEntity = workspaceRepository.findByName(name);
//            workspaceRepository.deleteById(workspaceEntity.get().getId());
//            if (workspaceRepository.findByName(name).isPresent()){
//                return ResponseEntity.badRequest().body("Failed to delete");
//            }else return ResponseEntity.ok("Successfully delete");
//        }else return ResponseEntity.unprocessableEntity().body("can not find workspace");
//    }
}
