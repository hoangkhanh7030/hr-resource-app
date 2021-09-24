package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.converter.WorkspaceConverter;
import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.BookingRequest;
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
    private static final int ONE_WEEK_WORK = 1;
    private static final int TWO_WEEK_WORK = 2;
    private static final int FOUR_WEEK_WORK = 4;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final TimeRepository timeRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final WorkspaceConverter workspaceConverter;
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    private TimeServiceImpl(
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
    public MessageResponse deleteBooking(Integer id) {
        if (timeRepository.findById(id).isPresent()) {
            timeRepository.deleteById(id);
            return new MessageResponse(ResponseMessage.DELETE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_BOOKING_FAIL, Status.FAIL.getCode());
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

        if (startDay.equals(endDay)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDay);
            TimeEntity timeEntity = new TimeEntity();
            timeEntity.setStartTime(startDay);
            timeEntity.setEndTime(endDay);
            timeEntity.setProjectEntity(projectEntity);
            timeEntity.setResourceEntity(resourceEntity);
            if (checkNull) {
                double hour = (8 * bookingRequest.getPercentage()) / 100;
                timeEntity.setTotalHour(DoubleRounder.round(hour, 1));
            } else {
                timeEntity.setTotalHour((bookingRequest.getDuration() * 8) / 8);
            }
            timeRepository.save(timeEntity);
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
            int hourTotalStandard = totalDays * 8;
            double hourForEachDay;
            double hourTotalActual;
            if (checkNull) {
                hourTotalActual = (hourTotalStandard * bookingRequest.getPercentage()) / 100;
            } else {
                hourTotalActual = (bookingRequest.getDuration() * hourTotalStandard) / 8;
            }
            hourForEachDay = DoubleRounder.round(hourTotalActual / totalDays, 1);
            for (List<Date> list : listOfDateRangeLists) {
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
    public MessageResponse updateBooking(BookingRequest bookingRequest, Integer idWorkspace) throws ParseException {
        Date startDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getStartDate());
        Date endDay = SIMPLE_DATE_FORMAT.parse(bookingRequest.getEndDate());
        if (bookingRequest.validate()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            if (startDay.getTime() > endDay.getTime()) {
                return new MessageResponse(ResponseMessage.WRONG_TIME, Status.FAIL.getCode());
            } else {

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
                return new MessageResponse(ResponseMessage.UPDATE_BOOKING_SUCCESS, Status.SUCCESS.getCode());
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
        double percent;
        List<Boolean> workDays = new ArrayList<>();
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).orElse(null);
        assert workspaceEntity != null;
        String[] arrayWorkDays = workspaceEntity.getWorkDays().split(",");
        for (String string : arrayWorkDays) {
            workDays.add(Boolean.parseBoolean(string));
        }
        int size=0;
        for (Boolean b:workDays){
            if (b.equals(true)){
                size+=1;
            }
        }
        DashboardListResponse dashboardListResponse = new DashboardListResponse();
        List<ResourceEntity> resourceEntities = resourceRepository.findAllBySearchName(idWorkspace, searchName);
        resourceEntities.sort((o1, o2) -> (int) (o1.getCreatedDate().getTime() - o2.getCreatedDate().getTime()));
        List<Integer> result = resourceEntities.stream().map(BaseEnity::getId).distinct().collect(Collectors.toList());
        List<ResourceResponse> resourceResponses = new ArrayList<>();
        for (Integer integer : result) {
            ResourceEntity resourceEntity = resourceRepository.findById(integer).orElse(null);
            if (searchName.isEmpty()) {
                ResourceResponse resourceResponse = new ResourceResponse();
                assert resourceEntity != null;
                resourceResponse.setId(resourceEntity.getId());
                resourceResponse.setName(resourceEntity.getName());
                resourceResponse.setAvatar(resourceEntity.getAvatar());
                resourceResponse.setTeamId(resourceEntity.getTeamEntityResource().getId());
                resourceResponse.setPosition(resourceEntity.getPositionEntity().getName());
                resourceResponse.setBookings(sort(resourceEntity, startDay, endDay));
                List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(integer);
                List<TimeDTO> timeDTOS = entityToDTO(timeEntities, startDay, endDay);
                Double sumHour = sumHour(timeDTOS);
                if (viewType == ONE_WEEK) {
                    percent = (sumHour / (size*8*ONE_WEEK_WORK)) * 100;
                } else if (viewType == TWO_WEEK) {
                    percent = (sumHour / (size*8*TWO_WEEK_WORK)) * 100;
                } else {
                    percent = (sumHour / (size*8*FOUR_WEEK_WORK)) * 100;
                }
                resourceResponse.setPercent(DoubleRounder.round(percent, 1));
                resourceResponses.add(resourceResponse);
            } else {
                List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(integer);
                for (int i = 0; i < timeEntities.size(); i++) {
                    if (timeEntities.get(i).getStartTime().getTime() >= startDay.getTime() &&
                            timeEntities.get(i).getEndTime().getTime() <= endDay.getTime()) {
                        if (resourceRepository.findBySearchName(searchName, integer, timeEntities.get(i).getStartTime(), timeEntities.get(i).getEndTime()).isPresent()) {
                            ResourceResponse resourceResponse = new ResourceResponse();
                            assert resourceEntity != null;
                            resourceResponse.setId(resourceEntity.getId());
                            resourceResponse.setName(resourceEntity.getName());
                            resourceResponse.setAvatar(resourceEntity.getAvatar());
                            resourceResponse.setTeamId(resourceEntity.getTeamEntityResource().getId());
                            resourceResponse.setPosition(resourceEntity.getPositionEntity().getName());
                            resourceResponse.setBookings(sort(resourceEntity, startDay, endDay));
                            List<TimeDTO> timeDTOS = entityToDTO(timeEntities, startDay, endDay);
                            Double sumHour = sumHour(timeDTOS);
                            if (viewType == ONE_WEEK) {
                                percent = (sumHour / (size*8*ONE_WEEK_WORK)) * 100;
                            } else if (viewType == TWO_WEEK) {
                                percent = (sumHour / (size*8*TWO_WEEK_WORK)) * 100;
                            } else {
                                percent = (sumHour / (size*8*FOUR_WEEK_WORK)) * 100;
                            }
                            resourceResponse.setPercent(DoubleRounder.round(percent, 1));
                            resourceResponses.add(resourceResponse);
                            break;
                        }

                    }
                }
            }


        }
        dashboardListResponse.setResources(resourceResponses);
        List<TeamEntity> teamEntities = teamRepository.findAllByidWorkspace(idWorkspace);
        teamEntities.sort((o1, o2) -> (int) (o1.getCreatedDate().getTime() - o2.getCreatedDate().getTime()));
        List<TeamResponse> teamResponses = teamEntities.stream().map(
                teamEntity -> modelMapper.map(teamEntity, TeamResponse.class))
                .collect(Collectors.toList());

        dashboardListResponse.setTeams(teamResponses);
        dashboardListResponse.setStatus(Status.SUCCESS.getCode());

        dashboardListResponse.setWorkDays(workDays);
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
        return modelMapper.map(projectEntity, ProjectDTO.class);
    }

    private List<List<DashboardResponse>> sort(ResourceEntity resourceEntity, Date startDay, Date endDay) {
        List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(resourceEntity.getId());
        List<TimeDTO> timeDTOS = entityToDTO(timeEntities, startDay, endDay);
        timeDTOS.sort((o1, o2) -> (int) (o1.getStartDate().getTime() - o2.getStartDate().getTime()));
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
            result.clear();
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
