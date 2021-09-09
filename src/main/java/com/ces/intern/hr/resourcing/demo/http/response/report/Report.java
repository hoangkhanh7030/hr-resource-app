package com.ces.intern.hr.resourcing.demo.http.response.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private ProjectReportResponse projectReports;
    private ResourceReportResponse resourceReports;
    private Double trafficTime;
    private Double allocatedTime;
    private Double overTime;
}
