package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository,
                               ModelMapper modelMapper) {
        this.positionRepository = positionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PositionDTO> getAll(Integer idWorkspace) {
        List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspace(idWorkspace);
        return positionEntities.stream().map(s -> modelMapper.map(s, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PositionDTO> getAllByIdTeam(Integer idWorkspace, Integer idTeam) {
        List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspaceAndidTeam(idWorkspace,idTeam);
        return positionEntities.stream().map(s -> modelMapper.map(s, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void updatePosition(List<PositionRequest> positionRequests) {
        List<PositionEntity> positionEntities = positionRepository.findAll();
        deletePosition(positionRequests,positionEntities);
        for (PositionRequest positionRequest : positionRequests){
            if (!positionRepository.findByName(positionRequest.getName()).isPresent()){
                PositionEntity positionEntity = new PositionEntity();
                positionEntity.setName(positionRequest.getName());
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
