package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
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
    public List<PositionDTO> getAll() {
        List<PositionEntity> positionEntityList = positionRepository.findAll();
        return positionEntityList.stream().map(s -> modelMapper.map(s, PositionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void createPosition(PositionRequest positionRequest) {
        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setName(positionRequest.getName());
        positionRepository.save(positionEntity);

    }

    @Override
    public void updatePosition(PositionRequest positionRequest, Integer idPosition) {
        PositionEntity positionEntity = positionRepository.findById(idPosition)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        positionEntity.setName(positionRequest.getName());
        positionRepository.save(positionEntity);

    }

    @Override
    public void deletePosition(Integer idPosition) {
        PositionEntity positionEntity = positionRepository.findById(idPosition)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        positionRepository.delete(positionEntity);
    }
}
