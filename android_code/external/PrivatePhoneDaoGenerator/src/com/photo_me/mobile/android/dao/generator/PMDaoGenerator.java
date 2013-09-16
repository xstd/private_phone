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
        
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.phone");

        Entity contactNote = schema.addEntity("phone_record");
        contactNote.addIdProperty();
        contactNote.addLongProperty("phone_number");
        contactNote.addIntProperty("type");
        contactNote.addIntProperty("count");
        contactNote.addLongProperty("start_time");
        
        Entity contactNote2 = schema.addEntity("phone_detail");
        contactNote2.addIdProperty();
        contactNote2.addLongProperty("phone_number");
        contactNote2.addIntProperty("type");
        contactNote2.addLongProperty("start_time");
        contactNote2.addLongProperty("end_time");

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
