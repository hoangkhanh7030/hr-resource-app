package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter

@AllArgsConstructor
public enum Status {
    SUCCESS("Success",200),FAIL("Fail",400);
    private String name;
    private Integer code;

}
