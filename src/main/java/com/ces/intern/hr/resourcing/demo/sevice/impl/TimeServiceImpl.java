package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.TimeConverter;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TimeServiceImpl implements TimeService {
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 17;
    private final TimeConverter timeConverter;
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    private TimeServiceImpl(TimeConverter timeConverter,
                            TimeRepository timeRepository,
                            ProjectRepository projectRepository,
                            ResourceRepository resourceRepository){
        this.timeConverter = timeConverter;
        this.timeRepository = timeRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
    }


    @Override
    public MessageResponse addNewBooking(TimeRequest timeRequest){
        TimeEntity timeEntity = new TimeEntity();
        int start = timeRequest.getStartHour();
        int end = timeRequest.getEndHour();
        timeEntity.setTask(timeRequest.getTaskName());
        timeEntity.setProjectEntity(projectRepository.findById(timeRequest.getProjectId()).orElse(null));
        timeEntity.setResourceEntity(resourceRepository.findById(timeRequest.getResourceId()).orElse(null));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeRequest.getDate());
        if (start >= START_HOUR && end <= END_HOUR && start < end) {
            if (!timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).isPresent()) {
                setShift(timeEntity, start, end, calendar);
                timeRepository.save(timeEntity);
                return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
            } else {
                if(TimeCheck(timeRequest, start, end, calendar)){
                    setShift(timeEntity, start, end, calendar);
                    timeRepository.save(timeEntity);
                    return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
                }
                else {
                    return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
                }
            }
        }

        return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
    }

    @Override
    public MessageResponse updateBooking(TimeRequest timeRequest, Integer timeId){
        TimeEntity timeEntity = new TimeEntity();
        if(timeRepository.findById(timeId).isPresent()
        && projectRepository.findById(timeRequest.getProjectId()).isPresent()
        && resourceRepository.findById(timeRequest.getResourceId()).isPresent()){
            timeEntity.setTask(timeRequest.getTaskName());
            timeEntity.setResourceEntity(resourceRepository.findById(timeRequest.getResourceId()).get());
            timeEntity.setProjectEntity(projectRepository.findById(timeRequest.getProjectId()).get());
            timeEntity.setId(timeId);
        }
        else {
            return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
        }
        int start = timeRequest.getStartHour();
        int end = timeRequest.getEndHour();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeRequest.getDate());
        if(start >= START_HOUR && end <= END_HOUR && start < end){
            if (timeRepository.findAllDifferentShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId(), timeId).isPresent()){
                if(TimeCheck(timeRequest, start, end, calendar)){
                    setShift(timeEntity, start, end, calendar);
                    timeRepository.save(timeEntity);
                    return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                }
                else {
                    return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
                }
            }
            else {
                return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
            }
        }
        return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
    }

    private boolean TimeCheck(TimeRequest timeRequest, int start, int end, Calendar calendar) {
        List<TimeEntity> listTime = timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).orElse(new ArrayList<>());
        boolean check = true;
        Calendar shiftStart = Calendar.getInstance();
        Calendar shiftEnd = Calendar.getInstance();
        for (TimeEntity t : listTime) {
            shiftStart.setTime(t.getStartTime());
            shiftEnd.setTime(t.getEndTime());
            if (shiftStart.get(Calendar.HOUR_OF_DAY) > start && shiftStart.get(Calendar.HOUR_OF_DAY) < end) {
                check = false;
                break;
            } else if (shiftEnd.get(Calendar.HOUR_OF_DAY) > start && shiftEnd.get(Calendar.HOUR_OF_DAY) < end) {
                check = false;
                break;
            } else if (start > shiftStart.get(Calendar.HOUR_OF_DAY) && start < shiftEnd.get(Calendar.HOUR_OF_DAY)) {
                check = false;
                break;
            } else if (end > shiftStart.get(Calendar.HOUR_OF_DAY) && end < shiftEnd.get(Calendar.HOUR_OF_DAY)) {
                check = false;
                break;
            }
        }

        return check;
    }

    @Override
    public MessageResponse deleteBooking(Integer id){
        if(timeRepository.findById(id).isPresent()){
            timeRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
    }


    @Override
    public Map<Date, List<TimeDTO>> getBookingByMonth(Integer month, Integer year, Integer workspaceId){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //int maxDaysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Map<Date, List<TimeDTO>> bookingMonth = new LinkedHashMap<>();
        while (calendar.get(Calendar.MONTH) + 1 == month) {
            List<TimeDTO> list = new ArrayList<>();
            Date date = calendar.getTime();
            List<TimeEntity> timeEntities = timeRepository.findAllShiftOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), workspaceId).orElse(new ArrayList<>());
            for (TimeEntity t : timeEntities) {
                list.add(timeConverter.convertToDto(t));
            }
            bookingMonth.put(date, list);
            calendar.add(Calendar.DATE, 1);
        }
        return bookingMonth;
    }

    private void setShift(TimeEntity timeEntity, Integer start, Integer end, Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, start);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeEntity.setStartTime(calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, end);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeEntity.setEndTime(calendar.getTime());
    }
}
