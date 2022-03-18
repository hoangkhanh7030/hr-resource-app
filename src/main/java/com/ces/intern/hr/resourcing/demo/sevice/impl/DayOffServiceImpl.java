package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.request.DayOffRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.DayOffService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import org.decimal4j.util.DoubleRounder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DayOffServiceImpl implements DayOffService {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final WorkspaceConverter workspaceConverter;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    private DayOffServiceImpl(
            TimeRepository timeRepository,
            ProjectRepository projectRepository,
            ResourceRepository resourceRepository,
            ModelMapper modelMapper,
            TeamRepository teamRepository,
            WorkspaceConverter workspaceConverter,
            WorkspaceRepository workspaceRepository) {

        this.timeRepository = timeRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
        this.workspaceConverter = workspaceConverter;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public MessageResponse deleteDayOff(Integer id) {
        if (timeRepository.findById(id).isPresent()) {
            TimeEntity timeEntity = timeRepository.findById(id).get();
            if (timeEntity.getStatus()==false){
                ResourceEntity resourceEntity = resourceRepository.findById(timeEntity.getResourceEntity().getId()).get();
                long getDiff = timeEntity.getEndTime().getTime() - timeEntity.getStartTime().getTime();
                long getDaysDiff = TimeUnit.MILLISECONDS.toDays(getDiff);
                int dayOff =(int)getDaysDiff +1;
                int curentVacation =resourceEntity.getVacation();
                resourceEntity.setVacation(curentVacation-dayOff);
                resourceRepository.save(resourceEntity);
                timeRepository.deleteById(id);
            }

            return new MessageResponse(ResponseMessage.DELETE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_BOOKING_FAIL, Status.FAIL.getCode());
    }

    @Override
    public void newDayOff(DayOffRequest dayOffRequest, Integer idWorkspace) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(dayOffRequest.getStartDate());
        Date endDay = SIMPLE_DATE_FORMAT.parse(dayOffRequest.getEndDate());
        ResourceEntity resourceEntity = resourceRepository.findByIdAndWorkspaceEntityResource_Id(dayOffRequest.getResourceId(), idWorkspace).orElse(null);
        ProjectEntity projectEntity = projectRepository.findByNameAndWorkspaceId("VACATION",idWorkspace).get();
        WorkspaceDTO workspaceDTO = workspaceConverter.convertToDTO(workspaceRepository.getById(idWorkspace));
        List<Boolean> workingDays = new ArrayList<>(workspaceDTO.getWorkDays());
        long getDiff = endDay.getTime() - startDay.getTime();
        long getDaysDiff = TimeUnit.MILLISECONDS.toDays(getDiff);
        int dayOff =(int)getDaysDiff +1;
        int curentVacation =resourceEntity.getVacation();
        if (startDay.equals(endDay)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDay);
            TimeEntity timeEntity = new TimeEntity();
            timeEntity.setStartTime(startDay);
            timeEntity.setEndTime(endDay);
            timeEntity.setStatus(dayOffRequest.getStatus());
            timeEntity.setResourceEntity(resourceEntity);
            timeEntity.setProjectEntity(projectEntity);
            timeEntity.setTotalHour(0.0);
            timeRepository.save(timeEntity);
            resourceEntity.setVacation(curentVacation+dayOff);
            resourceRepository.save(resourceEntity);

        } else {
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            calendarStart.setTime(startDay);
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            calendarStart.set(Calendar.MILLISECOND, 0);
            calendarEnd.setTime(endDay);
            calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
            calendarEnd.set(Calendar.MINUTE, 0);
            calendarEnd.set(Calendar.SECOND, 0);
            calendarEnd.set(Calendar.MILLISECOND, 0);
            List<Date> dateRangeList = new ArrayList<>();
            dateRangeList.add(calendarStart.getTime());
            do {
                calendarStart.add(Calendar.DATE, 1);
                dateRangeList.add(calendarStart.getTime());
            } while (!calendarStart.equals(calendarEnd));
            List<List<Date>> listOfDateRangeLists = new ArrayList<>();
            List<Date> listToAdd = new ArrayList<>();
            int totalDays = 0;
            for (int i = 0; i < dateRangeList.size(); i++) {
                if (!workingDays.get(Utils.getIndexFromDate(dateRangeList.get(i)))) {
                    totalDays += listToAdd.size();
                    List<Date> copy = new ArrayList<>(listToAdd);
                    listToAdd.clear();
                    listOfDateRangeLists.add(copy);
                } else if (i == dateRangeList.size() - 1) {
                    if (workingDays.get(Utils.getIndexFromDate(dateRangeList.get(i)))) {
                        listToAdd.add(dateRangeList.get(i));
                    }
                    totalDays += listToAdd.size();
                    List<Date> copy = new ArrayList<>(listToAdd);
                    listToAdd.clear();
                    listOfDateRangeLists.add(copy);
                } else {
                    if (i != dateRangeList.size() - 1) {
                        listToAdd.add(dateRangeList.get(i));
                    } else {
                        listOfDateRangeLists.add(listToAdd);
                        listToAdd.clear();
                    }
                }
            }
            for (List<Date> list : listOfDateRangeLists) {
                if (list.size() != 0) {
                    TimeEntity timeEntity = new TimeEntity();
                    timeEntity.setStartTime(list.get(0));
                    timeEntity.setEndTime(list.get(list.size() - 1));
                    timeEntity.setStatus(dayOffRequest.getStatus());
                    timeEntity.setResourceEntity(resourceEntity);
                    timeEntity.setProjectEntity(projectEntity);
                    timeEntity.setTotalHour(0.0);
                    timeRepository.save(timeEntity);
                    resourceEntity.setVacation(curentVacation+dayOff);
                    resourceRepository.save(resourceEntity);
                }
            }
        }

    }
    private void update(TimeEntity timeEntity, DayOffRequest dayOffRequest, Date currentStart, Date currentEnd) {
        timeEntity.setResourceEntity(resourceRepository.findById(dayOffRequest.getResourceId()).get());
        timeEntity.setStartTime(currentStart);
        timeEntity.setEndTime(currentEnd);
        timeEntity.setTotalHour(0.0);
        timeRepository.save(timeEntity);

    }

    @Override
    public MessageResponse updateDayOff(DayOffRequest dayOffRequest, Integer idWorkspace) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(dayOffRequest.getStartDate());
        Date endDay = SIMPLE_DATE_FORMAT.parse(dayOffRequest.getEndDate());
        if (dayOffRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            if (startDay.getTime() > endDay.getTime()) {
                return new MessageResponse(ResponseMessage.WRONG_TIME, Status.FAIL.getCode());
            } else {

                if (timeRepository.findById(dayOffRequest.getId()).isPresent()) {
                    TimeEntity timeEntity = timeRepository.findById(dayOffRequest.getId()).get();
                    Boolean checkStartDay = startDay.equals(timeEntity.getStartTime()) || startDay.after(timeEntity.getStartTime());
                    Boolean checkEndDay = endDay.equals(timeEntity.getEndTime()) || endDay.before(timeEntity.getEndTime());
                    if (checkStartDay && checkEndDay) {
                        update(timeEntity, dayOffRequest, startDay, endDay);
                    } else {
                        timeRepository.delete(timeEntity);
                        newDayOff(dayOffRequest, idWorkspace);
                    }
                }
                return new MessageResponse(ResponseMessage.UPDATE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
            }
        }
    }
}
