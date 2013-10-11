package com.xstd.pirvatephone.dao.contextmodel;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ContextModelDaoUtils {

	private static final String DATABASE_NAME = "contextmodel.db";

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
    
    public static ContextModelDao getContextModelDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getContextModelDao();
    	}
    	return sDaoSession.getContextModelDao();
    }
}
