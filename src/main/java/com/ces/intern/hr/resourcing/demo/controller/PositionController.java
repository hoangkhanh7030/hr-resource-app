package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
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
    private final TeamRepository teamRepository;

    @Autowired
    public PositionController(PositionRepository positionRepository,
                              PositionService positionService,
                              ModelMapper modelMapper,
                              TeamRepository teamRepository) {
        this.positionRepository = positionRepository;
        this.positionService = positionService;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
    }

    @GetMapping(value = "/{idWorkspace}/position")
    private List<PositionDTO> getAll(@PathVariable Integer idWorkspace) {

        return positionService.getAll(idWorkspace);
    }

    @GetMapping(value = "/{idWorkspace}/team/{idTeam}/position")
    private List<PositionDTO> getAllByIdTeam(@PathVariable Integer idWorkspace,
                                             @PathVariable Integer idTeam) {
        return positionService.getAllByIdTeam(idWorkspace, idTeam);
    }


    @PutMapping(value = "/{idWorkspace}/team/{idTeam}/position")
    private MessageResponse updatePosition(@RequestBody List<PositionRequest> positionRequestList,
                                           @PathVariable Integer idWorkspace,
                                           @PathVariable Integer idTeam
    ) {
        if (positionRequestList.isEmpty()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            if (teamRepository.findByidWorkspaceAndIdTeam(idWorkspace, idTeam).isPresent()) {
                positionService.updatePosition(positionRequestList, idWorkspace, idTeam);
                List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspaceAndidTeam(idWorkspace, idTeam);
                List<PositionRequest> list = positionEntities.stream().map(s -> modelMapper.map(s, PositionRequest.class)).collect(Collectors.toList());
                if (listEquals(list, positionRequestList)) {
                    return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                } else {
                    return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
                }
            } else return new MessageResponse(ResponseMessage.NOT_FOUND, Status.FAIL.getCode());
        }


    }

    private static boolean listEquals(List<PositionRequest> list1, List<PositionRequest> list2) {
        if (list1.size() != list2.size())
            return true;

        for (PositionRequest positionRequest : list1) {
            if (!list2.contains(positionRequest))
                return true;
        }
        return false;
    }


}

