package com.xstd.pirvatephone.dao.modeldetail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ModelDetailDaoUtils {

	private static final String DATABASE_NAME = "modeldetail.db";

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
    
    public static ModelDetailDao getModelDetailDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getModelDetailDao();
    	}
    	return sDaoSession.getModelDetailDao();
    }
}
