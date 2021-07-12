package com.ces.intern.hr.resourcing.demo.dto;

import com.ces.intern.hr.resourcing.demo.http.response.ProjectResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO extends BaseDTO{
    private String name;
    private String Role;
    private List<ProjectResponse> projects;
    private List<ResourceResponse> resources;



}
