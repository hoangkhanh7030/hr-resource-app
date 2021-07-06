package com.ces.intern.hr.resourcing.demo.importCSV.Message;

public class Message {
    private String filename;
    private String message;
    private Integer status;

    public Message(String filename, String message, Integer status) {
        this.filename = filename;
        this.message = message;
        this.status = status;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
