package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.report.Report;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public interface ReportService {
    Report report(Date startDate,Date endDate, Integer idWorkspace,String type);
    ProjectReportResponse reportProject(Date startDate,Date endDate, Integer idWorkspace,Integer time);
    ResourceReportResponse reportResource(Date startDate,Date endDate,Integer idWorkspace,Integer time);
    void export (HttpServletResponse response,Integer idWorkspace,Date startDate,Date endDate,String type) throws IOException;
}
