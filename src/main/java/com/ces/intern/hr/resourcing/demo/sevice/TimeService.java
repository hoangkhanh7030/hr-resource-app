package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimeService {
    MessageResponse addNewBooking(TimeRequest timeRequest);

    MessageResponse updateBooking(TimeRequest timeRequest, Integer timeId);

    MessageResponse deleteBooking(Integer id);

    Map<Date, List<TimeDTO>> getBookingByMonth(Integer month, Integer year, Integer workspaceId);


}
