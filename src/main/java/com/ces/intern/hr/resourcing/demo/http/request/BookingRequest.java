package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Integer id;
    private String startDate;
    private String endDate;
    private Double percentage;
    private Double duration;
    private Integer idProject;
    private Integer idResource;


}
