package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.project.ReportProject;
import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
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
    private static final String TRUE="true";
    private final ProjectRepository projectRepository;
    private final TimeRepository timeRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    @Autowired
    public ReportServiceImpl(ProjectRepository projectRepository,
                             TimeRepository timeRepository,
                             ModelMapper modelMapper,
                             WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.timeRepository = timeRepository;
        this.modelMapper=modelMapper;
        this.workspaceRepository=workspaceRepository;
    }

    @Override
    public ProjectReportResponse reportProject(Date startDate,Date endDate,
                                               Integer idWorkspace) {
        WorkspaceEntity workspaceEntity=workspaceRepository.findById(idWorkspace).
                orElseThrow(()-> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        List<Boolean> workDays = new ArrayList<>();
        String[] arrayWorkDays = workspaceEntity.getWorkDays().split(",");
        for (String string : arrayWorkDays){
            if (string.equals(TRUE)){
                workDays.add(Boolean.parseBoolean(string));
            }
        }


        List<ProjectEntity> projectEntities=projectRepository.findAllByWorkspaceEntityProject_Id(idWorkspace);
        ProjectReportResponse projectReport= new ProjectReportResponse();
        List<ReportProject> reportProjects=new ArrayList<>();
        for (ProjectEntity projectEntity:projectEntities){
            List<TimeEntity> timeEntities=timeRepository.findAllByIdProject(projectEntity.getId());
            ReportProject reportProject=modelMapper.map(projectEntity,ReportProject.class);
            Double sumWork=0.0;
            for (TimeEntity timeEntity:timeEntities){
                if (timeEntity.getStartTime().getTime()>=startDate.getTime()&&
                        timeEntity.getEndTime().getTime()<=endDate.getTime()){
                    sumWork+=timeEntity.getTotalHour();
                }
            }
            if (sumWork<=workDays.size()){
                reportProject.setWorkingDays(sumWork);
                reportProject.setOvertimeDays(0.0);
            }else {
                reportProject.setWorkingDays((double) workDays.size()*8);
                reportProject.setOvertimeDays(sumWork-(workDays.size()*8));
            }
            reportProjects.add(reportProject);
        }
        projectReport.setProjects(reportProjects);
        double workingDays=0.0;
        double overtimeDays=0.0;
        for (ReportProject reportProject:reportProjects){
            workingDays+=reportProject.getWorkingDays();
            overtimeDays+=reportProject.getOvertimeDays();
        }
        projectReport.setWorkingDays(workingDays);
        projectReport.setOvertimeDays(overtimeDays);
        return projectReport;
    }
}
