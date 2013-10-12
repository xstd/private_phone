package com.xstd.pirvatephone.dao.contact;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ContactInfoDaoUtils {

	private static final String DATABASE_NAME = "private_contact.db";

    private static DaoSession sDaoSession;

    public static DaoSession getDaoSession(Context context) {

        if (sDaoSession == null) {

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster m = new DaoMaster(database);

            sDaoSession = m.newSession();

        }

        return sDaoSession;
    }
    
    public static ContactInfoDao getContactInfoDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getContactInfoDao();
    	}
    	return sDaoSession.getContactInfoDao();
    }
}
