package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class PositionController {
    private final PositionRepository positionRepository;
    private final PositionService positionService;
    private final ModelMapper modelMapper;

    @Autowired
    public PositionController(PositionRepository positionRepository,
                              PositionService positionService,
                              ModelMapper modelMapper) {
        this.positionRepository = positionRepository;
        this.positionService = positionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(value = "/position")
    private List<PositionDTO> getAll() {
        return positionService.getAll();
    }



    @PutMapping(value = "/position")
    private MessageResponse updatePosition(@RequestBody List<PositionRequest> positionRequestList
                                           ) {
        positionService.updatePosition(positionRequestList);
        List<PositionEntity> positionEntityList = positionRepository.findAll();
        List<PositionRequest> list = positionEntityList.stream().map(s->modelMapper.map(s,PositionRequest.class)).collect(Collectors.toList());
        if (listEquals(list, positionRequestList)){
            return new MessageResponse(ResponseMessage.UPDATE_SUCCESS,Status.SUCCESS.getCode());
        }else {
            return new MessageResponse(ResponseMessage.UPDATE_FAIL,Status.FAIL.getCode());
        }

    }
    private static boolean listEquals(List<PositionRequest> list1, List<PositionRequest> list2) {
        if(list1.size() != list2.size())
            return true;

        for (PositionRequest positionRequest : list1) {
            if(!list2.contains(positionRequest))
                return true;
        }
        return false;
    }



}

