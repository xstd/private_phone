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
<<<<<<< Updated upstream
        /*Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.demo");
=======
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.privacy");
>>>>>>> Stashed changes

        Entity note = schema.addEntity("PMProtocal");
        note.addLongProperty("time").primaryKeyAsc();
        note.addIntProperty("mqttId").unique();
        note.addStringProperty("content").notNull();
        note.addIntProperty("count");
        note.addLongProperty("localId");
        note.addIntProperty("ownerId");

<<<<<<< Updated upstream
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

        Entity contactNote = schema.addEntity("PhoneRecord");
        contactNote.addIdProperty();
        contactNote.addLongProperty("phone_number");
        contactNote.addLongProperty("start_time");
        contactNote.addLongProperty("duration");
        contactNote.addIntProperty("type");
        contactNote.addStringProperty("name");

=======
        Entity userNote = schema.addEntity("PushUserInfo");
        userNote.addLongProperty("id").primaryKey();
        userNote.addLongProperty("lastLoginTime");
        userNote.addStringProperty("generateId");
        userNote.addStringProperty("ticket");
        userNote.addStringProperty("secret_key");
        userNote.addStringProperty("server");
>>>>>>> Stashed changes
        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
