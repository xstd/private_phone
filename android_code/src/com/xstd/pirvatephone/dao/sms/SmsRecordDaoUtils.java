package com.xstd.pirvatephone.dao.sms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SmsRecordDaoUtils {

	private static final String DATABASE_NAME = "sms.db";

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
    
    public static SmsRecordDao getSmsRecordDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getSmsRecordDao();
    	}
    	return sDaoSession.getSmsRecordDao();
    }
}
