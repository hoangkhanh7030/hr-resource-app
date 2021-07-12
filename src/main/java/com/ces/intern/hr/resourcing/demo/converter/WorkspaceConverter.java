package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.response.ProjectResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import java.util.List;

@Component
public class WorkspaceConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public WorkspaceConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }

    public WorkspaceDTO toDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity,WorkspaceDTO.class);
        workspaceDTO.setProjects(projectResponseList(workspaceEntity));
        workspaceDTO.setResources(resourceResponseList(workspaceEntity));

        return workspaceDTO;
    }
    public WorkspaceEntity toEntity(WorkspaceDTO workspaceDTO){

        return modelMapper.map(workspaceDTO,WorkspaceEntity.class);
    }


    public List<ProjectDTO> projectDTOList(WorkspaceEntity workspaceEntity){
        List<ProjectDTO> list = new ArrayList<>();

        if(workspaceEntity.getProjectEntities().size()>0){
        for (ProjectEntity projectEntity : workspaceEntity.getProjectEntities()){
                ProjectDTO projectDTO = modelMapper.map(projectEntity,ProjectDTO.class
                );
                list.add(projectDTO);
        }
        }
        return list;
    }
    public List<ProjectResponse> projectResponseList(WorkspaceEntity workspaceEntity){
        List<ProjectResponse> list = new ArrayList<>();

        if(workspaceEntity.getProjectEntities().size()>0){
            for (ProjectEntity projectEntity: workspaceEntity.getProjectEntities()){
                ProjectResponse projectResponse = modelMapper.map(projectEntity,ProjectResponse.class);
                projectResponse.setActivate(projectEntity.getIsActivate());
                list.add(projectResponse);
            }
        }
        return list;
    }
    public List<ResourceDTO> resourceDTOList(WorkspaceEntity workspaceEntity){
        List<ResourceDTO> list = new ArrayList<>();

        if(workspaceEntity.getResourceEntities().size() > 0){
        for (ResourceEntity resourceEntity:workspaceEntity.getResourceEntities()){
                ResourceDTO resourceDTO = modelMapper.map(resourceEntity,ResourceDTO.class
                );
                list.add(resourceDTO);
        }
        }
        return list;
    }
    public List<ResourceResponse> resourceResponseList(WorkspaceEntity workspaceEntity){
        List<ResourceResponse> list = new ArrayList<>();

        if (workspaceEntity.getResourceEntities().size()>0){
            for (ResourceEntity resourceEntity:workspaceEntity.getResourceEntities()){
                ResourceResponse resourceResponse = modelMapper.map(resourceEntity,ResourceResponse.class
                );
                resourceResponse.setTeam(resourceEntity.getTeamEntity().getName());
                resourceResponse.setPosition(resourceEntity.getPositionEntity().getName());
                list.add(resourceResponse);
            }
        }
        return list;
    }

}
