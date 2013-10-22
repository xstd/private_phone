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
    private String name;
    private Integer type;
    private Long date;
    private Integer contact_times;

    public PhoneRecord() {
    }

    public PhoneRecord(Long id) {
        this.id = id;
    }

    public PhoneRecord(Long id, String phone_number, String name, Integer type, Long date, Integer contact_times) {
        this.id = id;
        this.phone_number = phone_number;
        this.name = name;
        this.type = type;
        this.date = date;
        this.contact_times = contact_times;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getContact_times() {
        return contact_times;
    }

    public void setContact_times(Integer contact_times) {
        this.contact_times = contact_times;
    }

    @Override
    public String toString() {
        return "[PhoneRecord]" + "id = " + id + ", " + "phone_number = " + phone_number + ", " + "name = " + name + ", " + "type = " + type + ", " + "date = " + date + ", " + "contact_times = " + contact_times + "\r\n";
    }

}
