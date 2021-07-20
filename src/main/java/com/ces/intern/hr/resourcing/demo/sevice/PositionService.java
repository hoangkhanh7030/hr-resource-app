package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;

import java.util.List;

public interface PositionService {
    List<PositionDTO> getAll(Integer idWorkspace);
    List<PositionDTO> getAllByIdTeam(Integer idWorkspace,Integer idTeam);
    void updatePosition(List<PositionRequest> positionRequests,Integer idWorkspace,Integer idTeam);

}
