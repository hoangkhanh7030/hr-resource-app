package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ResourceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.BadRequestException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceConverter resourceConverter;
    private final PositionRepository positionRepository;
    private final WorkspaceRepository workspaceRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;


    private static final String TEAM_PARAMETER = "positionEntity.teamEntity.name";
    private static final String POSITION_PARAMETER = "positionEntity.name";
    private static final String RESOURCE_NAME_PARAMETER = "name";
    private static final String CREATED_DATE_PARAMETER = "createdDate";
    private static final String STATUS_PARAMETER = "isArchived";


    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               ResourceConverter resourceConverter,
                               PositionRepository positionRepository,
                               WorkspaceRepository workspaceRepository,
                               TeamRepository teamRepository,
                               ModelMapper modelMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceConverter = resourceConverter;
        this.positionRepository = positionRepository;
        this.workspaceRepository = workspaceRepository;
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public MessageResponse addNewResource(ResourceRequest resourceRequest, Integer accountId, Integer workspaceId) {
        Date currentDate = new Date();
        ResourceEntity resourceEntity = new ResourceEntity();
        TeamEntity teamEntity = teamRepository.findById(resourceRequest.getTeamId()).orElse(null);
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(workspaceId).orElse(null);
        if (resourceRequest.getName().equals("") || resourceRequest.getName() == null) {
            throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
        }
        resourceEntity.setName(resourceRequest.getName());
        if (resourceRequest.getAvatar() == null) {
            resourceEntity.setAvatar("");
        }
        resourceEntity.setAvatar(resourceRequest.getAvatar());
        resourceEntity.setPositionEntity(positionRepository.findById(resourceRequest.getPositionId()).orElseThrow(()
                -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
        resourceEntity.setCreatedDate(currentDate);
        resourceEntity.setCreatedBy(accountId);
        resourceEntity.setWorkspaceEntityResource(workspaceEntity);
        resourceEntity.setTeamEntityResource(teamEntity);
        resourceEntity.setModifiedDate(currentDate);
        resourceEntity.setModifiedBy(accountId);
        resourceRepository.save(resourceEntity);
        return new MessageResponse(ResponseMessage.CREATE_RESOURCE_SUCCESS, Status.SUCCESS.getCode());
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
        if (resourceRepository.findByWorkspaceEntityResource_IdAndId(workspaceId, resourceId).isPresent()) {
            ResourceEntity resourceEntityTarget = resourceRepository.findByWorkspaceEntityResource_IdAndId(workspaceId, resourceId).get();
            PositionEntity positionEntity = resourceEntityTarget.getPositionEntity();
            TeamEntity teamEntity = resourceEntityTarget.getTeamEntityResource();
            resourceEntityTarget.setId(resourceRequest.getId());
            resourceEntityTarget.setModifiedBy(accountId);
            Date currentDate = new Date();
            resourceEntityTarget.setModifiedDate(currentDate);
            if (resourceRequest.getAvatar() == null) {
                resourceEntityTarget.setAvatar("");
            }
            resourceEntityTarget.setAvatar(resourceRequest.getAvatar());
            if (resourceRequest.getName().equals("") || resourceRequest.getName() == null) {
                throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
            }
            resourceEntityTarget.setName(resourceRequest.getName());
            resourceEntityTarget.setPositionEntity(positionRepository.findById(resourceRequest.getPositionId()).orElseThrow(()
                    -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
            resourceEntityTarget.setTeamEntityResource(teamRepository.findById(resourceRequest.getTeamId()).orElseThrow(()
                    -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
            if (teamEntity.getIsArchived()) {
                TeamRequest teamRequest = new TeamRequest();
                teamRequest.setId(teamEntity.getId());
                teamRequest.setName(teamEntity.getName());
                deleteOneTeam(teamRequest);
            }
            if (positionEntity.getIsArchived()) {
                PositionRequest positionRequest = new PositionRequest();
                positionRequest.setId(positionEntity.getId());
                positionRequest.setName(positionEntity.getName());
                deleteOne(positionRequest);
            }
            resourceRepository.save(resourceEntityTarget);
            return new MessageResponse(ResponseMessage.UPDATE_RESOURCE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.UPDATE_RESOURCE_FAIL, Status.FAIL.getCode());
    }

    private void deleteOneTeam(TeamRequest teamRequest) {
        TeamEntity teamEntity = teamRepository.findById(teamRequest.getId()).orElse(null);
        if (teamEntity != null) {
            if (resourceRepository
                    .countAllByWorkspaceEntityResource_IdAndTeamEntityResource_Id
                            (teamEntity.getWorkspaceEntityTeam().getId(), teamEntity.getId()) == 0) {
                for (PositionEntity positionEntity : teamEntity.getPositionEntities()) {
                    positionRepository.deleteById(positionEntity.getId());
                }
                teamRepository.deleteById(teamEntity.getId());
            } else {
                teamEntity.setIsArchived(true);
                for (PositionEntity positionEntity : teamEntity.getPositionEntities()) {
                    positionEntity.setIsArchived(true);
                    positionRepository.save(positionEntity);
                }
                teamRepository.save(teamEntity);
            }
        }
    }

    private void deleteOne(PositionRequest positionRequest) {
        PositionEntity positionEntity = positionRepository.findById(positionRequest.getId()).orElse(null);
        if (positionEntity != null) {
            if (resourceRepository
                    .countResourcesOfPosition
                            (positionEntity.getId(), positionEntity.getTeamEntity().getWorkspaceEntityTeam().getId()) == 0) {
                resourceRepository.deleteById(positionEntity.getId());
            } else {
                positionEntity.setIsArchived(true);
                positionRepository.save(positionEntity);
            }
        }
    }

    @Override

    public MessageResponse archiveResource(Integer resourceId, Integer workspaceId) {

        ResourceEntity resourceEntityTarget = resourceRepository
                .findByWorkspaceEntityResource_IdAndId(workspaceId, resourceId).get();
        resourceEntityTarget.setIsArchived(!resourceEntityTarget.getIsArchived());
        resourceRepository.save(resourceEntityTarget);
        if (resourceEntityTarget.getIsArchived().equals(Boolean.FALSE)) {
            return new MessageResponse(ResponseMessage.ENABLE_RESOURCE, Status.SUCCESS.getCode());
        } else {
            return new MessageResponse(ResponseMessage.ARCHIVED_RESOURCE, Status.SUCCESS.getCode());
        }


    }

    @Override
    public MessageResponse deleteResource(Integer id, Integer workspaceId) {
        if (resourceRepository.findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(id, workspaceId).isPresent()) {
            resourceRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_RESOURCE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_RESOURCE_FAIL, Status.FAIL.getCode());
    }

    @Override
    public List<ResourceDTO> getResourcesOfWorkSpace(Integer id) {
        List<ResourceDTO> resourceDTOS = new ArrayList<>();
        List<ResourceEntity> resourceEntityList = resourceRepository.findAllByWorkspaceEntityResource_Id(id);
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
        if (resourceRepository.findByIdAndWorkspaceEntityResource_Id(resourceId, workspaceId).isPresent()) {
            return resourceConverter.convertToDto(resourceRepository
                    .findByIdAndWorkspaceEntityResource_Id(resourceId, workspaceId).get());
        } else {
            return null;
        }
    }


    @Override
    public List<ResourceDTO> sortResources(Integer idWorkspace, String searchName, String isArchived,
                                           String sortColumn, String type, Integer page, Integer size) {
        if (sortColumn.equals(ColumnPara.TEAM.getName())) {
            sortColumn = TEAM_PARAMETER;
        } else if (sortColumn.equals(ColumnPara.POSITION.getName())) {
            sortColumn = POSITION_PARAMETER;
        } else if (sortColumn.equals(ColumnPara.NAME.getName())) {
            sortColumn = RESOURCE_NAME_PARAMETER;
        } else if (sortColumn.equals(ColumnPara.STATUS.getName())) {
            sortColumn = STATUS_PARAMETER;
            if (type.equals(SortPara.ASC.getName())) {
                type = SortPara.DESC.getName();
            } else {
                type = SortPara.ASC.getName();
            }
        } else {
            sortColumn = CREATED_DATE_PARAMETER;
        }
        Page<ResourceEntity> resourceEntityPage;
        Pageable pageable;
        if (type.equals(SortPara.ASC.getName())) {
//            Sort.NullHandling.NULLS_LAST;
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortColumn));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortColumn));
        }
        if (isArchived.equals(StatusPara.ARCHIVED.getName())) {
            resourceEntityPage = resourceRepository
                    .filterListByStatus(idWorkspace, searchName, true, pageable);
        } else if (isArchived.equals(StatusPara.ACTIVE.getName())) {
            resourceEntityPage = resourceRepository
                    .filterListByStatus(idWorkspace, searchName, false, pageable);
        } else {
            resourceEntityPage = resourceRepository
                    .filterList(idWorkspace, searchName, pageable);
        }
        List<ResourceEntity> resourceEntityList = resourceEntityPage.getContent();
        for (ResourceEntity r : resourceEntityList) {
            System.out.println(r.getId());
            if (r.getPositionEntity() == null) {
                System.out.println("Position null");
            }
            if (r.getTeamEntityResource() == null) {
                System.out.println("Team null");
            }
            System.out.println(r.getTeamEntityResource().getName());
            System.out.println(r.getPositionEntity().getName());
        }
        List<ResourceDTO> result = new ArrayList<>();
        for (ResourceEntity resourceEntity : resourceEntityList) {
            result.add(resourceConverter.convertToDto(resourceEntity));
        }
        return result;
    }


    @Override
    public Integer getNumberOfResources(Integer idWorkspace, String searchName, String isArchived) {
        if (isArchived.equals(StatusPara.ARCHIVED.getName())) {
            return resourceRepository.getNumberOfResourcesOfWorkspaceWithStatus(idWorkspace, true, searchName);
        } else if (isArchived.equals(StatusPara.ACTIVE.getName())) {
            return resourceRepository.getNumberOfResourcesOfWorkspaceWithStatus(idWorkspace, false, searchName);
        } else {
            return resourceRepository.getNumberOfResourcesOfWorkspace(idWorkspace, searchName);
        }
    }

    @Override
    public List<ResourceDTO> getAll(Integer idWorkspace, String searchName) {
        List<ResourceEntity> resourceEntities = resourceRepository.findAll(idWorkspace, searchName);
        return resourceEntities.stream().map(
                resourceEntity -> modelMapper.map(resourceEntity, ResourceDTO.class))
                .collect(Collectors.toList());
    }

}

