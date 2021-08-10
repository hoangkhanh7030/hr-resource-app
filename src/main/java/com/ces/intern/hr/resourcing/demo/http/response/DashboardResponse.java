package com.ces.intern.hr.resourcing.demo.http.response;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class DashboardResponse {
    private Integer id;
    private String startDate;
    private String endDate;
    private Double percentage;
    private Double duration;
    private ProjectDTO projectDTO;
    private Double hourTotal;

    public DashboardResponse() {

    }
}
