package com.xstd.pirvatephone.dao.phone;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PHONE_RECORD.
 */
public class PhoneRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String phone_number;
    private Long start_time;
    private Long duration;
    private Integer type;
    private String name;

    public PhoneRecord() {
    }

    public PhoneRecord(Long id) {
        this.id = id;
    }

    public PhoneRecord(Long id, String phone_number, Long start_time, Long duration, Integer type, String name) {
        this.id = id;
        this.phone_number = phone_number;
        this.start_time = start_time;
        this.duration = duration;
        this.type = type;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Long getStart_time() {
        return start_time;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[PhoneRecord]" + "id = " + id + ", " + "phone_number = " + phone_number + ", " + "start_time = " + start_time + ", " + "duration = " + duration + ", " + "type = " + type + ", " + "name = " + name + "\r\n";
    }

}
