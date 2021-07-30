package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

@AllArgsConstructor
public enum ColumnPara {
    TEAM("team"),POSITION("position"),NAME("name"),STATUS("status");
    private String name;
}
