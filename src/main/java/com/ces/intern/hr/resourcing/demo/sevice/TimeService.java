package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;

import java.util.Date;
import java.util.List;

public interface TimeService {
    MessageResponse addNewBooking(TimeRequest timeRequest);

    MessageResponse updateBooking(TimeRequest timeRequest, Integer timeId);

    List<TimeDTO> showBookingByWeek(Date date, Integer workspaceId);

    MessageResponse deleteBooking(Integer id);
}
