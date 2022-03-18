package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardResponse;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardListResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;

import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("api/v1/workspaces")
public class TimeController {
    private final TimeService timeService;
    private final ProjectService projectService;
    private final ResourceService resourceService;


    @Autowired
    private TimeController(
            TimeService timeService,
            ProjectService projectService,
            ResourceService resourceService) {
        this.timeService = timeService;
        this.projectService = projectService;
        this.resourceService = resourceService;
    }


    @DeleteMapping("/{workspaceId}/bookings/{timeId}")
    public MessageResponse deleteBooking(@PathVariable Integer timeId) {
            return timeService.deleteBooking(timeId);

    }

    @PostMapping("/{idWorkspace}/bookings")
    public MessageResponse addBooking(@RequestBody BookingRequest bookingRequest,
                                      @PathVariable Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(bookingRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(bookingRequest.getEndDate());
        if (bookingRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            if (startDay.getTime() > endDay.getTime()) {
                return new MessageResponse(ResponseMessage.WRONG_TIME, Status.FAIL.getCode());
            } else {
                timeService.newBooking(bookingRequest, idWorkspace);
                return new MessageResponse(ResponseMessage.CREATE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
            }
        }

    }

    @PutMapping("/{idWorkspace}/bookings")
    public MessageResponse updateBookings(@RequestBody BookingRequest bookingRequest,
                                          @PathVariable Integer idWorkspace) throws ParseException {
        return timeService.updateBooking(bookingRequest, idWorkspace);


    }

    @GetMapping("/{idWorkspace}/bookings/projects")
    public List<ProjectDTO> getAllProject(@PathVariable Integer idWorkspace,
                                          @RequestParam String searchName) {
        return projectService.getAll(idWorkspace, searchName);
    }

    @GetMapping("/{idWorkspace}/bookings/resources")
    public List<ResourceDTO> getAllResource(@PathVariable Integer idWorkspace,
                                            @RequestParam String searchName) {
        return resourceService.getAll(idWorkspace, searchName);
    }

    @GetMapping("/{idWorkspace}/booking/{idBooking}")
    public DashboardResponse getBooking(@PathVariable Integer idWorkspace,
                                        @PathVariable Integer idBooking) {
        return timeService.getBooking(idWorkspace, idBooking);
    }



    @GetMapping("/{idWorkspace}/bookings")
    public DashboardListResponse search(@PathVariable Integer idWorkspace,
                                        @RequestParam String searchName,
                                        @RequestParam String startDate,
                                        @RequestParam String endDate) throws ParseException {
        return timeService.searchBooking(idWorkspace, startDate, endDate, searchName);
    }
}
