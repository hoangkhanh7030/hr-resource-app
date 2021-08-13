package com.ces.intern.hr.resourcing.demo.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResponse extends BaseResponse{
    private String name;
    private String avatar;
    private String position;
    private Integer teamId;
    private Double percent;
    private List<List<DashboardResponse>> bookings;

}
