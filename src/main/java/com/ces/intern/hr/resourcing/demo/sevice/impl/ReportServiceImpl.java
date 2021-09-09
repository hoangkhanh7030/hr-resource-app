package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.project.ReportProject;
import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.report.Report;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ReportResource;
import com.ces.intern.hr.resourcing.demo.importCSV.Style;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.sevice.ReportService;
import com.ces.intern.hr.resourcing.demo.utils.CSVFile;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private static final int MILLISECOND = (1000 * 60 * 60 * 24);
    private static final int ONE_WEEK = 7;
    private static final String DAY = "DAY";

    private static final String TRUE = "true";
    private final ProjectRepository projectRepository;
    private final TimeRepository timeRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final ResourceRepository resourceRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;

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
        workbook = new XSSFWorkbook();
    }


    @Override
    public Report report(Date startDate, Date endDate
            , Integer idWorkspace, String type) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).
                orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        int sumDay = (int) ((((endDate.getTime() - startDate.getTime()) / MILLISECOND) + 1) / ONE_WEEK) * workDays(workspaceEntity).size();

        int time;
        Report report = new Report();
        if (type.equals(DAY)) {
            time = 1;
        } else {
            time = 8;
        }

        report.setProjectReports(reportProject(startDate, endDate, idWorkspace, time));
        report.setResourceReports(reportResource(startDate, endDate, idWorkspace, time));
        List<ResourceEntity> resourceEntities = resourceRepository.findAllByidWorkspace(idWorkspace);
        report.setTrafficTime((double) sumDay * time * resourceEntities.size());
        report.setOverTime(reportResource(startDate, endDate, idWorkspace, time).getOvertimeDays());
        report.setAllocatedTime(reportResource(startDate, endDate, idWorkspace, time).getWorkingDays());
        return report;
    }

    @Override
    public ProjectReportResponse reportProject(Date startDate, Date endDate,
                                               Integer idWorkspace, Integer time) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).
                orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        int sumDay = (int) (((((endDate.getTime() - startDate.getTime()) / MILLISECOND) + 1) / ONE_WEEK) * workDays(workspaceEntity).size()) * time;

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
                    if (time == 1) {
                        sumWork += (timeEntity.getTotalHour() / 8);
                    } else {
                        sumWork += timeEntity.getTotalHour();
                    }

                }
            }
            if (sumWork <= sumDay) {
                reportProject.setWorkingDays(sumWork);
                reportProject.setOvertimeDays(0.0);
            } else {
                reportProject.setWorkingDays((double) sumDay);
                reportProject.setOvertimeDays(sumWork - (double) sumDay);
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
    public ResourceReportResponse reportResource(Date startDate, Date endDate, Integer idWorkspace, Integer time) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).
                orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        int sumDay = (int) (((((endDate.getTime() - startDate.getTime()) / MILLISECOND) + 1) / ONE_WEEK) * workDays(workspaceEntity).size()) * time;

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
                    if (time == 1) {
                        sumWork += (timeEntity.getTotalHour() / 8);
                    } else {
                        sumWork += timeEntity.getTotalHour();
                    }

                }
            }
            if (sumWork <= sumDay) {
                reportResource.setWorkingDays(sumWork);
                reportResource.setOvertimeDays(0.0);
            } else {
                reportResource.setWorkingDays((double) sumDay);
                reportResource.setOvertimeDays(sumWork - (double) sumDay);
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

    @Override
    public void export(HttpServletResponse response, Integer idWorkspace,
                       Date startDate, Date endDate, String type) throws IOException {
        int time;
        if (type.equals("DAY")) {
            time = 1;
        } else {
            time = 8;
        }
        writeHeaderLine();
        writeDataLines(report(startDate, endDate, idWorkspace, type), startDate, endDate,type);

        writeResourceHeaderLine();
        writeResourceDataLines(reportResource(startDate, endDate, idWorkspace, time));

        writeProjectHeaderLine();
        writeProjectDataLines(reportProject(startDate, endDate, idWorkspace, time));

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }


    private void writeHeaderLine() {


        sheet = workbook.createSheet("Overview");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Traffic Time", style);
        createCell(row, 1, "Allocated Time", style);
        createCell(row, 2, "Over Time", style);
        createCell(row, 3, "Type", style);
        createCell(row, 4, "StartDate", style);
        createCell(row, 5, "EndDate", style);
    }

    private void writeDataLines(Report report, Date startDate, Date endDate,String type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDay = simpleDateFormat.format(startDate);
        String endDay = simpleDateFormat.format(endDate);
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        try {
            Row row = sheet.createRow(rowCount);
            int columnCount = 0;

            createCell(row, columnCount++, report.getTrafficTime(), style);
            createCell(row, columnCount++, report.getAllocatedTime(), style);
            createCell(row, columnCount++, report.getOverTime(), style);
            createCell(row, columnCount++, type, style);
            createCell(row, columnCount++, startDay, style);
            createCell(row, columnCount, endDay, style);

        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }

    }

    private void writeResourceHeaderLine() {
        sheet = workbook.createSheet("Resources");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "Name", style);
        createCell(row, 1, "Team Name", style);
        createCell(row, 2, "Position Name", style);
        createCell(row, 3, "Working Days", style);
        createCell(row, 4, "OverTime Days", style);

    }

    private void writeProjectHeaderLine() {
        sheet = workbook.createSheet("Project");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Name", style);
        createCell(row, 1, "Client Name", style);
        createCell(row, 2, "Color", style);
        createCell(row, 3, "Working Days", style);
        createCell(row, 4, "OverTime Days", style);

    }

    private void writeResourceDataLines(ResourceReportResponse resources) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        try {
            for (ReportResource reportResource : resources.getResources()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;

                createCell(row, columnCount++, reportResource.getName(), style);
                createCell(row, columnCount++, reportResource.getTeamName(), style);
                createCell(row, columnCount++, reportResource.getPositionName(), style);
                createCell(row, columnCount++, reportResource.getWorkingDays(), style);
                createCell(row, columnCount, reportResource.getOvertimeDays(), style);

            }
        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }

    }

    private void writeProjectDataLines(ProjectReportResponse projects) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        try {
            for (ReportProject reportProject : projects.getProjects()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;

                createCell(row, columnCount++, reportProject.getName(), style);
                createCell(row, columnCount++, reportProject.getClientName(), style);
                createCell(row, columnCount++, reportProject.getColor(), style);
                createCell(row, columnCount++, reportProject.getWorkingDays(), style);
                createCell(row, columnCount, reportProject.getOvertimeDays(), style);

            }
        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }

    }

    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
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
