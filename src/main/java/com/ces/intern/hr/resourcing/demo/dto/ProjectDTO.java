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
public class ProjectDTO extends BaseDTO{
    private String name;
    private String color;
    private Boolean isActivate;
    private String AccountManager;
    private String ProjectManager;

}
