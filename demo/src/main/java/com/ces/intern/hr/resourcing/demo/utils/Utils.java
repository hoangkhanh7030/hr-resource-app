package com.ces.intern.hr.resourcing.demo.utils;

public enum Utils {
    VIEW("VIEW",1),EDIT("EDIT",2),MANAGER("Manager",1),MEMBER("Member",2);
    private String name;
    private Integer code;

    Utils(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
