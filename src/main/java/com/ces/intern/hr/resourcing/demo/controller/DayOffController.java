package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.request.DayOffRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.sevice.DayOffService;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("api/v1/workspaces")
public class DayOffController {
    private final DayOffService dayOffService;
    private final ProjectService projectService;
    private final ResourceService resourceService;


    @Autowired
    private DayOffController(
            DayOffService dayOffService,
            ProjectService projectService,
            ResourceService resourceService) {
        this.dayOffService = dayOffService;
        this.projectService = projectService;
        this.resourceService = resourceService;
    }
    @DeleteMapping("/dayOff/{dayOffId}")
    public MessageResponse deleteDayOff(@PathVariable Integer dayOffId) {
        return dayOffService.deleteDayOff(dayOffId);

    }
    @PostMapping("/{idWorkspace}/dayOff")
    public MessageResponse addDayOff(@RequestBody DayOffRequest dayOffRequest,
                                      @PathVariable Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(dayOffRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(dayOffRequest.getEndDate());
        if (dayOffRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            if (startDay.getTime() > endDay.getTime()) {
                return new MessageResponse(ResponseMessage.WRONG_TIME, Status.FAIL.getCode());
            } else {
                dayOffService.newDayOff(dayOffRequest, idWorkspace);
                return new MessageResponse(ResponseMessage.CREATE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
            }
        }

    }
    @PutMapping("/{idWorkspace}/dayOff")
    public MessageResponse updateDayOff(@RequestBody DayOffRequest dayOffRequest,
                                          @PathVariable Integer idWorkspace) throws ParseException {
        return dayOffService.updateDayOff(dayOffRequest, idWorkspace);


    }
}
