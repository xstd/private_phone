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

        Entity entity = schema.addEntity("ProxyTalk");
        entity.addIdProperty();
        entity.addStringProperty("phonenumber");
        entity.addStringProperty("weixinnumber");
        entity.addStringProperty("weixinpwd");
        entity.addStringProperty("talk_content").notNull();
        entity.addDateProperty("starttime").notNull();
        entity.addDateProperty("endtime").notNull();
        entity.addIntProperty("type").notNull();

        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
