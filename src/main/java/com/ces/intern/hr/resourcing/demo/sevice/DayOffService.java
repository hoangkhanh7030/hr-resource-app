package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.request.DayOffRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;

import java.text.ParseException;

public interface DayOffService {
    MessageResponse deleteDayOff(Integer id);

    void newDayOff(DayOffRequest dayOffRequest, Integer idWorkspace) throws ParseException;

    MessageResponse updateDayOff(DayOffRequest dayOffRequest, Integer idWorkspace) throws ParseException;

}
