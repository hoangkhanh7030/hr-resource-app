package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.ProjectConverter;
import com.ces.intern.hr.resourcing.demo.converter.TimeConverter;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
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
    public void addNewBooking(TimeRequest timeRequest, Integer start, Integer end){
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setProjectEntity(projectRepository.findById(timeRequest.getProjectId()).orElse(null));
        timeEntity.setResourceEntity(resourceRepository.findById(timeRequest.getResourceId()).orElse(null));
        Date date = timeRequest.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (start >= START_HOUR && start < END_HOUR && end > START_HOUR && end <= END_HOUR && start < end) {
            if (!timeRepository.findShiftOfResource(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), timeRequest.getResourceId()).isPresent()) {
                setShift(timeEntity, start, end, calendar);
                timeRepository.save(timeEntity);
            } else {
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
                if(check){
                    setShift(timeEntity, start, end, calendar);
                    timeRepository.save(timeEntity);
                }
            }
        }
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
