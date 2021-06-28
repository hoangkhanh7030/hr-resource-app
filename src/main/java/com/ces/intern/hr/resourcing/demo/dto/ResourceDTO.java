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
public class ResourceDTO extends BaseDTO{
    private String name;
    private String avatar;
    private String workspaceName;
    private List<ProjectDTO> listProject;
}
