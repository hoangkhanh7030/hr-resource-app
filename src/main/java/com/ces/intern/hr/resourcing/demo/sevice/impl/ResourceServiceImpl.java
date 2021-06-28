package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceConverter resourceConverter;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public ResourceDTO createResource(ResourceDTO resourceDTO) {
        ResourceEntity resourceEntity = resourceConverter.toEntity(resourceDTO);
        WorkspaceEntity workspaceEntity = workspaceRepository.findByName(resourceDTO.getWorkspaceName().getName()).orElse(null);
        resourceEntity.setWorkspaceEntityResource(workspaceEntity);
        resourceRepository.save(resourceEntity);
        return resourceConverter.toDTO(resourceEntity);
    }
}
