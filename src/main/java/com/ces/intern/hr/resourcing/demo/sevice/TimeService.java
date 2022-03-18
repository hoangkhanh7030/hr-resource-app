package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardResponse;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardListResponse;

import java.text.ParseException;

public interface TimeService {

    MessageResponse deleteBooking(Integer id);

    void newBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException;

    MessageResponse updateBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException;

    DashboardResponse getBooking(Integer idWorkspace,Integer idBooking);
    DashboardListResponse searchBooking(Integer idWorkspace,String startDate,String endDate,
                                        String searchName) throws ParseException;
}
