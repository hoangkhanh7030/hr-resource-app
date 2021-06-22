package com.ces.intern.hr.resourcing.demo.dto;

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
//    private List<ProjectDTO> projectList;
//    private List<ResourceDTO> resourceList;
    private Integer projectListLength;
    private Integer resourceListLength;


}
