package com.udemy.log.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "app-logs")
public class Log {

    @Id
    private String id;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String service;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Text)
    private String message;

    public Log() {
    }

    public Log(Instant timestamp, String service, String level, String message) {
        this.timestamp = timestamp;
        this.service = service;
        this.level = level;
        this.message = message;
    }

    public Log(String id, Instant timestamp, String service, String level, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.service = service;
        this.level = level;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
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
}