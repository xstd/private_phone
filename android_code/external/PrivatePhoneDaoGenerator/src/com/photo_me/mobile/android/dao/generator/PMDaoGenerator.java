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
       /* Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.simulacomm");

        Entity entity = schema.addEntity("SimulateComm");
        entity.addIdProperty();
        entity.addStringProperty("name");
        entity.addStringProperty("phonenumber").notNull();
        entity.addDateProperty("futuretime").notNull();
        entity.addStringProperty("content");
        entity.addIntProperty("type");*/
        
        Schema schema = new Schema(VERSION, "com.xstd.pirvatephone.dao.model");

        Entity entity = schema.addEntity("Model");
        entity.addIdProperty();
        entity.addStringProperty("model_name");
        entity.addIntProperty("model_type");
        
        
        try {
            new DaoGenerator().generateAll(schema, "../../src");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
