package com.ces.intern.hr.resourcing.demo.http.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequest {
    private Integer id;
    private String name;
    private String avatar;
    private Integer positionId;
    private Integer teamId;
    private String positionName;
    private String teamName;
}
