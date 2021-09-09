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
        if (resourceEntity.getTeamEntityResource()==null){
            resourceDTO.setTeamDTO(null);
        }else {
            resourceDTO.setTeamDTO(modelMapper.map(resourceEntity.getTeamEntityResource(),TeamDTO.class));

        }
        if(resourceEntity.getPositionEntity() != null){
            PositionEntity positionEntity= resourceEntity.getPositionEntity();
            resourceDTO.setPositionDTO(ObjectMapperUtils.map(positionEntity, PositionDTO.class));
            if(positionEntity.getTeamEntity() != null){
                resourceDTO.getPositionDTO()
                        .setTeamDTO(ObjectMapperUtils.map(positionEntity.getTeamEntity(), TeamDTO.class));
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
            PositionDTO positionDTO= resourceDTO.getPositionDTO();
            resourceEntity.setPositionEntity(ObjectMapperUtils.map(positionDTO, PositionEntity.class));
            if(positionDTO.getTeamDTO() != null){
                resourceEntity.getPositionEntity()
                        .setTeamEntity(ObjectMapperUtils.map(positionDTO.getTeamDTO(), TeamEntity.class));
            }
        }
        if (resourceDTO.getListTime() != null){
            resourceEntity.setTimeEntities(ObjectMapperUtils.mapAll(resourceDTO.getListTime(), TimeEntity.class));
        }
        return resourceEntity;
    }


}
