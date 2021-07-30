package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.BadRequestException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceListResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceConverter resourceConverter;
    private final PositionRepository positionRepository;

    private static final String TEAM_PARAMETER = "positionEntity.teamEntity.name";
    private static final String POSITION_PARAMETER = "positionEntity.name";
    private static final String RESOURCE_NAME_PARAMETER = "name";
    private static final String CREATED_DATE_PARAMETER = "createdDate";
    private static final String STATUS_PARAMETER = "isArchived";


    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               ResourceConverter resourceConverter,
                               PositionRepository positionRepository) {
        this.resourceRepository = resourceRepository;
        this.resourceConverter = resourceConverter;
        this.positionRepository = positionRepository;
    }

    @Override
    public MessageResponse addNewResource(ResourceRequest resourceRequest, Integer accountId) {
        Date currentDate = new Date();
        ResourceEntity resourceEntity = new ResourceEntity();
        if(resourceRequest.getName().equals("") || resourceRequest.getName() == null){
            throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
        }
        resourceEntity.setName(resourceRequest.getName());
        if(resourceRequest.getAvatar() == null){
            resourceEntity.setAvatar("");
        }
        resourceEntity.setAvatar(resourceRequest.getAvatar());
        resourceEntity.setPositionEntity(positionRepository.findById(resourceRequest.getPositionId()).orElseThrow(()
                -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
        resourceEntity.setCreatedDate(currentDate);
        resourceEntity.setCreatedBy(accountId);
        resourceEntity.setModifiedDate(currentDate);
        resourceEntity.setModifiedBy(accountId);
        resourceRepository.save(resourceEntity);
        return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
    }

    @Override
    public ResourceDTO findById(Integer id) {
        ResourceEntity resourceEntity = new ResourceEntity();
        if (resourceRepository.findById(id).isPresent()) {
            resourceEntity = resourceRepository.findById(id).get();
        }
        return resourceConverter.convertToDto(resourceEntity);
    }

    @Override
    public MessageResponse updateResource(ResourceRequest resourceRequest, Integer resourceId, Integer workspaceId, Integer accountId) {
        if (resourceRepository.findByPositionEntity_TeamEntity_WorkspaceEntityTeam_IdAndId(workspaceId, resourceId).isPresent()) {
            ResourceEntity resourceEntityTarget = resourceRepository.findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(resourceId,workspaceId).get();
            resourceEntityTarget.setId(resourceRequest.getId());
            resourceEntityTarget.setModifiedBy(accountId);
            Date currentDate = new Date();
            resourceEntityTarget.setModifiedDate(currentDate);
            if(resourceRequest.getAvatar() == null) {
                resourceEntityTarget.setAvatar("");
            }
            resourceEntityTarget.setAvatar(resourceRequest.getAvatar());
            if(resourceRequest.getName().equals("") || resourceRequest.getName() == null){
                throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
            }
            resourceEntityTarget.setName(resourceRequest.getName());
            resourceEntityTarget.setPositionEntity(positionRepository.findById(resourceRequest.getPositionId()).orElseThrow(()
                    -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
            resourceRepository.save(resourceEntityTarget);
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
    }

    @Override
    public MessageResponse archiveResource(Integer resourceId, Integer workspaceId){
        if (resourceRepository.findByPositionEntity_TeamEntity_WorkspaceEntityTeam_IdAndId(workspaceId, resourceId).isPresent()) {
            ResourceEntity resourceEntityTarget = resourceRepository
                    .findByPositionEntity_TeamEntity_WorkspaceEntityTeam_IdAndId(workspaceId, resourceId).get();
            resourceEntityTarget.setIsArchived(!resourceEntityTarget.getIsArchived());
            resourceRepository.save(resourceEntityTarget);
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
    }

    @Override
    public MessageResponse deleteResource(Integer id, Integer workspaceId) {
        if (resourceRepository.findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(id, workspaceId).isPresent()) {
            resourceRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
    }

    @Override
    public List<ResourceDTO> getResourcesOfWorkSpace(Integer id) {
        List<ResourceDTO> resourceDTOS = new ArrayList<>();
        List<ResourceEntity> resourceEntityList = resourceRepository.findAllByPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(id);
        for (ResourceEntity resourceEntity : resourceEntityList) {
            resourceDTOS.add(resourceConverter.convertToDto(resourceEntity));
        }
        return resourceDTOS;
    }

    @Override
    public List<ResourceDTO> searchByName(String name, String posName, String teamName,
                                          Integer workspaceId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ResourceDTO> result = new ArrayList<>();
        Page<ResourceEntity> found = resourceRepository.
                filterResultByParameter(name, posName, teamName, workspaceId, pageable);
        List<ResourceEntity> resourceEntityList = found.getContent();
        for (ResourceEntity resourceEntity : resourceEntityList) {
            result.add(resourceConverter.convertToDto(resourceEntity));
        }
        return result;
    }


    @Override
    public ResourceDTO getResourceInfo(Integer resourceId, Integer workspaceId) {
        if (resourceRepository.findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(resourceId, workspaceId).isPresent()) {
            return resourceConverter.convertToDto(resourceRepository
                    .findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(resourceId, workspaceId).get());
        } else {
            return null;
        }
    }


//    @Override
//    public List<ResourceDTO> sortResources(Integer idWorkspace, String searchName, String teamName, String posName,
//                                           String sortColumn, String type, Integer page, Integer size){
//        switch (sortColumn) {
//            case "team":
//                sortColumn = TEAM_PARAMETER;
//                break;
//            case "position":
//                sortColumn = POSITION_PARAMETER;
//                break;
//            case "name":
//                sortColumn = RESOURCE_NAME_PARAMETER;
//                break;
//            default:
//                sortColumn = CREATED_DATE_PARAMETER;
//                break;
//        }
//        Page<ResourceEntity> resourceEntityPage;
//        Pageable pageable;
//        if (type.equals(SortPara.ASC.getName())) {
//            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortColumn));
//        }else {
//            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortColumn));
//        }
//        resourceEntityPage = resourceRepository
//                .filterList(idWorkspace, searchName, teamName, posName, pageable);
//        List<ResourceEntity> resourceEntityList = resourceEntityPage.getContent();
//        List<ResourceDTO> result = new ArrayList<>();
//        for (ResourceEntity resourceEntity : resourceEntityList) {
//            result.add(resourceConverter.convertToDto(resourceEntity));
//        }
//        return result;
//    }

    @Override
    public List<ResourceDTO> sortResources(Integer idWorkspace, String searchName, String isArchived,
                                           String sortColumn, String type, Integer page, Integer size){
        if (sortColumn.equals(ColumnPara.TEAM.getName())){
            sortColumn = TEAM_PARAMETER;
        }
        else if (sortColumn.equals(ColumnPara.POSITION.getName())){
            sortColumn = POSITION_PARAMETER;
        }
        else if (sortColumn.equals(ColumnPara.NAME.getName())){
            sortColumn = RESOURCE_NAME_PARAMETER;
        }
        else if (sortColumn.equals(ColumnPara.STATUS.getName())){
            sortColumn = STATUS_PARAMETER;
            if (type.equals(SortPara.ASC.getName())){
                type = SortPara.DESC.getName();
            }
            else {
                type = SortPara.ASC.getName();
            }
        }
        else {
            sortColumn = CREATED_DATE_PARAMETER;
        }
        Page<ResourceEntity> resourceEntityPage;
        Pageable pageable;
        if (type.equals(SortPara.ASC.getName())) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortColumn));
        }else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortColumn));
        }
        if (isArchived.equals(StatusPara.ARCHIVED.getName())){
            resourceEntityPage = resourceRepository
                    .filterListByStatus(idWorkspace, searchName, true, pageable);
        }
        else if (isArchived.equals(StatusPara.ACTIVE.getName())){
            resourceEntityPage = resourceRepository
                    .filterListByStatus(idWorkspace, searchName, false, pageable);
        }
        else {
            resourceEntityPage = resourceRepository
                    .filterList(idWorkspace, searchName, pageable);
        }
        List<ResourceEntity> resourceEntityList = resourceEntityPage.getContent();
        List<ResourceDTO> result = new ArrayList<>();
        for (ResourceEntity resourceEntity : resourceEntityList) {
            result.add(resourceConverter.convertToDto(resourceEntity));
        }
        return result;
    }


    @Override
    public Integer getNumberOfResources(Integer idWorkspace, String searchName, String isArchived){
        if (isArchived.equals("true")){
            return resourceRepository.getNumberOfResourcesOfWorkspaceWithStatus(idWorkspace, true, searchName);
        }
        else if (isArchived.equals("false")){
            return resourceRepository.getNumberOfResourcesOfWorkspaceWithStatus(idWorkspace, false, searchName);
        }
        else {
            return resourceRepository.getNumberOfResourcesOfWorkspace(idWorkspace, searchName);
        }
    }

}

