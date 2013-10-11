package com.xstd.pirvatephone.dao.service;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PROXY_TALK.
 */
public class ProxyTalk implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String phonenumber;
    private String weixinnumber;
    private String weixinpwd;
    private java.util.Date starttime;
    private java.util.Date endtime;
    private Integer type;

    public ProxyTalk() {
    }

    public ProxyTalk(Long id) {
        this.id = id;
    }

    public ProxyTalk(Long id, String phonenumber, String weixinnumber, String weixinpwd, java.util.Date starttime, java.util.Date endtime, Integer type) {
        this.id = id;
        this.phonenumber = phonenumber;
        this.weixinnumber = weixinnumber;
        this.weixinpwd = weixinpwd;
        this.starttime = starttime;
        this.endtime = endtime;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getWeixinnumber() {
        return weixinnumber;
    }

    public void setWeixinnumber(String weixinnumber) {
        this.weixinnumber = weixinnumber;
    }

    public String getWeixinpwd() {
        return weixinpwd;
    }

    public void setWeixinpwd(String weixinpwd) {
        this.weixinpwd = weixinpwd;
    }

    public java.util.Date getStarttime() {
        return starttime;
    }

    public void setStarttime(java.util.Date starttime) {
        this.starttime = starttime;
    }

    public java.util.Date getEndtime() {
        return endtime;
    }

    public void setEndtime(java.util.Date endtime) {
        this.endtime = endtime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[ProxyTalk]" + "id = " + id + ", " + "phonenumber = " + phonenumber + ", " + "weixinnumber = " + weixinnumber + ", " + "weixinpwd = " + weixinpwd + ", " + "starttime = " + starttime + ", " + "endtime = " + endtime + ", " + "type = " + type + "\r\n";
    }

}
