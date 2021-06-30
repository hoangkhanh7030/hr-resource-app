package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;

public interface TimeService {
    void addNewBooking(TimeRequest timeRequest);

    void updateBooking(TimeRequest timeRequest, Integer timeId);
}
