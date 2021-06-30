package com.ces.intern.hr.resourcing.demo.http.response;

import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse extends  BaseResponse{
    private String name;
    private String color;
    private boolean isActivate;
    private ResourceResponse ProjectManager;
    private ResourceResponse AccountManager;

}
