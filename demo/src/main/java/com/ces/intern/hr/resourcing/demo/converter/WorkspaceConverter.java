package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkspaceConverter {
    @Autowired
    private  AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    public WorkspaceDTO toDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = new WorkspaceDTO();
        workspaceDTO.setId(workspaceEntity.getId());
        workspaceDTO.setName(workspaceEntity.getName());
//        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository
//                .findById(workspaceEntity.getId())
//                .orElseThrow(()->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
//        if (accountWorkspaceRoleEntity.getCodeRole().equals(Utils.EDIT.getCode())){
//            workspaceDTO.setRole(Utils.EDIT.getName());
//        }workspaceDTO.setRole(Utils.VIEW.getName());
        workspaceDTO.setCreatedBy(workspaceEntity.getCreatedBy());
        workspaceDTO.setCreatedDate(workspaceEntity.getCreatedDate());
        workspaceDTO.setModifiedBy(workspaceEntity.getModifiedBy());
        workspaceDTO.setModifiedDate(workspaceEntity.getModifiedDate());
//        workspaceDTO.setProjectList(projectDTOList(workspaceEntity));
//        workspaceDTO.setResourceList(resourceDTOList(workspaceEntity));

        return workspaceDTO;
    }
    public WorkspaceEntity toEntity(WorkspaceDTO workspaceDTO){
        WorkspaceEntity workspaceEntity = new WorkspaceEntity();
        workspaceEntity.setId(workspaceDTO.getId());
        workspaceEntity.setName(workspaceDTO.getName());
        workspaceEntity.setCreatedBy(workspaceDTO.getCreatedBy());
        workspaceEntity.setCreatedDate(workspaceDTO.getCreatedDate());
        workspaceEntity.setModifiedBy(workspaceDTO.getModifiedBy());
        workspaceEntity.setModifiedDate(workspaceDTO.getModifiedDate());
        return workspaceEntity;
    }
    public List<ProjectDTO> projectDTOList(WorkspaceEntity workspaceEntity){
        List<ProjectDTO> list = new ArrayList<>();
        int size =workspaceEntity.getProjectEntities().size();
        if(size>0){
        for (int i=0;i<size;i++){
                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setId(workspaceEntity.getProjectEntities().get(i).getId());
                projectDTO.setName(workspaceEntity.getProjectEntities().get(i).getName());
                projectDTO.setCreatedBy(workspaceEntity.getProjectEntities().get(i).getCreatedBy());
                projectDTO.setCreatedDate(workspaceEntity.getProjectEntities().get(i).getCreatedDate());
                projectDTO.setModifiedBy(workspaceEntity.getProjectEntities().get(i).getModifiedBy());
                projectDTO.setModifiedDate(workspaceEntity.getProjectEntities().get(i).getModifiedDate());
                list.add(projectDTO);
        }
        }
        return list;
    }
    public List<ResourceDTO> resourceDTOList(WorkspaceEntity workspaceEntity){
        List<ResourceDTO> list = new ArrayList<>();
        int size = workspaceEntity.getResourceEntities().size();
        if(size > 0){
        for (int i=0;i< size;i++){
                ResourceDTO resourceDTO = new ResourceDTO();
                resourceDTO.setId(workspaceEntity.getResourceEntities().get(i).getId());
                resourceDTO.setName(workspaceEntity.getResourceEntities().get(i).getName());
                resourceDTO.setCreatedBy(workspaceEntity.getResourceEntities().get(i).getCreatedBy());
                resourceDTO.setCreatedDate(workspaceEntity.getResourceEntities().get(i).getCreatedDate());
                resourceDTO.setModifiedBy(workspaceEntity.getResourceEntities().get(i).getModifiedBy());
                resourceDTO.setModifiedDate(workspaceEntity.getResourceEntities().get(i).getModifiedDate());
                list.add(resourceDTO);
        }
        }
        return list;
    }
}
