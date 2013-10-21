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

        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.privacy");

        Entity talk = schema.addEntity("PrivacyFile");
        talk.addIdProperty();
        talk.addStringProperty("srcName").notNull();
        talk.addStringProperty("destName").notNull();
        talk.addStringProperty("srcPath").notNull();
        talk.addDateProperty("misstime");
        talk.addIntProperty("type");

        Entity ticket = schema.addEntity("PrivacyPwd");
        ticket.addIdProperty();
        ticket.addStringProperty("name").notNull();
        ticket.addStringProperty("site");
        ticket.addStringProperty("number");
        ticket.addStringProperty("password").notNull();

        Entity pic = schema.addEntity("PrivacyPic");
        pic.addIdProperty();
        pic.addStringProperty("album").notNull();

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
