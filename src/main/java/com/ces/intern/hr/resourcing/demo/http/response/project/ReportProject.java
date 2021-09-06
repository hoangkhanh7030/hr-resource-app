package com.ces.intern.hr.resourcing.demo.http.response.project;

import com.ces.intern.hr.resourcing.demo.http.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportProject extends BaseResponse {
    private String name;
    private String clientName;
    private String color;
    private Double workingDays;
    private Double overtimeDays;

}
