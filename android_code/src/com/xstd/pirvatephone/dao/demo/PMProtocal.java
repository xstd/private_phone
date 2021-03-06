package com.xstd.pirvatephone.dao.demo;

import java.io.Serializable;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PMPROTOCAL.
 */
public class PMProtocal implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long time;
    private Integer mqttId;
    /** Not-null value. */
    private String content;
    private Integer count;
    private Long localId;
    private Integer ownerId;

    public PMProtocal() {
    }

    public PMProtocal(Long time) {
        this.time = time;
    }

    public PMProtocal(Long time, Integer mqttId, String content, Integer count, Long localId, Integer ownerId) {
        this.time = time;
        this.mqttId = mqttId;
        this.content = content;
        this.count = count;
        this.localId = localId;
        this.ownerId = ownerId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getMqttId() {
        return mqttId;
    }

    public void setMqttId(Integer mqttId) {
        this.mqttId = mqttId;
    }

    /** Not-null value. */
    public String getContent() {
        return content;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "[PMProtocal]" + "time = " + time + ", " + "mqttId = " + mqttId + ", " + "content = " + content + ", " + "count = " + count + ", " + "localId = " + localId + ", " + "ownerId = " + ownerId + "\r\n";
    }

}
