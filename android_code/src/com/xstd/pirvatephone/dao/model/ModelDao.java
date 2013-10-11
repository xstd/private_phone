package com.xstd.pirvatephone.dao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.model.Model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MODEL.
*/
public class ModelDao extends AbstractDao<Model, Long> {

    public static final String TABLENAME = "MODEL";

    /**
     * Properties of entity Model.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Model_name = new Property(1, String.class, "model_name", false, "MODEL_NAME");
        public final static Property Model_type = new Property(2, Integer.class, "model_type", false, "MODEL_TYPE");
    };


    public ModelDao(DaoConfig config) {
        super(config);
    }
    
    public ModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MODEL' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'MODEL_NAME' TEXT," + // 1: model_name
                "'MODEL_TYPE' INTEGER);"); // 2: model_type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MODEL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Model entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String model_name = entity.getModel_name();
        if (model_name != null) {
            stmt.bindString(2, model_name);
        }
 
        Integer model_type = entity.getModel_type();
        if (model_type != null) {
            stmt.bindLong(3, model_type);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Model readEntity(Cursor cursor, int offset) {
        Model entity = new Model( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // model_name
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2) // model_type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Model entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setModel_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setModel_type(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Model entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Model entity) {
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