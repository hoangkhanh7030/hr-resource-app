package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

@AllArgsConstructor
public enum StatusPara {
    ACTIVE("active"),ARCHIVED("archived"),ALL("all");
    private String name;
}
