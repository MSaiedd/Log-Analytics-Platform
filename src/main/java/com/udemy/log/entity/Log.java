package com.udemy.log.entity;

import java.sql.Timestamp;

public class Log {
    //that time stamp is different from the one will be added to the database
    //its the timestamp of the log itself
    private Timestamp timestamp;
    private String service;
    private String level;
    private String message;

    public Log() {
    }

    public Log(Timestamp timestamp, String service, String level, String message) {
        this.timestamp = timestamp;
        this.service = service;
        this.level = level;
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Log{" +
                "timestamp=" + timestamp +
                ", service='" + service + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
