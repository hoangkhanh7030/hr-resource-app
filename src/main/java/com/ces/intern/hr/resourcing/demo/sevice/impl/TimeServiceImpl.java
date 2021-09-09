package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.TimeConverter;
import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.BookingResponse;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardListResponse;
import com.ces.intern.hr.resourcing.demo.http.response.dashboard.DashboardResponse;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.TeamResponse;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.TimeService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
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
import java.util.stream.Collectors;

@Service
public class TimeServiceImpl implements TimeService {
    private static final int MILLISECOND = (1000 * 60 * 60 * 24);
    private static final int ONE_WEEK = 7;
    private static final int TWO_WEEK = 14;
    private static final int FOUR_WEEK = 28;
    private static final int ONE_WEEK_WORK_HOUR = 40;
    private static final int TWO_WEEK_WORK_HOUR = 80;
    private static final int FOUR_WEEK_WORK_HOUR = 160;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final TimeConverter timeConverter;
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final WorkspaceConverter workspaceConverter;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    private TimeServiceImpl(TimeConverter timeConverter,
                            TimeRepository timeRepository,
                            ProjectRepository projectRepository,
                            ResourceRepository resourceRepository,
                            ModelMapper modelMapper,
                            TeamRepository teamRepository,
                            WorkspaceConverter workspaceConverter,
                            WorkspaceRepository workspaceRepository) {
        this.timeConverter = timeConverter;
        this.timeRepository = timeRepository;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
        this.workspaceConverter = workspaceConverter;
        this.workspaceRepository = workspaceRepository;
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
    public void newBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getStartDate());
        Date endDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getEndDate());
        ProjectEntity projectEntity = projectRepository.findByIdAndWorkspaceEntityProject_Id(bookingRequest.getProjectId(), idWorkspace).orElse(null);
        ResourceEntity resourceEntity = resourceRepository.findByIdAndWorkspaceEntityResource_Id(bookingRequest.getResourceId(), idWorkspace).orElse(null);
        boolean checkNull = bookingRequest.getDuration() == null;

        WorkspaceDTO workspaceDTO = workspaceConverter.convertToDTO(workspaceRepository.getById(idWorkspace));
        List<Boolean> workingDays = new ArrayList<>(workspaceDTO.getWorkDays());
        //Boolean[] workDays = workspaceDTO.getWorkDays().toArray(new Boolean[0]);
        List<BookingResponse> bookingResponseList = new ArrayList<>();
        //Long hourTotal;
        if (startDay.equals(endDay)) {
            //hourTotal = (((endDay.getTime() - startDay.getTime()) / MILLISECOND) + 1) * 8;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDay);
            if (workingDays.get(calendar.get(Calendar.DAY_OF_WEEK) - 1)) {
                TimeEntity timeEntity = new TimeEntity();
                timeEntity.setStartTime(startDay);
                timeEntity.setEndTime(endDay);
                timeEntity.setProjectEntity(projectEntity);
                timeEntity.setResourceEntity(resourceEntity);
                if (checkNull) {
                    double totalHour = (8 * bookingRequest.getPercentage()) / 100;
                    timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
                } else {
                    timeEntity.setTotalHour((bookingRequest.getDuration() * 8) / 8);
                }
                timeRepository.save(timeEntity);
            }
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
            List<Date> dateList = new ArrayList<>();
            dateList.add(calendarStart.getTime());
            //System.out.println(calendar1.getTime());
            do {
                calendarStart.add(Calendar.DATE, 1);
                dateList.add(calendarStart.getTime());
            } while (!calendarStart.equals(calendarEnd));
            //
            List<List<Date>> listOfDateList = new ArrayList<>();
            List<Date> listToAdd = new ArrayList<>();
            int totalDays = 0;
            for (int i = 0; i < dateList.size(); i++) {
                if (!workingDays.get(Utils.getIndexFromDate(dateList.get(i)))) {
                    totalDays += listToAdd.size();
                    List<Date> copy = new ArrayList<>(listToAdd);
                    listToAdd.clear();
                    listOfDateList.add(copy);
                } else {
                    if (i != dateList.size() - 1) {
                        listToAdd.add(dateList.get(i));
                    } else {
                        listOfDateList.add(listToAdd);
                        listToAdd.clear();
                    }
                }
            }
            int hourTotalStandard = totalDays * 8;
            double hourForEachDay;
            double hourTotalActual;
            if (checkNull) {
                hourTotalActual = (hourTotalStandard * bookingRequest.getPercentage()) / 100;
            } else {
                hourTotalActual = (bookingRequest.getDuration() * hourTotalStandard) / 8;
            }
            hourForEachDay = DoubleRounder.round(hourTotalActual / totalDays, 1);
            for (List<Date> list : listOfDateList) {
                if (list.size() != 0) {
                    TimeEntity timeEntity = new TimeEntity();
                    timeEntity.setStartTime(list.get(0));
                    timeEntity.setEndTime(list.get(list.size() - 1));
                    timeEntity.setTotalHour(hourForEachDay * list.size());
                    timeEntity.setProjectEntity(projectEntity);
                    timeEntity.setResourceEntity(resourceEntity);
                    timeRepository.save(timeEntity);
                }
            }
        }

        //


//        if ((startDay.equals(Utils.toSaturDayOfWeek(startDay)) || startDay.equals(Utils.toSunDayOfWeek(startDay)))
//                && (endDay.equals(Utils.toSaturDayOfWeek(startDay)) || endDay.equals(Utils.toSunDayOfWeek(startDay)))) {
//            Long hourTotal = (((endDay.getTime() - startDay.getTime()) / MILLISECOND) + 1) * 8;
//            TimeEntity timeEntity = new TimeEntity();
//            timeEntity.setStartTime(startDay);
//            timeEntity.setEndTime(endDay);
//            timeEntity.setProjectEntity(projectEntity);
//            timeEntity.setResourceEntity(resourceEntity);
//            if (checkNull) {
//                double totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
//                timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
//            } else {
//                timeEntity.setTotalHour((bookingRequest.getDuration() * hourTotal) / 8);
//            }
//            timeRepository.save(timeEntity);
//
//        } else {
//            List<BookingResponse> bookingResponses = new ArrayList<>();
//            Date currentDate = new Date(startDay.getTime());
//            while (true) {
//                if (startDay.equals(Utils.toSaturDayOfWeek(currentDate))) {
//                    currentDate.setDate(currentDate.getDate() + 2);
//                }
//                if (startDay.equals(Utils.toSunDayOfWeek(currentDate))) {
//                    currentDate.setDate(currentDate.getDate() + 1);
//                }
//
//                Date first = Utils.toMonDayOfWeek(currentDate);
//                Date end = Utils.toFriDayOfWeek(currentDate);
//                if (startDay.getTime() > first.getTime()) {
//                    first = startDay;
//
//                }
//                if (end.getTime() > endDay.getTime()) {
//                    end = endDay;
//                }
//                BookingResponse bookingResponse = new BookingResponse(first, end);
//                bookingResponses.add(bookingResponse);
//
//                currentDate.setDate(currentDate.getDate() + 7);
//                currentDate = Utils.toMonDayOfWeek(currentDate);
//                if (currentDate.getTime() > endDay.getTime()) {
//                    break;
//                }
//            }
//            for (BookingResponse bookingResponse : bookingResponses) {
//                TimeEntity timeEntity = new TimeEntity();
//                timeEntity.setStartTime(bookingResponse.getStartDay());
//                timeEntity.setEndTime(bookingResponse.getEndDay());
//                timeEntity.setProjectEntity(projectEntity);
//                timeEntity.setResourceEntity(resourceEntity);
//                Long hourTotal = (((bookingResponse.getEndDay().getTime() - bookingResponse.getStartDay().getTime()) / MILLISECOND) + 1) * 8;
//                if (checkNull) {
//                    double totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
//                    timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
//                } else {
//                    timeEntity.setTotalHour((bookingRequest.getDuration() * hourTotal) / 8);
//                }
//                timeRepository.save(timeEntity);
//
//            }
//        }


    }

    private void update(TimeEntity timeEntity, BookingRequest bookingRequest, Date currentStart, Date currentEnd) {
        timeEntity.setResourceEntity(resourceRepository.findById(bookingRequest.getResourceId()).get());
        timeEntity.setProjectEntity(projectRepository.findById(bookingRequest.getProjectId()).get());
        timeEntity.setStartTime(currentStart);
        timeEntity.setEndTime(currentEnd);
        Long hourTotal = (((currentEnd.getTime() - currentStart.getTime()) / MILLISECOND) + 1) * 8;
        double totalHour;
        if (bookingRequest.getDuration() == null) {
            totalHour = (hourTotal * bookingRequest.getPercentage()) / 100;
        } else {
            totalHour = (bookingRequest.getDuration() * hourTotal) / 8;
        }
        timeEntity.setTotalHour(DoubleRounder.round(totalHour, 1));
        timeRepository.save(timeEntity);

    }

    @Override
    public void updateBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getStartDate());
        Date endDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getEndDate());
        if (timeRepository.findById(bookingRequest.getId()).isPresent()) {
            TimeEntity timeEntity = timeRepository.findById(bookingRequest.getId()).get();
            Boolean checkStartDay = startDay.equals(timeEntity.getStartTime()) || startDay.after(timeEntity.getStartTime());
            Boolean checkEndDay = endDay.equals(timeEntity.getEndTime()) || endDay.before(timeEntity.getEndTime());
            if (checkStartDay && checkEndDay) {
                update(timeEntity, bookingRequest, startDay, endDay);
            } else {
                timeRepository.delete(timeEntity);
                newBooking(bookingRequest, idWorkspace);
            }
        }
    }

    @Override
    public DashboardResponse getBooking(Integer idWorkspace, Integer idBooking) {
        TimeEntity timeEntity = timeRepository.findById(idBooking).orElseThrow(
                () -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        Long totalHour = (((timeEntity.getEndTime().getTime() - timeEntity.getStartTime().getTime()) / MILLISECOND) + 1) * 8;
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setId(timeEntity.getId());
        dashboardResponse.setStartDate(SIMPLE_DATE_FORMAT.format(timeEntity.getStartTime()));
        dashboardResponse.setEndDate(SIMPLE_DATE_FORMAT.format(timeEntity.getEndTime()));
        dashboardResponse.setPercentage(DoubleRounder.round((timeEntity.getTotalHour() / totalHour) * 100, 1));
        dashboardResponse.setDuration(DoubleRounder.round((timeEntity.getTotalHour() / totalHour) * 8, 1));

        return dashboardResponse;
    }

    @Override
    public DashboardListResponse searchBooking(Integer idWorkspace, String startDate, String endDate, String searchName) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(startDate);
        Date endDay = SIMPLE_DATE_FORMAT.parse(endDate);
        long viewType = ((endDay.getTime() - startDay.getTime()) / MILLISECOND) + 1;
        Double percent;
        DashboardListResponse dashboardListResponse = new DashboardListResponse();
        List<ResourceEntity> resourceEntities = resourceRepository.findAllBySearchName(idWorkspace, searchName);
        Collections.sort(resourceEntities, (o1, o2) -> (int) (o1.getCreatedDate().getTime() - o2.getCreatedDate().getTime()));
        List<Integer> result = resourceEntities.stream().map(resourceEntity -> resourceEntity.getId()).distinct().collect(Collectors.toList());
        List<ResourceResponse> resourceResponses = new ArrayList<>();
        for (Integer integer : result) {
            ResourceEntity resourceEntity = resourceRepository.findById(integer).get();
            ResourceResponse resourceResponse = new ResourceResponse();
            resourceResponse.setId(resourceEntity.getId());
            resourceResponse.setName(resourceEntity.getName());
            resourceResponse.setAvatar(resourceEntity.getAvatar());
            resourceResponse.setTeamId(resourceEntity.getTeamEntityResource().getId());
            resourceResponse.setPosition(resourceEntity.getPositionEntity().getName());
            resourceResponse.setBookings(sort(resourceEntity, startDay, endDay));
            List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(resourceEntity.getId());
            List<TimeDTO> timeDTOS = entityToDTO(timeEntities, startDay, endDay);
            Double sumHour = sumHour(timeDTOS);
            if (viewType == ONE_WEEK) {
                percent = (sumHour / ONE_WEEK_WORK_HOUR) * 100;
            } else if (viewType == TWO_WEEK) {
                percent = (sumHour / TWO_WEEK_WORK_HOUR) * 100;
            } else {
                percent = (sumHour / FOUR_WEEK_WORK_HOUR) * 100;
            }
            resourceResponse.setPercent(DoubleRounder.round(percent, 1));
            resourceResponses.add(resourceResponse);
        }
        dashboardListResponse.setResources(resourceResponses);
        List<TeamEntity> teamEntities = teamRepository.findAllByidWorkspace(idWorkspace);
        Collections.sort(teamEntities, (o1, o2) -> (int) (o1.getCreatedDate().getTime() - o2.getCreatedDate().getTime()));
        List<TeamResponse> teamResponses = teamEntities.stream().map(
                teamEntity -> modelMapper.map(teamEntity, TeamResponse.class))
                .collect(Collectors.toList());

        dashboardListResponse.setTeams(teamResponses);
        dashboardListResponse.setStatus(Status.SUCCESS.getCode());
        return dashboardListResponse;
    }


    private List<TimeDTO> entityToDTO(List<TimeEntity> timeEntities, Date startDay, Date endDay) {
        List<TimeDTO> timeDTOS = new ArrayList<>();
        for (TimeEntity timeEntity : timeEntities) {
            if (timeEntity.getStartTime().getTime() >= startDay.getTime() && timeEntity.getEndTime().getTime() <= endDay.getTime()) {
                TimeDTO timeDTO = new TimeDTO();
                timeDTO.setId(timeEntity.getId());
                timeDTO.setStartDate(timeEntity.getStartTime());
                timeDTO.setEndDate(timeEntity.getEndTime());
                ProjectEntity projectEntity = projectRepository.findById(timeEntity.getProjectEntity().getId()).get();
                timeDTO.setProjectDTO(toDTO(projectEntity));
                Long totalHour = (((timeEntity.getEndTime().getTime() - timeEntity.getStartTime().getTime()) / MILLISECOND) + 1) * 8;
                timeDTO.setPercentage(DoubleRounder.round((timeEntity.getTotalHour() / totalHour) * 100, 1));
                timeDTO.setDuration(DoubleRounder.round((timeEntity.getTotalHour() / totalHour) * 8, 1));
                timeDTO.setHourTotal(timeEntity.getTotalHour());
                timeDTOS.add(timeDTO);
            }
        }
        return timeDTOS;
    }

    private Double sumHour(List<TimeDTO> timeDTOS) {
        Double sum = 0.0;
        for (TimeDTO timeDTO : timeDTOS) {
            sum += timeDTO.getHourTotal();
        }
        return sum;
    }

    private ProjectDTO toDTO(ProjectEntity projectEntity) {
        ProjectDTO projectDTO = modelMapper.map(projectEntity, ProjectDTO.class);
        return projectDTO;
    }

    private List<List<DashboardResponse>> sort(ResourceEntity resourceEntity, Date startDay, Date endDay) {
        List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(resourceEntity.getId());
        List<TimeDTO> timeDTOS = entityToDTO(timeEntities, startDay, endDay);
        Collections.sort(timeDTOS, (o1, o2) -> (int) (o1.getStartDate().getTime() - o2.getStartDate().getTime()));
        List<List<TimeDTO>> list = new ArrayList<>();
        findArr(timeDTOS, list);
        List<List<DashboardResponse>> result = new ArrayList<>();
        for (List<TimeDTO> dtos : list) {
            result.add(convert(dtos));
        }
        return result;
    }

    private List<DashboardResponse> convert(List<TimeDTO> timeDTOS) {
        List<DashboardResponse> dashboardResponses = new ArrayList<>();
        for (TimeDTO timeDTO : timeDTOS) {
            DashboardResponse dashboardResponse = modelMapper.map(timeDTO, DashboardResponse.class);
            dashboardResponse.setStartDate(SIMPLE_DATE_FORMAT.format(timeDTO.getStartDate()));
            dashboardResponse.setEndDate(SIMPLE_DATE_FORMAT.format(timeDTO.getEndDate()));
            dashboardResponses.add(dashboardResponse);
        }
        return dashboardResponses;
    }

    private void findArr(List<TimeDTO> input, List<List<TimeDTO>> result) {
        if (input.isEmpty()) {
            result.isEmpty();
        } else {
            List<TimeDTO> tempList = new ArrayList<>();
            tempList.add(input.get(0));
            for (int i = 1; i < input.size(); i++) {
                TimeDTO tempTime = input.get(i);
                if (tempTime.getStartDate().getTime() > tempList.get(tempList.size() - 1).getEndDate().getTime()) {
                    tempList.add(tempTime);
                }
            }
            result.add(tempList);
            List<TimeDTO> remainingTime = input.stream()
                    .filter(ele -> !tempList.contains(ele))
                    .collect(Collectors.toList());
            if (!remainingTime.isEmpty()) {
                findArr(remainingTime, result);
            }
        }

    }

}
