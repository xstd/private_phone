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
       /* Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.demo");

        Entity note = schema.addEntity("PMProtocal");
        note.addLongProperty("time").primaryKeyAsc();
        note.addIntProperty("mqttId").unique();
        note.addStringProperty("content").notNull();
        note.addIntProperty("count");
        note.addLongProperty("localId");
        note.addIntProperty("ownerId");

        Entity userNote = schema.addEntity("PushUserInfo");
        userNote.addLongProperty("id").primaryKey();
        userNote.addLongProperty("lastLoginTime");
        userNote.addStringProperty("generateId");
        userNote.addStringProperty("ticket");
        userNote.addStringProperty("secret_key");
        userNote.addStringProperty("server");

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.phone");

        Entity recordNote = schema.addEntity("PhoneRecord");
        recordNote.addIdProperty();
        recordNote.addStringProperty("phone_number");
        recordNote.addStringProperty("name");
        recordNote.addIntProperty("type");
        recordNote.addLongProperty("date");
        recordNote.addIntProperty("contact_times");

        Entity userNote = schema.addEntity("PhoneDetail");
        userNote.addIdProperty();
        userNote.addStringProperty("phone_number");
        userNote.addStringProperty("name");
        userNote.addIntProperty("type");
        userNote.addLongProperty("date");
        userNote.addLongProperty("duration");

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
