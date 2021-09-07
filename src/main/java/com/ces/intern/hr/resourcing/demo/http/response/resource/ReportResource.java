package com.ces.intern.hr.resourcing.demo.http.response.resource;

import com.ces.intern.hr.resourcing.demo.http.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResource extends BaseResponse {
    private String name;
    private String teamName;
    private String positionName;
    private Double workingDays;
    private Double overtimeDays;
}
