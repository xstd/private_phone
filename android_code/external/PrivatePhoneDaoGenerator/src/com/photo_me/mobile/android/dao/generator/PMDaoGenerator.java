package com.photo_me.mobile.android.dao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class PMDaoGenerator {

    private static final int VERSION = 1;

    public static void main(String[] args) throws Exception {
        generateMQTTDaoModel();
    }
    
    
    private static void generateMQTTDaoModel() {
        /*Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.demo");

        Entity note = schema.addEntity("PMProtocal");
        note.addLongProperty("time").primaryKeyAsc();
        note.addIntProperty("mqttId").unique();
        note.addStringProperty("content").notNull();
        note.addIntProperty("count");
        note.addLongProperty("localId");
        note.addIntProperty("ownerId");

        Entity contactNote = schema.addEntity("PushUserInfo");
        contactNote.addLongProperty("id").primaryKey();
        contactNote.addLongProperty("lastLoginTime");
        contactNote.addStringProperty("generateId");
        contactNote.addStringProperty("ticket");
        contactNote.addStringProperty("secret_key");
        contactNote.addStringProperty("server");

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
        
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.sms");

        Entity smsNote = schema.addEntity("SmsRecord");
        smsNote.addIdProperty();
        smsNote.addStringProperty("phone_number");
        smsNote.addLongProperty("lasted_contact");
        smsNote.addStringProperty("lasted_data");
        smsNote.addIntProperty("count");
        
        Entity smsNote2 = schema.addEntity("SmsDetail");
        smsNote2.addIdProperty();
        smsNote2.addStringProperty("phone_number");
        smsNote2.addLongProperty("date");
        smsNote2.addStringProperty("data");
        smsNote2.addIntProperty("thread_id");
        
        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
