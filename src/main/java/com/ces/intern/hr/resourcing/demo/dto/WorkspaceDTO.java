package com.ces.intern.hr.resourcing.demo.dto;

import com.ces.intern.hr.resourcing.demo.http.response.project.ProjectResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ResourceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO extends BaseDTO{
    private String name;
    private String Role;
    private String emailSuffix;
    private List<Boolean> workDays = new ArrayList<>();
    private List<ProjectResponse> projects;
    private List<ResourceResponse> resources;
}
