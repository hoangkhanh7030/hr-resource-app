package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.*;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.utils.ObjectMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceConverter {
    @Autowired
    private ModelMapper modelMapper;

    public ResourceDTO convertToDto(ResourceEntity resourceEntity){
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(resourceEntity.getId());
        resourceDTO.setCreatedBy(resourceEntity.getCreatedBy());
        resourceDTO.setCreatedDate(resourceEntity.getCreatedDate());
        resourceDTO.setModifiedBy(resourceEntity.getModifiedBy());
        resourceDTO.setModifiedDate(resourceEntity.getModifiedDate());
        resourceDTO.setAvatar(resourceEntity.getAvatar());
        resourceDTO.setName(resourceEntity.getName());
        if(resourceEntity.getTeamEntity() != null){
            resourceDTO.setTeamDTO(ObjectMapperUtils.map(resourceEntity.getTeamEntity(), TeamDTO.class));
        }
        if(resourceEntity.getPositionEntity() != null){
            resourceDTO.setPositionDTO(ObjectMapperUtils.map(resourceEntity.getPositionEntity(), PositionDTO.class));
        }
        if (resourceEntity.getTimeEntities() != null && resourceEntity.getTimeEntities().size() != 0){
            resourceDTO.setListTime(ObjectMapperUtils.mapAll(resourceEntity.getTimeEntities(), TimeDTO.class));
        }
        if (resourceEntity.getWorkspaceEntityResource() != null){
            resourceDTO.setWorkspaceName(ObjectMapperUtils.map(resourceEntity.getWorkspaceEntityResource(), WorkspaceDTO.class));
        }
        return resourceDTO;
    }

    public ResourceEntity convertToEntity(ResourceDTO resourceDTO){
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setId(resourceDTO.getId());
        resourceEntity.setCreatedBy(resourceDTO.getCreatedBy());
        resourceEntity.setCreatedDate(resourceDTO.getCreatedDate());
        resourceEntity.setModifiedBy(resourceDTO.getModifiedBy());
        resourceEntity.setModifiedDate(resourceDTO.getModifiedDate());
        resourceEntity.setAvatar(resourceDTO.getAvatar());
        resourceEntity.setName(resourceDTO.getName());
        if(resourceDTO.getTeamDTO() != null){
            resourceEntity.setTeamEntity(ObjectMapperUtils.map(resourceDTO.getTeamDTO(), TeamEntity.class));
        }
        if(resourceDTO.getPositionDTO() != null){
            resourceEntity.setPositionEntity(ObjectMapperUtils.map(resourceDTO.getPositionDTO(), PositionEntity.class));
        }
        if (resourceDTO.getListTime() != null && resourceDTO.getListTime().size() != 0){
            resourceEntity.setTimeEntities(ObjectMapperUtils.mapAll(resourceDTO.getListTime(), TimeEntity.class));
        }
        if (resourceDTO.getWorkspaceName() != null){
            resourceEntity.setWorkspaceEntityResource(ObjectMapperUtils.map(resourceDTO.getWorkspaceName(), WorkspaceEntity.class));
        }
        return resourceEntity;
    }
}
