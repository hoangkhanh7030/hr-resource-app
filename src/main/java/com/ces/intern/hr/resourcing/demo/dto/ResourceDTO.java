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
    private TeamDTO teamDTO;
    private PositionDTO positionDTO;
    private Boolean isArchived;
    private List<TimeDTO> listTime;


}
