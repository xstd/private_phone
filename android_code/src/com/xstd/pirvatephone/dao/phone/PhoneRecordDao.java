package com.xstd.pirvatephone.dao.phone;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.phone.PhoneRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PHONE_RECORD.
*/
public class PhoneRecordDao extends AbstractDao<PhoneRecord, Long> {

    public static final String TABLENAME = "PHONE_RECORD";

    /**
     * Properties of entity PhoneRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Phone_number = new Property(1, String.class, "phone_number", false, "PHONE_NUMBER");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Body = new Property(3, String.class, "body", false, "BODY");
        public final static Property Date = new Property(4, Long.class, "date", false, "DATE");
        public final static Property Contact_times = new Property(5, Integer.class, "contact_times", false, "CONTACT_TIMES");
    };


    public PhoneRecordDao(DaoConfig config) {
        super(config);
    }
    
    public PhoneRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PHONE_RECORD' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PHONE_NUMBER' TEXT," + // 1: phone_number
                "'NAME' TEXT," + // 2: name
                "'BODY' TEXT," + // 3: body
                "'DATE' INTEGER," + // 4: date
                "'CONTACT_TIMES' INTEGER);"); // 5: contact_times
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PHONE_RECORD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PhoneRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String phone_number = entity.getPhone_number();
        if (phone_number != null) {
            stmt.bindString(2, phone_number);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String body = entity.getBody();
        if (body != null) {
            stmt.bindString(4, body);
        }
 
        Long date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date);
        }
 
        Integer contact_times = entity.getContact_times();
        if (contact_times != null) {
            stmt.bindLong(6, contact_times);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PhoneRecord readEntity(Cursor cursor, int offset) {
        PhoneRecord entity = new PhoneRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // phone_number
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // body
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // date
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // contact_times
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PhoneRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhone_number(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBody(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setContact_times(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PhoneRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PhoneRecord entity) {
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
