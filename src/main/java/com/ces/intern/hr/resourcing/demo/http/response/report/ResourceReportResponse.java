package com.ces.intern.hr.resourcing.demo.http.response.report;

import com.ces.intern.hr.resourcing.demo.http.response.resource.ReportResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceReportResponse {
    private Double workingDays;
    private Double overtimeDays;
    private List<ReportResource> resources;
}
