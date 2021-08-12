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
    private Integer projectId;
    private Integer resourceId;

    public boolean validate() {
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty() ||
                projectId == null || duration == null ||
                resourceId == null || percentage==null) {
            return true;
        } else {
            return false;
        }
    }


}
