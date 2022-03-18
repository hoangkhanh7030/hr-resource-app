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
public class DayOffRequest {
    private Integer id;
    private String startDate;
    private String endDate;
    private Boolean status;
    private Integer resourceId;

    public boolean validate() {
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty() ||
                resourceId == null ) {
            return true;
        } else {
            return false;
        }
    }
}
