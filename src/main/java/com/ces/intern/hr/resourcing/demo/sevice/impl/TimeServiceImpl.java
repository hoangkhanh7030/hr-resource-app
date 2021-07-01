package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ProjectConverter;
import com.ces.intern.hr.resourcing.demo.converter.TimeConverter;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 17;
    private final TimeConverter timeConverter;
    private final ProjectConverter projectConverter;
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    private TimeServiceImpl(TimeConverter timeConverter,
                            ProjectConverter projectConverter,
                            TimeRepository timeRepository,
                            ProjectRepository projectRepository,
                            ResourceRepository resourceRepository){
        this.timeConverter = timeConverter;
        this.projectConverter = projectConverter;
        this.timeRepository = timeRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
    }


    @Override
    public void addNewBooking(TimeRequest timeRequest){
        TimeEntity timeEntity = new TimeEntity();
        int start = timeRequest.getStartHour();
        int end = timeRequest.getEndHour();
        timeEntity.setProjectEntity(projectRepository.findById(timeRequest.getProjectId()).orElse(null));
        timeEntity.setResourceEntity(resourceRepository.findById(timeRequest.getResourceId()).orElse(null));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeRequest.getDate());
        if (start >= START_HOUR && end <= END_HOUR && start < end) {
            if (!timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).isPresent()) {
                setShift(timeEntity, start, end, calendar);
                timeRepository.save(timeEntity);
            } else {
                if(TimeCheck(timeRequest, timeEntity, start, end, calendar)){
                    setShift(timeEntity, start, end, calendar);
                    timeRepository.save(timeEntity);
                }
            }
        }
    }

    @Override
    public void updateBooking(TimeRequest timeRequest, Integer timeId){
        TimeEntity timeEntity = new TimeEntity();
        if(timeRepository.findById(timeId).isPresent()
        && projectRepository.findById(timeRequest.getProjectId()).isPresent()
        && resourceRepository.findById(timeRequest.getResourceId()).isPresent()){
            timeEntity.setResourceEntity(resourceRepository.findById(timeRequest.getResourceId()).get());
            timeEntity.setProjectEntity(projectRepository.findById(timeRequest.getProjectId()).get());
            timeEntity.setId(timeId);
        }
        int start = timeRequest.getStartHour();
        int end = timeRequest.getEndHour();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeRequest.getDate());
        if(start >= START_HOUR && end <= END_HOUR && start < end){
            if (timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).isPresent()){
                if(TimeCheck(timeRequest, timeEntity, start, end, calendar)){
                    setShift(timeEntity, start, end, calendar);
                    timeRepository.save(timeEntity);
                }
            }
        }
    }

    private boolean TimeCheck(TimeRequest timeRequest, TimeEntity timeEntity, int start, int end, Calendar calendar) {
        List<TimeEntity> listTime = timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).get();
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
//        if(check){
//            setShift(timeEntity, start, end, calendar);
//            timeRepository.save(timeEntity);
//        }
        return check;
    }

    @Override
    public void deleteBooking(Integer id){
        if(timeRepository.findById(id).isPresent()){
            timeRepository.deleteById(id);
        }
    }

    @Override
    public List<TimeDTO> showBookingByWeek(Date date, Integer workspaceId){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        List<Date> dateList = new ArrayList<>();
        dateList.add(date);
        List<TimeDTO> list = new ArrayList<>();
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
                calendar.add(Calendar.DATE, 1);
                Date newDate = calendar.getTime();
                dateList.add(newDate);
            }
        }
        else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                calendar.add(Calendar.DATE, -1);
                Date newDate = calendar.getTime();
                dateList.add(newDate);
            }
        }
        else {
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
                calendar.add(Calendar.DATE, 1);
                Date newDate = calendar.getTime();
                dateList.add(newDate);
            }
            calendar.setTime(date);
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                calendar.add(Calendar.DATE, -1);
                Date newDate = calendar.getTime();
                dateList.add(newDate);
            }
        }
        if (!resourceRepository.findResourcesOfWorkSpace(workspaceId).isEmpty()) {
            List<ResourceEntity> resourceEntityList = resourceRepository.findResourcesOfWorkSpace(workspaceId);
            List<List<TimeEntity>> resourceTimeList = new ArrayList<>();
            for(Date d : dateList){
                Calendar calendarOfDateList = Calendar.getInstance();
                calendarOfDateList.setTime(d);
                for (ResourceEntity res : resourceEntityList){
                    if (timeRepository.findShiftOfResource(calendarOfDateList.get(Calendar.YEAR),
                            calendarOfDateList.get(Calendar.MONTH) + 1,
                            calendarOfDateList.get(Calendar.DAY_OF_MONTH), res.getId()).isPresent()){
                        resourceTimeList.add(timeRepository.findShiftOfResource(calendarOfDateList.get(Calendar.YEAR),
                                calendarOfDateList.get(Calendar.MONTH) + 1,
                                calendarOfDateList.get(Calendar.DAY_OF_MONTH), res.getId()).get());
                    }
                }
            }
            for (List<TimeEntity> l : resourceTimeList){
                for (TimeEntity t : l){
                    list.add(timeConverter.convertToDto(t));
                }
            }
        }
        return list;
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
