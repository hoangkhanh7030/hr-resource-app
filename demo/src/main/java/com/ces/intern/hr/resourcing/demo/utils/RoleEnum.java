package com.ces.intern.hr.resourcing.demo.utils;

public enum RoleEnum {
    VIEW("VIEW"),EDIT("EDIT");
    private String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
