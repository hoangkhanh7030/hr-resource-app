package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.response.DashboardResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.DashboardListResponse;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimeService {

    MessageResponse deleteBooking(Integer id);

    Map<Date, List<TimeDTO>> getBookingByMonth(Integer month, Integer year, Integer workspaceId);

    void newBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException;

    void updateBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException;

    DashboardResponse getBooking(Integer idWorkspace,Integer idBooking);
    DashboardListResponse searchBooking(Integer idWorkspace,String startDate,String endDate,String searchName) throws ParseException;
    DashboardListResponse getAllBooking(Integer idWorkspace, String startDate, String endDate) throws ParseException;
}
