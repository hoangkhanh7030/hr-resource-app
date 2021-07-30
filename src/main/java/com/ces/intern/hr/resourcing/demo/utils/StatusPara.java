package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

@AllArgsConstructor
public enum StatusPara {
    ACTIVE("ACTIVE"),ARCHIVED("ARCHIVED"),ALL("ALL");
    private String name;
}
