package com.xstd.pirvatephone.dao.demo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.demo.PMProtocal;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PMPROTOCAL.
*/
public class PMProtocalDao extends AbstractDao<PMProtocal, Long> {

    public static final String TABLENAME = "PMPROTOCAL";

    /**
     * Properties of entity PMProtocal.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Time = new Property(0, Long.class, "time", true, "TIME");
        public final static Property MqttId = new Property(1, Integer.class, "mqttId", false, "MQTT_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property Count = new Property(3, Integer.class, "count", false, "COUNT");
        public final static Property LocalId = new Property(4, Long.class, "localId", false, "LOCAL_ID");
        public final static Property OwnerId = new Property(5, Integer.class, "ownerId", false, "OWNER_ID");
    };


    public PMProtocalDao(DaoConfig config) {
        super(config);
    }
    
    public PMProtocalDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PMPROTOCAL' (" + //
                "'TIME' INTEGER PRIMARY KEY ASC ," + // 0: time
                "'MQTT_ID' INTEGER UNIQUE ," + // 1: mqttId
                "'CONTENT' TEXT NOT NULL ," + // 2: content
                "'COUNT' INTEGER," + // 3: count
                "'LOCAL_ID' INTEGER," + // 4: localId
                "'OWNER_ID' INTEGER);"); // 5: ownerId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PMPROTOCAL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PMProtocal entity) {
        stmt.clearBindings();
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(1, time);
        }
 
        Integer mqttId = entity.getMqttId();
        if (mqttId != null) {
            stmt.bindLong(2, mqttId);
        }
        stmt.bindString(3, entity.getContent());
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(4, count);
        }
 
        Long localId = entity.getLocalId();
        if (localId != null) {
            stmt.bindLong(5, localId);
        }
 
        Integer ownerId = entity.getOwnerId();
        if (ownerId != null) {
            stmt.bindLong(6, ownerId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PMProtocal readEntity(Cursor cursor, int offset) {
        PMProtocal entity = new PMProtocal( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // time
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // mqttId
            cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // count
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // localId
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // ownerId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PMProtocal entity, int offset) {
        entity.setTime(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMqttId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setContent(cursor.getString(offset + 2));
        entity.setCount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setLocalId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setOwnerId(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PMProtocal entity, long rowId) {
        entity.setTime(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PMProtocal entity) {
        if(entity != null) {
            return entity.getTime();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}