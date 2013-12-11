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
        
        Schema schema = new Schema(VERSION, "com.xstd.active.plugin.dao");
        Entity contactInfo = schema.addEntity("SilenceApp");
        contactInfo.addIdProperty();
        contactInfo.addStringProperty("packagename");
        contactInfo.addStringProperty("display_name").notNull();
        contactInfo.addLongProperty("contact_id");
        contactInfo.addLongProperty("photo_id");
        contactInfo.addIntProperty("type").notNull();
        
        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
