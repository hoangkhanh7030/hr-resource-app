package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/workspaces")
public class PositionController {
    private final PositionRepository positionRepository;
    private final PositionService positionService;

    @Autowired
    public PositionController(PositionRepository positionRepository,
                              PositionService positionService) {
        this.positionRepository = positionRepository;
        this.positionService = positionService;
    }

    @GetMapping(value = "/position")
    private List<PositionDTO> getAll() {
        return positionService.getAll();
    }

    @PostMapping(value = "/position")
    private MessageResponse createPosition(@RequestBody PositionRequest positionRequest) {
        if (positionRepository.findByName(positionRequest.getName()).isPresent()) {
            return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
        } else {
            positionService.createPosition(positionRequest);
            if (positionRepository.findByName(positionRequest.getName()).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
            } else {
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
        }

    }

    @PutMapping(value = "/position/{idPosition}")
    private MessageResponse updatePosition(@RequestBody PositionRequest positionRequest,
                                           @PathVariable Integer idPosition) {
        if (positionRepository.findByName(positionRequest.getName()).isPresent()) {
            positionService.updatePosition(positionRequest, idPosition);
            if (positionRepository.findByName(positionRequest.getName()).isPresent()) {
                return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
            } else {
                return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
            }

        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }
    }

    @DeleteMapping(value = "/position/{idPosition}")
    private MessageResponse deletePosition(@PathVariable Integer idPosition) {
        if (positionRepository.findById(idPosition).isPresent()) {
            positionService.deletePosition(idPosition);
            if (positionRepository.findById(idPosition).isPresent()) {
                return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
            } else {
                return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
            }
        } else {
            return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }
    }
}

