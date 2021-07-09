package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;

import java.util.List;

public interface PositionService {
    List<PositionDTO> getAll();
    void createPosition(PositionRequest positionRequest);
    void updatePosition(PositionRequest positionRequest,Integer idPosition);
    void deletePosition(Integer idPosition);
}
