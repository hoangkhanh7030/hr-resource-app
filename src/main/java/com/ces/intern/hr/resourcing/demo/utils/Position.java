package com.ces.intern.hr.resourcing.demo.utils;

public enum Position {
    ACCOUNTMANAGER("ACCOUNT MANAGEMENT"),PROJECTMANAGER("PROJECT MANAGEMENT");
    private String name;

    Position(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
