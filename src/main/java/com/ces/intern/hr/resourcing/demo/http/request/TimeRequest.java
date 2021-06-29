package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeRequest {
    private Date date;
    private Integer resourceId;
    private Integer projectId;
    private Integer startHour;
    private Integer endHour;
}
