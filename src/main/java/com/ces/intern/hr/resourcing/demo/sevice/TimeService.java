package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;

import java.util.Date;
import java.util.List;

public interface TimeService {
    void addNewBooking(TimeRequest timeRequest);

    void updateBooking(TimeRequest timeRequest, Integer timeId);

    List<TimeDTO> showBookingByWeek(Date date, Integer workspaceId);

    void deleteBooking(Integer id);
}
