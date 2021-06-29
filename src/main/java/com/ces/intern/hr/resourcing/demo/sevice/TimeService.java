package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;

public interface TimeService {
    void addNewBooking(TimeRequest timeRequest, Integer start, Integer end);
}
