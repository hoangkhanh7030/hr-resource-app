package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.BookingResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
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
import java.util.Map;


@RestController
@RequestMapping("api/v1/workspaces")
public class TimeController {
    private final TimeService timeService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final ProjectService projectService;
    private final ResourceService resourceService;


    @Autowired
    private TimeController(
            TimeService timeService,
            AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
            ProjectService projectService,
            ResourceService resourceService) {
        this.timeService = timeService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.projectService=projectService;
        this.resourceService=resourceService;
    }


    @GetMapping("/{workspaceId}/bookings")
    public Map<Date, List<TimeDTO>> showBookingForRangeOfDays(@PathVariable Integer workspaceId,
                                                              @RequestParam Integer month,
                                                              @RequestParam Integer year) {
        return timeService.getBookingByMonth(month, year, workspaceId);
    }




    @DeleteMapping("/{workspaceId}/bookings/{timeId}")
    public MessageResponse deleteBooking(@PathVariable Integer timeId,
                                         @PathVariable Integer workspaceId,
                                         @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return timeService.deleteBooking(timeId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }
    @PostMapping("/{idWorkspace}/bookings")
    public MessageResponse addBooking(@RequestBody BookingRequest bookingRequest,
                               @PathVariable Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(bookingRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(bookingRequest.getEndDate());
        if (startDay.getTime()>endDay.getTime()){
            return new MessageResponse(ResponseMessage.WRONG_TIME,Status.FAIL.getCode());
        }else {
            timeService.newBooking(bookingRequest,idWorkspace);
            return new MessageResponse(ResponseMessage.CREATE_SUCCESS,Status.SUCCESS.getCode());
        }
    }
    @PutMapping("/{idWorkspace}/bookings")
    public MessageResponse updateBookings(@RequestBody BookingRequest bookingRequest,
                               @PathVariable Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(bookingRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(bookingRequest.getEndDate());
        if (startDay.getTime()>endDay.getTime()){
            return new MessageResponse(ResponseMessage.WRONG_TIME,Status.FAIL.getCode());
        }else {
            timeService.updateBooking(bookingRequest,idWorkspace);
            return new MessageResponse(ResponseMessage.CREATE_SUCCESS,Status.SUCCESS.getCode());
        }

    }
    @GetMapping("/{idWorkspace}/bookings/projects")
    public List<ProjectDTO> getAllProject(@PathVariable Integer idWorkspace,
                                   @RequestParam String searchName){
        return projectService.getAll(idWorkspace,searchName);
    }
    @GetMapping("/{idWorkspace}/bookings/resources")
    public List<ResourceDTO> getAllResource(@PathVariable Integer idWorkspace,
                                            @RequestParam String searchName){
        return resourceService.getAll(idWorkspace,searchName);
    }
}
