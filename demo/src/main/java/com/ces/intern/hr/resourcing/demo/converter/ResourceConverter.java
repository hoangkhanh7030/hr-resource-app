package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import org.springframework.stereotype.Component;

@Component
public class ResourceConverter {
    public ResourceDTO toDTO(ResourceEntity resourceEntity){
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(resourceEntity.getId());
        resourceDTO.setName(resourceEntity.getName());
        resourceDTO.setAvatar(resourceEntity.getAvatar());
        resourceDTO.setWorkspaceName(resourceEntity.getWorkspaceEntityResource().getName());
        return resourceDTO;
    }
    public ResourceEntity toEntity(ResourceDTO resourceDTO){
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setId(resourceDTO.getId());
        resourceEntity.setName(resourceDTO.getName());
        resourceEntity.setAvatar(resourceDTO.getAvatar());
        return resourceEntity;
    }
}
