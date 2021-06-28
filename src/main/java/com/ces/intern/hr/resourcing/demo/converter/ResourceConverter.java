package com.ces.intern.hr.resourcing.demo.converter;


import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceConverter {
    @Autowired
    private ModelMapper modelMapper;

    public ResourceDTO toDTO(ResourceEntity resourceEntity){
        ResourceDTO resourceDTO = modelMapper.map(resourceEntity,ResourceDTO.class);
        resourceDTO.setWorkspaceName(resourceEntity.getWorkspaceEntityResource().getName());

        return resourceDTO;
    }
    public ResourceEntity toEntity(ResourceDTO resourceDTO){
        ResourceEntity resourceEntity = modelMapper.map(resourceDTO,ResourceEntity.class);
        return resourceEntity;
    }
}
