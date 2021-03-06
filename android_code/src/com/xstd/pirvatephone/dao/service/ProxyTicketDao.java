package com.xstd.pirvatephone.dao.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.pirvatephone.dao.service.ProxyTicket;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PROXY_TICKET.
*/
public class ProxyTicketDao extends AbstractDao<ProxyTicket, Long> {

    public static final String TABLENAME = "PROXY_TICKET";

    /**
     * Properties of entity ProxyTicket.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Cardid = new Property(2, String.class, "cardid", false, "CARDID");
        public final static Property Flightnumber = new Property(3, String.class, "flightnumber", false, "FLIGHTNUMBER");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property Receiver = new Property(5, String.class, "receiver", false, "RECEIVER");
        public final static Property Address = new Property(6, String.class, "address", false, "ADDRESS");
        public final static Property Phonenumber = new Property(7, String.class, "phonenumber", false, "PHONENUMBER");
        public final static Property Status = new Property(8, String.class, "status", false, "STATUS");
    };


    public ProxyTicketDao(DaoConfig config) {
        super(config);
    }
    
    public ProxyTicketDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PROXY_TICKET' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'CARDID' TEXT NOT NULL ," + // 2: cardid
                "'FLIGHTNUMBER' TEXT," + // 3: flightnumber
                "'DATE' INTEGER NOT NULL ," + // 4: date
                "'RECEIVER' TEXT," + // 5: receiver
                "'ADDRESS' TEXT," + // 6: address
                "'PHONENUMBER' TEXT," + // 7: phonenumber
                "'STATUS' TEXT);"); // 8: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PROXY_TICKET'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ProxyTicket entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getCardid());
 
        String flightnumber = entity.getFlightnumber();
        if (flightnumber != null) {
            stmt.bindString(4, flightnumber);
        }
        stmt.bindLong(5, entity.getDate().getTime());
 
        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(6, receiver);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(7, address);
        }
 
        String phonenumber = entity.getPhonenumber();
        if (phonenumber != null) {
            stmt.bindString(8, phonenumber);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(9, status);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ProxyTicket readEntity(Cursor cursor, int offset) {
        ProxyTicket entity = new ProxyTicket( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.getString(offset + 2), // cardid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // flightnumber
            new java.util.Date(cursor.getLong(offset + 4)), // date
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // receiver
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // address
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // phonenumber
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ProxyTicket entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setCardid(cursor.getString(offset + 2));
        entity.setFlightnumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(new java.util.Date(cursor.getLong(offset + 4)));
        entity.setReceiver(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAddress(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPhonenumber(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setStatus(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ProxyTicket entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ProxyTicket entity) {
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
