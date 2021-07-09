package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
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

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/v1/workspaces")
public class TimeController {
    private final TimeService timeService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;


    @Autowired
    private TimeController(
            TimeService timeService,
            AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository) {
        this.timeService = timeService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }


    @GetMapping("/{workspaceId}/bookings")
    public Map<Date, List<TimeDTO>> showBookingForRangeOfDays(@PathVariable Integer workspaceId,
                                                              @RequestParam Integer month,
                                                              @RequestParam Integer year) {
        return timeService.getBookingByMonth(month, year, workspaceId);
    }

    @PostMapping("/{workspaceId}/bookings/{resourceId}")
    public MessageResponse addNewBooking(@RequestBody TimeRequest timeRequest,
                                         @PathVariable Integer resourceId,
                                         @PathVariable Integer workspaceId,
                                         @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            timeRequest.setResourceId(resourceId);
            return timeService.addNewBooking(timeRequest);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @PutMapping("/{workspaceId}/bookings/{timeId}")
    public MessageResponse editBooking(@RequestBody TimeRequest timeRequest,
                                       @PathVariable Integer timeId,
                                       @PathVariable Integer workspaceId,
                                       @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return timeService.updateBooking(timeRequest, timeId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
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
}
