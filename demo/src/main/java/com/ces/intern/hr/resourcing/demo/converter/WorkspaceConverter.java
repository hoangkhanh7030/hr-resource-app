package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkspaceConverter {
    public WorkspaceDTO toDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = new WorkspaceDTO();
        workspaceDTO.setId(workspaceEntity.getId());
        workspaceDTO.setName(workspaceEntity.getName());
        workspaceDTO.setProjectList(projectDTOList(workspaceEntity));
        workspaceDTO.setResourceList(resourceDTOList(workspaceEntity));
        return workspaceDTO;
    }
    public WorkspaceEntity toEntity(WorkspaceDTO workspaceDTO){
        WorkspaceEntity workspaceEntity = new WorkspaceEntity();
        workspaceEntity.setId(workspaceDTO.getId());
        workspaceEntity.setName(workspaceDTO.getName());
        return workspaceEntity;
    }
    public List<ProjectDTO> projectDTOList(WorkspaceEntity workspaceEntity){
        List<ProjectDTO> list = new ArrayList<>();
        if(workspaceEntity.getProjectEntities().size()>0){
        for (int i=0;i<workspaceEntity.getProjectEntities().size();i++){
                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setId(workspaceEntity.getProjectEntities().get(i).getId());
                projectDTO.setName(workspaceEntity.getProjectEntities().get(i).getName());
                list.add(projectDTO);
        }
        return list;
        }else return list;
    }
    public List<ResourceDTO> resourceDTOList(WorkspaceEntity workspaceEntity){
        List<ResourceDTO> list = new ArrayList<>();
        int size = workspaceEntity.getResourceEntities().size();
        if(size > 0){
        for (int i=0;i< size;i++){
                ResourceDTO resourceDTO = new ResourceDTO();
                resourceDTO.setId(workspaceEntity.getResourceEntities().get(i).getId());
                resourceDTO.setName(workspaceEntity.getResourceEntities().get(i).getName());
                list.add(resourceDTO);
        }
        return list;
        }else return list;
    }
}
