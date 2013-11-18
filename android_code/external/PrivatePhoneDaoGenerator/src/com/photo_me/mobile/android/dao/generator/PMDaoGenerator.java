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
/*
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.privacy");

        Entity talk = schema.addEntity("PrivacyFile");
        talk.addIdProperty();
        talk.addStringProperty("srcName").notNull();
        talk.addStringProperty("destName").notNull();
        talk.addStringProperty("srcPath").notNull();
        talk.addDateProperty("misstime");
        talk.addIntProperty("type");
        talk.addLongProperty("ref_id");

        Entity ticket = schema.addEntity("PrivacyPwd");
        ticket.addIdProperty();
        ticket.addStringProperty("name").notNull();
        ticket.addStringProperty("site");
        ticket.addStringProperty("number");
        ticket.addStringProperty("password").notNull();

        Entity pic = schema.addEntity("PrivacyPic");
        pic.addIdProperty();
        pic.addStringProperty("name").notNull();*/
        

       /* Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.contact");

        Entity contact = schema.addEntity("ContactInfo");
        contact.addIdProperty();
        contact.addStringProperty("phone_number").notNull();
        contact.addStringProperty("display_name").notNull();
        contact.addIntProperty("type");*/
        
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.contact");
        Entity contactInfo = schema.addEntity("ContactInfo");
        contactInfo.addIdProperty();
        contactInfo.addStringProperty("phone_number").notNull();
        contactInfo.addStringProperty("display_name").notNull();
        contactInfo.addLongProperty("icon_id");
        contactInfo.addIntProperty("type").notNull();
        
        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
