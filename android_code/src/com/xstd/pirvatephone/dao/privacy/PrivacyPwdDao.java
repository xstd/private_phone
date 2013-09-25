package com.xstd.pirvatephone.dao.privacy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.privacy.PrivacyPwd;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PRIVACY_PWD.
*/
public class PrivacyPwdDao extends AbstractDao<PrivacyPwd, Long> {

    public static final String TABLENAME = "PRIVACY_PWD";

    /**
     * Properties of entity PrivacyPwd.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Site = new Property(2, String.class, "site", false, "SITE");
        public final static Property Number = new Property(3, String.class, "number", false, "NUMBER");
        public final static Property Password = new Property(4, String.class, "password", false, "PASSWORD");
    };


    public PrivacyPwdDao(DaoConfig config) {
        super(config);
    }
    
    public PrivacyPwdDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PRIVACY_PWD' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'SITE' TEXT," + // 2: site
                "'NUMBER' TEXT," + // 3: number
                "'PASSWORD' TEXT NOT NULL );"); // 4: password
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PRIVACY_PWD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PrivacyPwd entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
 
        String site = entity.getSite();
        if (site != null) {
            stmt.bindString(3, site);
        }
 
        String number = entity.getNumber();
        if (number != null) {
            stmt.bindString(4, number);
        }
        stmt.bindString(5, entity.getPassword());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PrivacyPwd readEntity(Cursor cursor, int offset) {
        PrivacyPwd entity = new PrivacyPwd( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // site
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // number
            cursor.getString(offset + 4) // password
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PrivacyPwd entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setSite(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPassword(cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PrivacyPwd entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PrivacyPwd entity) {
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
