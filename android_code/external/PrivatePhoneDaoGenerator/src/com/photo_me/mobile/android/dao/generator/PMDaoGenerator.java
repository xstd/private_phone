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

        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.service");

        Entity talk = schema.addEntity("ProxyTalk");
        talk.addIdProperty();
        talk.addStringProperty("phonenumber");
        talk.addStringProperty("weixinnumber");
        talk.addStringProperty("weixinpwd");
        talk.addStringProperty("talk_content").notNull();
        talk.addDateProperty("starttime").notNull();
        talk.addDateProperty("endtime").notNull();
        talk.addIntProperty("type").notNull();

        Entity ticket = schema.addEntity("ProxyTicket");
        ticket.addIdProperty();
        ticket.addStringProperty("name").notNull();
        ticket.addStringProperty("cardid").notNull();
        ticket.addStringProperty("flightnumber");
        ticket.addDateProperty("date").notNull();
        ticket.addStringProperty("receiver");
        ticket.addStringProperty("address");
        ticket.addStringProperty("phonenumber");
        ticket.addStringProperty("status");

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
