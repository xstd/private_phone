package com.xstd.pirvatephone.dao.simulacomm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SIMULATE_COMM.
*/
public class SimulateCommDao extends AbstractDao<SimulateComm, Long> {

    public static final String TABLENAME = "SIMULATE_COMM";

    /**
     * Properties of entity SimulateComm.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Phonenumber = new Property(1, long.class, "phonenumber", false, "PHONENUMBER");
        public final static Property Futuretime = new Property(2, java.util.Date.class, "futuretime", false, "FUTURETIME");
        public final static Property Contemt = new Property(3, String.class, "contemt", false, "CONTEMT");
        public final static Property Type = new Property(4, Integer.class, "type", false, "TYPE");
    };


    public SimulateCommDao(DaoConfig config) {
        super(config);
    }
    
    public SimulateCommDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SIMULATE_COMM' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PHONENUMBER' INTEGER NOT NULL ," + // 1: phonenumber
                "'FUTURETIME' INTEGER NOT NULL ," + // 2: futuretime
                "'CONTEMT' TEXT," + // 3: contemt
                "'TYPE' INTEGER);"); // 4: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SIMULATE_COMM'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SimulateComm entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPhonenumber());
        stmt.bindLong(3, entity.getFuturetime().getTime());
 
        String contemt = entity.getContemt();
        if (contemt != null) {
            stmt.bindString(4, contemt);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(5, type);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SimulateComm readEntity(Cursor cursor, int offset) {
        SimulateComm entity = new SimulateComm( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // phonenumber
            new java.util.Date(cursor.getLong(offset + 2)), // futuretime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // contemt
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SimulateComm entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhonenumber(cursor.getLong(offset + 1));
        entity.setFuturetime(new java.util.Date(cursor.getLong(offset + 2)));
        entity.setContemt(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SimulateComm entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SimulateComm entity) {
        if(entity != null) {
            return entity.getId();
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