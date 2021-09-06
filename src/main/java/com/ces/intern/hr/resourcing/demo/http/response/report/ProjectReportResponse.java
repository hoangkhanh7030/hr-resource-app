package com.ces.intern.hr.resourcing.demo.http.response.report;

import com.ces.intern.hr.resourcing.demo.http.response.project.ReportProject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectReportResponse {
    private Double workingDays;
    private Double overtimeDays;
    private List<ReportProject> projects;
}
