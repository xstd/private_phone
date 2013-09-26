package com.xstd.pirvatephone.dao.sms;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SMS_RECORD.
*/
public class SmsRecordDao extends AbstractDao<SmsRecord, Long> {

    public static final String TABLENAME = "SMS_RECORD";

    /**
     * Properties of entity SmsRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Phone_number = new Property(1, String.class, "phone_number", false, "PHONE_NUMBER");
        public final static Property Lasted_contact = new Property(2, Long.class, "lasted_contact", false, "LASTED_CONTACT");
        public final static Property Lasted_data = new Property(3, String.class, "lasted_data", false, "LASTED_DATA");
        public final static Property Count = new Property(4, Integer.class, "count", false, "COUNT");
    };


    public SmsRecordDao(DaoConfig config) {
        super(config);
    }
    
    public SmsRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SMS_RECORD' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PHONE_NUMBER' TEXT," + // 1: phone_number
                "'LASTED_CONTACT' INTEGER," + // 2: lasted_contact
                "'LASTED_DATA' TEXT," + // 3: lasted_data
                "'COUNT' INTEGER);"); // 4: count
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SMS_RECORD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SmsRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String phone_number = entity.getPhone_number();
        if (phone_number != null) {
            stmt.bindString(2, phone_number);
        }
 
        Long lasted_contact = entity.getLasted_contact();
        if (lasted_contact != null) {
            stmt.bindLong(3, lasted_contact);
        }
 
        String lasted_data = entity.getLasted_data();
        if (lasted_data != null) {
            stmt.bindString(4, lasted_data);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(5, count);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SmsRecord readEntity(Cursor cursor, int offset) {
        SmsRecord entity = new SmsRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // phone_number
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // lasted_contact
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // lasted_data
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // count
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SmsRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhone_number(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLasted_contact(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setLasted_data(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCount(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SmsRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SmsRecord entity) {
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
