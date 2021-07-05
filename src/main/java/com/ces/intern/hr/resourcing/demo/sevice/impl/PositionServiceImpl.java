package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.sevice.PositionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    @Override
    public List<PositionDTO> getAll() {
        return null;
    }

    @Override
    public void createPosition(PositionRequest positionRequest) {

    }

    @Override
    public void updatePosition(PositionRequest positionRequest) {

    }

    @Override
    public void deletePosition(Integer idPosition) {

    }
}
