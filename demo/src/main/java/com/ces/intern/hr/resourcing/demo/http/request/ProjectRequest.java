package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequest {
    private String name;
    private String color;
    private Integer idProjectManager;
    private Integer idAccountManager;

}
