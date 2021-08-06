package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.TimeConverter;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TimeRequest;
import com.ces.intern.hr.resourcing.demo.http.response.BookingResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import org.decimal4j.util.DoubleRounder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimeServiceImpl implements TimeService {
    private static final int MILLISECOND = (1000 * 60 * 60 * 24);

    private final TimeConverter timeConverter;
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private TimeServiceImpl(TimeConverter timeConverter,
                            TimeRepository timeRepository,
                            ProjectRepository projectRepository,
                            ResourceRepository resourceRepository,
                            ModelMapper modelMapper) {
        this.timeConverter = timeConverter;
        this.timeRepository = timeRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
        this.modelMapper=modelMapper;
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
    public MessageResponse deleteBooking(Integer id) {
        if (timeRepository.findById(id).isPresent()) {
            timeRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
    }


    @Override
    public Map<Date, List<TimeDTO>> getBookingByMonth(Integer month, Integer year, Integer workspaceId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Map<Date, List<TimeDTO>> bookingMonth = new LinkedHashMap<>();
        while (calendar.get(Calendar.MONTH) + 1 == month) {
            List<TimeDTO> list = new ArrayList<>();
            Date date = calendar.getTime();
//            List<TimeEntity> timeEntities = timeRepository.findAllShiftOfMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
//                    calendar.get(Calendar.DAY_OF_MONTH), workspaceId).orElse(new ArrayList<>());
//            for (TimeEntity t : timeEntities) {
//                list.add(timeConverter.convertToDto(t));
//            }
//            bookingMonth.put(date, list);
//            calendar.add(Calendar.DATE, 1);
        }
        return bookingMonth;
    }

    @Override
    public void newBooking(BookingRequest bookingRequest,Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(bookingRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(bookingRequest.getEndDate());
        ProjectEntity projectEntity = projectRepository.findByIdAndWorkspaceEntityProject_Id(bookingRequest.getIdProject(),idWorkspace).orElse(null);
        ResourceEntity resourceEntity = resourceRepository.findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(bookingRequest.getIdResource(),idWorkspace).orElse(null);
        boolean checkNull = bookingRequest.getDuration() == null;

        if ((startDay.equals(Utils.toSaturDayOfWeek(startDay)) || startDay.equals(Utils.toSunDayOfWeek(startDay)))
                && (endDay.equals(Utils.toSaturDayOfWeek(startDay)) || endDay.equals(Utils.toSunDayOfWeek(startDay)))) {
            Long hourTotal = (((endDay.getTime() - startDay.getTime()) / MILLISECOND) + 1) * 8;
            TimeEntity timeEntity = new TimeEntity();
            timeEntity.setStartTime(startDay);
            timeEntity.setEndTime(endDay);
            timeEntity.setProjectEntity(projectEntity);
            timeEntity.setResourceEntity(resourceEntity);
            if (checkNull) {
                Double totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
                timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
            } else {
                timeEntity.setTotalHour((bookingRequest.getDuration()*hourTotal)/8);
            }
            timeRepository.save(timeEntity);

        } else {
            List<BookingResponse> bookingResponses = new ArrayList<>();
            Date currentDate = new Date(startDay.getTime());
            while (true) {
                if (startDay.equals(Utils.toSaturDayOfWeek(currentDate))) {
                    currentDate.setDate(currentDate.getDate() + 2);
                }
                if (startDay.equals(Utils.toSunDayOfWeek(currentDate))){
                    currentDate.setDate(currentDate.getDate()+1);
                }

                Date first = Utils.toMonDayOfWeek(currentDate);
                Date end = Utils.toFriDayOfWeek(currentDate);
                if (startDay.getTime() > first.getTime()) {
                    first = startDay;

                }
                if (end.getTime() > endDay.getTime()) {
                    end = endDay;
                }
                BookingResponse bookingResponse = new BookingResponse(first, end);
                bookingResponses.add(bookingResponse);

                currentDate.setDate(currentDate.getDate() + 7);
                currentDate=Utils.toMonDayOfWeek(currentDate);
                if (currentDate.getTime() > endDay.getTime()) {
                    break;
                }
            }
            for (BookingResponse bookingResponse : bookingResponses) {
                TimeEntity timeEntity = new TimeEntity();
                timeEntity.setStartTime(bookingResponse.getStartDay());
                timeEntity.setEndTime(bookingResponse.getEndDay());
                timeEntity.setProjectEntity(projectEntity);
                timeEntity.setResourceEntity(resourceEntity);
                Long hourTotal = (((bookingResponse.getEndDay().getTime() - bookingResponse.getStartDay().getTime()) / MILLISECOND) + 1) * 8;
                if (checkNull) {
                    Double totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
                    timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
                } else {
                    timeEntity.setTotalHour((bookingRequest.getDuration()*hourTotal)/8);
                }
                timeRepository.save(timeEntity);

            }
        }


    }
    private void update(TimeEntity timeEntity,BookingRequest bookingRequest,Date currentStart,Date currentEnd){
        timeEntity.setResourceEntity(resourceRepository.findById(bookingRequest.getIdResource()).get());
        timeEntity.setProjectEntity(projectRepository.findById(bookingRequest.getIdProject()).get());
        timeEntity.setStartTime(currentStart);
        timeEntity.setEndTime(currentEnd);
        Long hourTotal = (((currentEnd.getTime() - currentStart.getTime()) / MILLISECOND) + 1) * 8;
        if (bookingRequest.getDuration() == null) {
            Double totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
            timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
        } else {
            timeEntity.setTotalHour((bookingRequest.getDuration()*hourTotal)/8);
        }
        timeRepository.save(timeEntity);

    }

    @Override
    public void updateBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(bookingRequest.getStartDate());
        Date endDay = simpleDateFormat.parse(bookingRequest.getEndDate());
        Date currentStart;
        Date currentEnd;
        if (timeRepository.findById(bookingRequest.getId()).isPresent()){
            TimeEntity timeEntity = timeRepository.findById(bookingRequest.getId()).get();
            Date mon = Utils.toMonDayOfWeek(timeEntity.getEndTime());
            Date fri = Utils.toFriDayOfWeek(timeEntity.getEndTime());
            Date sat=Utils.toSaturDayOfWeek(timeEntity.getEndTime());
            Date sun=Utils.toSunDayOfWeek(timeEntity.getEndTime());
            boolean checkEquals = endDay.equals(sat) || endDay.equals(sun);
            if (startDay.getTime()>= mon.getTime()&&endDay.getTime()<=sun.getTime()){
                if (checkEquals){
                    currentEnd=fri;
                }else {
                    currentEnd=endDay;
                }
                currentStart=startDay;
                update(timeEntity,bookingRequest,currentStart,currentEnd);
            }else{
                timeRepository.delete(timeEntity);
                newBooking(bookingRequest, idWorkspace);
            }

        }


    }




}
