package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.response.report.ProjectReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;

import java.util.Date;

public interface ReportService {

    ProjectReportResponse reportProject(Date startDate,Date endDate, Integer idWorkspace);
    ResourceReportResponse reportResource(Date startDate,Date endDate,Integer idWorkspace);
}
