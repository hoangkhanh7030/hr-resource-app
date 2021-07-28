package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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



    @GetMapping(value = "/{idWorkspace}/team/{idTeam}/position")
    private List<PositionDTO> getAllByIdTeam(@PathVariable Integer idWorkspace,
                                             @PathVariable Integer idTeam) {
        return positionService.getAllByIdTeam(idWorkspace, idTeam);
    }


}

