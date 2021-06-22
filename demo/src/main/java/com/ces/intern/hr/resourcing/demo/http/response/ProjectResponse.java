package com.ces.intern.hr.resourcing.demo.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse extends BaseResponse{
    private String name;
    private String color;
    private boolean isActivate;
}
