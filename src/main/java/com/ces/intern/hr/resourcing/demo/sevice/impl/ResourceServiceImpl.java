package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private WorkspaceService workSpaceService;

    @Autowired
    private WorkspaceConverter workSpaceConverter;

    @Autowired
    private ResourceConverter resourceConverter;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Override
    public List<ResourceDTO> searchByName(String name, Integer id){
        List<ResourceDTO> result = new ArrayList<>();
        List<ResourceEntity> found = new ArrayList<>(resourceRepository.
                findByNameContainingIgnoreCaseAndWorkspaceEntityResource_Id(name, id));
        for (ResourceEntity resourceEntity : found) {
            result.add(resourceConverter.convertToDto(resourceEntity));
        }
        return result;
    }

    @Override
    public MessageResponse addNewResource(ResourceRequest resourceRequest, Integer id, Integer accountId) {
        Date date = new Date();
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setName(resourceRequest.getName());
        resourceDTO.setAvatar(resourceRequest.getAvatar());
        TeamEntity teamEntity = teamRepository.findById(resourceRequest.getTeamId()).orElse(null);
        resourceDTO.setTeamDTO(ObjectMapperUtils.map(teamEntity, TeamDTO.class));
        PositionEntity positionEntity = positionRepository.findById(resourceRequest.getTeamId()).orElse(null);
        resourceDTO.setPositionDTO(ObjectMapperUtils.map(positionEntity, PositionDTO.class));
        resourceDTO.setCreatedDate(date);
        resourceDTO.setCreatedBy(accountId);
        resourceDTO.setWorkspaceName(workSpaceConverter.toDTO(workspaceRepository.findById(id).orElseThrow(()
                ->new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()))));
        resourceRepository.save(resourceConverter.convertToEntity(resourceDTO));
        return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
    }

    @Override
    public ResourceDTO findById(Integer id){
        ResourceEntity resourceEntity = new ResourceEntity();
        if (resourceRepository.findById(id).isPresent()) {
            resourceEntity = resourceRepository.findById(id).get();
        }
        return resourceConverter.convertToDto(resourceEntity);
    }

    @Override
    public MessageResponse updateResource(ResourceRequest resourceRequest, Integer resourceId, Integer workspaceId, Integer accountId){
        if(resourceRepository.findByIdAndWorkspaceEntityResource_Id(resourceId, workspaceId).isPresent()) {
            ResourceEntity resourceEntityTarget = resourceRepository.findByIdAndWorkspaceEntityResource_Id
                    (resourceId, workspaceId).get();
            //ResourceEntity resourceEntityUpdated = resourceConverter.convertToEntity(resourceDTO);
            //resourceEntityTarget.setId(resourceEntityUpdated.getId());
//            resourceEntityTarget.setCreatedBy(resourceEntityUpdated.getCreatedBy());
//            resourceEntityTarget.setCreatedDate(resourceEntityUpdated.getCreatedDate());
            resourceEntityTarget.setModifiedBy(accountId);
            resourceEntityTarget.setModifiedDate(new Date());
            resourceEntityTarget.setAvatar(resourceRequest.getAvatar());
            resourceEntityTarget.setName(resourceRequest.getName());
            resourceEntityTarget.setTeamEntity(teamRepository.findById
                    (resourceRequest.getTeamId()).orElse(null));
            resourceEntityTarget.setPositionEntity(positionRepository.findById
                    (resourceRequest.getPositionId()).orElse(null));
//            resourceEntityTarget.setTimeEntities(resourceEntityUpdated.getTimeEntities());
//            resourceEntityTarget.setWorkspaceEntityResource(resourceEntityUpdated.getWorkspaceEntityResource());
            resourceRepository.save(resourceEntityTarget);
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS,Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.UPDATE_FAIL,Status.FAIL.getCode());
    }

    @Override
    public MessageResponse deleteResource(Integer id, Integer workspaceId){
        if(resourceRepository.findByIdAndWorkspaceEntityResource_Id(id, workspaceId).isPresent()){
            resourceRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS,Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_FAIL,Status.FAIL.getCode());
    }

    @Override
    public List<ResourceDTO> getResourcesOfWorkSpace(Integer id){
        ArrayList<ResourceDTO> list = new ArrayList<>();
        for(ResourceEntity r : resourceRepository.findResourcesOfWorkSpace(id)){
            list.add(resourceConverter.convertToDto(r));
        }
        return list;
    }

    @Override
    public List<ResourceDTO> getProductManagers(Integer id){
        ArrayList<ResourceDTO> list = new ArrayList<>();
        for(ResourceEntity r : resourceRepository.findAllProductManagersOfWorkspace(id)){
            list.add(resourceConverter.convertToDto(r));
        }
        return list;
    }

    @Override
    public List<ResourceDTO> getAccountManagers(Integer id){
        ArrayList<ResourceDTO> list = new ArrayList<>();
        for(ResourceEntity r : resourceRepository.findAllAccountManagersOfWorkspace(id)){
            list.add(resourceConverter.convertToDto(r));
        }
        return list;
    }
}
