package com.photo_me.mobile.android.dao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class PMDaoGenerator {

    private static final String PACKAGE_NAME = "com.xstd.pirvatephone.dao.model";

    private static final int VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(VERSION, PACKAGE_NAME);

        generateChat(schema);

        new DaoGenerator().generateAll(schema, "../../src");
    }

    private static void generateChat(Schema schema) {
        Entity note = schema.addEntity("Chat");

        note.addIntProperty("direction").notNull();
        note.addIntProperty("sendStatus");

        note.addLongProperty("ownerId").notNull();
        note.addLongProperty("conversationId").notNull();
        note.addLongProperty("localTime").notNull();

        note.addIntProperty("type").notNull();
        note.addIntProperty("subtype").notNull();
        note.addLongProperty("txid").primaryKey();
        note.addLongProperty("time").notNull();

        note.addIntProperty("fromType");
        note.addLongProperty("fromId").notNull();
        note.addStringProperty("fromName");
        note.addStringProperty("fromAvatar");

        note.addIntProperty("toType");
        note.addLongProperty("toId").notNull();
        note.addStringProperty("toName");
        note.addStringProperty("toAvatar");

        note.addIntProperty("relatedType");
        note.addLongProperty("relatedId");
        note.addStringProperty("relatedName");
        note.addStringProperty("relatedAvatar");

        note.addIntProperty("tplId");
        note.addStringProperty("text");
        note.addLongProperty("soundId");
        note.addIntProperty("soundDuration");
        note.addStringProperty("soundUrl");
        note.addLongProperty("imgId");
        note.addStringProperty("imgPrefix");
        note.addStringProperty("imgOrigin");
        note.addStringProperty("imgW720");
        note.addStringProperty("imgW640");
        note.addIntProperty("imgWidth");
        note.addIntProperty("imgHeight");
        note.addStringProperty("imgLocalPath");
        note.addStringProperty("url");
        note.addLongProperty("latitude");
        note.addLongProperty("langitude");
        note.addLongProperty("altitude");
        note.addLongProperty("refId");
        note.addIntProperty("refType");
        note.addLongProperty("refUserId");
        note.addStringProperty("refUserName");
        note.addStringProperty("refUserAvatar");
        note.addStringProperty("refDesc");
        note.addLongProperty("refImgId");
        note.addStringProperty("refImgPrefix");
        note.addStringProperty("refImgOrigin");
        note.addStringProperty("refImgW720");
        note.addStringProperty("refImgW640");
        note.addIntProperty("refImgWidth");
        note.addIntProperty("refImgHeight");
        note.addIntProperty("refPokeCount");
        note.addLongProperty("refPostTime");
    }

}
