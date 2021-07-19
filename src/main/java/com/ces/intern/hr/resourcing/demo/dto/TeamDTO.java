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
public class TeamDTO {
    private Integer id;
    private String name;
    private WorkspaceDTO workspaceDTO;
    private List<PositionDTO> positionDTOS;
}
