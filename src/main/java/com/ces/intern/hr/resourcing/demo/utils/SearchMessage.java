package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum SearchMessage {
    PROJECT_NAME("project_name"),CLIENT_NAME("client_name");
    private String name;

}
