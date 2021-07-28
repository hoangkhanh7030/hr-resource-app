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
        resourceDTO.setIsArchived(resourceEntity.getIsArchived());
        if(resourceEntity.getPositionEntity() != null){
            resourceDTO.setPositionDTO(ObjectMapperUtils.map(resourceEntity.getPositionEntity(), PositionDTO.class));
            if(resourceEntity.getPositionEntity().getTeamEntity() != null){
                resourceDTO.getPositionDTO()
                        .setTeamDTO(ObjectMapperUtils.map(resourceEntity
                                .getPositionEntity().getTeamEntity(), TeamDTO.class));
            }
        }
        if (resourceEntity.getTimeEntities() != null){
            resourceDTO.setListTime(ObjectMapperUtils.mapAll(resourceEntity.getTimeEntities(), TimeDTO.class));
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
        resourceEntity.setIsArchived(resourceDTO.getIsArchived());
        if(resourceDTO.getPositionDTO() != null){
            resourceEntity.setPositionEntity(ObjectMapperUtils.map(resourceDTO.getPositionDTO(), PositionEntity.class));
            if(resourceDTO.getPositionDTO() != null){
                resourceEntity.getPositionEntity()
                        .setTeamEntity(ObjectMapperUtils.map(resourceDTO
                                .getPositionDTO().getTeamDTO(), TeamEntity.class));
            }
        }
        if (resourceDTO.getListTime() != null){
            resourceEntity.setTimeEntities(ObjectMapperUtils.mapAll(resourceDTO.getListTime(), TimeEntity.class));
        }
        return resourceEntity;
    }


}
