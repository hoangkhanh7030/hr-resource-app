package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.http.response.report.Report;
import com.ces.intern.hr.resourcing.demo.sevice.ReportService;
import com.ces.intern.hr.resourcing.demo.utils.CSVFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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



    @GetMapping("/{idWorkspace}/report")
    private Report getAllResource(@PathVariable Integer idWorkspace,
                                  @RequestParam String startDate,
                                  @RequestParam String endDate,
                                  @RequestParam String type) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(startDate);
        Date endDay = simpleDateFormat.parse(endDate);
        return reportService.report(startDay, endDay, idWorkspace,type);
    }

    @GetMapping("/{idWorkspace}/exportReport")
    public void exportToExcel(HttpServletResponse response,
                              @PathVariable Integer idWorkspace,
                              @RequestParam String startDate,
                              @RequestParam String endDate,
                              @RequestParam String type) throws IOException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDay = simpleDateFormat.parse(startDate);
        Date endDay = simpleDateFormat.parse(endDate);
        response.setContentType("application/octet-stream");
        String currentDateTime = simpleDateFormat.format(new Date());

        String headerKey = CSVFile.HEADER_KEY;
        String headerValue = "attachment; filename=report_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        reportService.export(response,idWorkspace,startDay,endDay,type);

    }
}
