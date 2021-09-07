package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.project.ReportProject;
import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ReportResource;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ReportService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private static final String TRUE = "true";
    private final ProjectRepository projectRepository;
    private final TimeRepository timeRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final ResourceRepository resourceRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;

    @Autowired
    public ReportServiceImpl(ProjectRepository projectRepository,
                             TimeRepository timeRepository,
                             ModelMapper modelMapper,
                             WorkspaceRepository workspaceRepository,
                             ResourceRepository resourceRepository,
                             TeamRepository teamRepository,
                             PositionRepository positionRepository) {
        this.projectRepository = projectRepository;
        this.timeRepository = timeRepository;
        this.modelMapper = modelMapper;
        this.workspaceRepository = workspaceRepository;
        this.resourceRepository = resourceRepository;
        this.teamRepository = teamRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public ProjectReportResponse reportProject(Date startDate, Date endDate,
                                               Integer idWorkspace) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).
                orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));

        List<ProjectEntity> projectEntities = projectRepository.findAllByWorkspaceEntityProject_Id(idWorkspace);
        ProjectReportResponse projectReport = new ProjectReportResponse();
        List<ReportProject> reportProjects = new ArrayList<>();
        for (ProjectEntity projectEntity : projectEntities) {
            List<TimeEntity> timeEntities = timeRepository.findAllByIdProject(projectEntity.getId());
            ReportProject reportProject = modelMapper.map(projectEntity, ReportProject.class);
            Double sumWork = 0.0;
            for (TimeEntity timeEntity : timeEntities) {
                if (timeEntity.getStartTime().getTime() >= startDate.getTime() &&
                        timeEntity.getEndTime().getTime() <= endDate.getTime()) {
                    sumWork += timeEntity.getTotalHour();
                }
            }
            if (sumWork <= workDays(workspaceEntity).size() * 8) {
                reportProject.setWorkingDays(sumWork);
                reportProject.setOvertimeDays(0.0);
            } else {
                reportProject.setWorkingDays((double) workDays(workspaceEntity).size() * 8);
                reportProject.setOvertimeDays(sumWork - (workDays(workspaceEntity).size() * 8));
            }
            reportProjects.add(reportProject);
        }
        projectReport.setProjects(reportProjects);
        double workingDays = 0.0;
        double overtimeDays = 0.0;
        for (ReportProject reportProject : reportProjects) {
            workingDays += reportProject.getWorkingDays();
            overtimeDays += reportProject.getOvertimeDays();
        }
        projectReport.setWorkingDays(workingDays);
        projectReport.setOvertimeDays(overtimeDays);
        return projectReport;
    }

    @Override
    public ResourceReportResponse reportResource(Date startDate, Date endDate, Integer idWorkspace) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).
                orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        List<ResourceEntity> resourceEntities = resourceRepository.findAllByidWorkspace(idWorkspace);
        ResourceReportResponse resourceReportResponse = new ResourceReportResponse();
        List<ReportResource> resources = new ArrayList<>();
        for (ResourceEntity resourceEntity : resourceEntities) {
            List<TimeEntity> timeEntities = timeRepository.findAllByIdResource(resourceEntity.getId());
            ReportResource reportResource = modelMapper.map(resourceEntity, ReportResource.class);
            TeamEntity teamEntity = teamRepository.findById(resourceEntity.getTeamEntityResource().getId()).
                    orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            reportResource.setTeamName(teamEntity.getName());
            PositionEntity positionEntity = positionRepository.findById(resourceEntity.getPositionEntity().getId()).
                    orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            reportResource.setPositionName(positionEntity.getName());
            Double sumWork = 0.0;
            for (TimeEntity timeEntity : timeEntities) {
                if (timeEntity.getStartTime().getTime() >= startDate.getTime() &&
                        timeEntity.getEndTime().getTime() <= endDate.getTime()) {
                    sumWork += timeEntity.getTotalHour();
                }
            }
            if (sumWork <= workDays(workspaceEntity).size() * 8) {
                reportResource.setWorkingDays(sumWork);
                reportResource.setOvertimeDays(0.0);
            } else {
                reportResource.setWorkingDays((double) workDays(workspaceEntity).size() * 8);
                reportResource.setOvertimeDays(sumWork - (workDays(workspaceEntity).size() * 8));
            }
            resources.add(reportResource);
        }
        resourceReportResponse.setResources(resources);
        double workingDays = 0.0;
        double overtimeDays = 0.0;
        for (ReportResource reportResource : resources) {
            workingDays += reportResource.getWorkingDays();
            overtimeDays += reportResource.getOvertimeDays();
        }
        resourceReportResponse.setWorkingDays(workingDays);
        resourceReportResponse.setOvertimeDays(overtimeDays);
        return resourceReportResponse;
    }

    private List<Boolean> workDays(WorkspaceEntity workspaceEntity) {
        List<Boolean> workDays = new ArrayList<>();
        String[] arrayWorkDays = workspaceEntity.getWorkDays().split(",");
        for (String string : arrayWorkDays) {
            if (string.equals(TRUE)) {
                workDays.add(true);
            }
        }
        return workDays;
    }
}
