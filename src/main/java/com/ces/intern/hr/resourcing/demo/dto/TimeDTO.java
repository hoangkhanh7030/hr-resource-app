package com.ces.intern.hr.resourcing.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeDTO {
    private Integer id;

    private Date startTime;

    private Date endTime;

    private ResourceDTO resourceDTO;

    private ProjectDTO projectDTO;
}
