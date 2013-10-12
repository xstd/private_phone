package com.xstd.pirvatephone.dao.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ModelDaoUtils {

	private static final String DATABASE_NAME = "model.db";

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
    
    public static ModelDao getModelDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getModelDao();
    	}
    	return sDaoSession.getModelDao();
    }
}
