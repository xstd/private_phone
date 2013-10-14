package com.xstd.pirvatephone.dao.simulacomm;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SIMULATE_COMM.
 */
public class SimulateComm implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    /** Not-null value. */
    private String phonenumber;
    /** Not-null value. */
    private java.util.Date futuretime;
    private String content;
    private Integer type;

    public SimulateComm() {
    }

    public SimulateComm(Long id) {
        this.id = id;
    }

    public SimulateComm(Long id, String name, String phonenumber, java.util.Date futuretime, String content, Integer type) {
        this.id = id;
        this.name = name;
        this.phonenumber = phonenumber;
        this.futuretime = futuretime;
        this.content = content;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getPhonenumber() {
        return phonenumber;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /** Not-null value. */
    public java.util.Date getFuturetime() {
        return futuretime;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFuturetime(java.util.Date futuretime) {
        this.futuretime = futuretime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[SimulateComm]" + "id = " + id + ", " + "name = " + name + ", " + "phonenumber = " + phonenumber + ", " + "futuretime = " + futuretime + ", " + "content = " + content + ", " + "type = " + type + "\r\n";
    }

}
