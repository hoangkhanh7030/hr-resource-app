package com.ces.intern.hr.resourcing.demo.http.response.project;

import com.ces.intern.hr.resourcing.demo.http.response.BaseResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ResourceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse extends BaseResponse {
    private String name;
    private String color;
    private boolean isActivate;
    private ResourceResponse ProjectManager;
    private ResourceResponse AccountManager;

}
