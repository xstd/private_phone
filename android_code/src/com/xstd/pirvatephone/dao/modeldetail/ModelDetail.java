package com.xstd.pirvatephone.dao.modeldetail;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MODEL_DETAIL.
 */
public class ModelDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String address;
    private String name;
    private String massage;

    public ModelDetail() {
    }

    public ModelDetail(Long id) {
        this.id = id;
    }

    public ModelDetail(Long id, String address, String name, String massage) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.massage = massage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    @Override
    public String toString() {
        return "[ModelDetail]" + "id = " + id + ", " + "address = " + address + ", " + "name = " + name + ", " + "massage = " + massage + "\r\n";
    }

}
