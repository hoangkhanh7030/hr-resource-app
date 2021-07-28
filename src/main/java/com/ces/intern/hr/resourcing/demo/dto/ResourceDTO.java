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
    //private WorkspaceDTO workspaceName;
    //private List<ProjectDTO> listProject;
    //private TeamDTO teamDTO;
    private PositionDTO positionDTO;
    private List<TimeDTO> listTime;
    private Boolean isArchived;
}
