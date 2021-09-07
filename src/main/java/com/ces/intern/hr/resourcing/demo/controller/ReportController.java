package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;
import com.ces.intern.hr.resourcing.demo.sevice.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("api/v1/workspaces")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        
        this.reportService = reportService;
    }

    @GetMapping("/{idWorkspace}/reportProject")
    private ProjectReportResponse getAllProject(@PathVariable Integer idWorkspace,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(startDate);
        Date endDay = simpleDateFormat.parse(endDate);
        return reportService.reportProject(startDay, endDay, idWorkspace);
    }
    @GetMapping("/{idWorkspace}/reportResource")
    private ResourceReportResponse getAllResource(@PathVariable Integer idWorkspace,
                                          @RequestParam String startDate,
                                          @RequestParam String endDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(startDate);
        Date endDay = simpleDateFormat.parse(endDate);
        return reportService.reportResource(startDay, endDay, idWorkspace);
    }
}
