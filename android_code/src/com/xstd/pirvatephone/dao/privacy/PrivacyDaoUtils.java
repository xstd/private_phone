package com.xstd.pirvatephone.dao.privacy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PrivacyDaoUtils {

	private static final String DATABASE_NAME = "privacy_mapping.db";

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
    
    public static SrcToDestMappingDao getPrivacyDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getSrcToDestMappingDao();
    	}
    	return sDaoSession.getSrcToDestMappingDao();
    }
}
