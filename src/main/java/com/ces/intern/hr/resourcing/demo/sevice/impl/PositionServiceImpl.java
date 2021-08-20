package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final TeamRepository teamRepository;
    private final ResourceRepository resourceRepository;
    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository,
                               ModelMapper modelMapper,
                               WorkspaceRepository workspaceRepository,
                               TeamRepository teamRepository,
                               ResourceRepository resourceRepository) {
        this.positionRepository = positionRepository;
        this.modelMapper = modelMapper;
        this.teamRepository=teamRepository;
        this.workspaceRepository=workspaceRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public List<PositionDTO> getAll(Integer idWorkspace) {
        //List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspace(idWorkspace);
        List<PositionEntity> positionEntities = positionRepository.findAllActiveByIdWorkspace(idWorkspace);
        return positionEntities.stream().map(s -> modelMapper.map(s, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> getAllByIdTeam(Integer idWorkspace, Integer idTeam) {
        List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspaceAndidTeam(idWorkspace,idTeam);
        return positionEntities.stream().map(s -> modelMapper.map(s, PositionDTO.class)).collect(Collectors.toList());
    }


    @Override
    public void deleteMultiple(List<PositionRequest> positionRequests) {
        List<PositionEntity> positionEntities = new ArrayList<>();
        for (PositionRequest positionRequest : positionRequests){
            PositionEntity positionEntity = positionRepository.findById(positionRequest.getId()).orElse(null);
            if (positionEntity != null){
                positionEntities.add(positionEntity);
            }
        }
        for (PositionEntity positionEntity : positionEntities){
            if (resourceRepository
                    .countResourcesOfPosition
                            (positionEntity.getId(), positionEntity.getTeamEntity().getWorkspaceEntityTeam().getId()) == 0){
                resourceRepository.deleteById(positionEntity.getId());
            }
            else {
                positionEntity.setIsArchived(true);
                positionRepository.save(positionEntity);
            }
        }
    }

    private void deletePosition(List<PositionRequest> positionRequests,List<PositionEntity> positionEntities){

        for (PositionEntity positionEntity: positionEntities){
            if (positionRequests.stream().filter(s->positionEntity.getName().equals(s.getName())).findAny().orElse(null)==null){
                positionRepository.delete(positionEntity);
            }
        }
    }


}
