package com.xstd.pirvatephone.dao.sms;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SMS_DETAIL.
 */
public class SmsDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String phone_number;
    private Long date;
    private String data;
    private Integer thread_id;

    public SmsDetail() {
    }

    public SmsDetail(Long id) {
        this.id = id;
    }

    public SmsDetail(Long id, String phone_number, Long date, String data, Integer thread_id) {
        this.id = id;
        this.phone_number = phone_number;
        this.date = date;
        this.data = data;
        this.thread_id = thread_id;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getThread_id() {
        return thread_id;
    }

    public void setThread_id(Integer thread_id) {
        this.thread_id = thread_id;
    }

    @Override
    public String toString() {
        return "[SmsDetail]" + "id = " + id + ", " + "phone_number = " + phone_number + ", " + "date = " + date + ", " + "data = " + data + ", " + "thread_id = " + thread_id + "\r\n";
    }

}
