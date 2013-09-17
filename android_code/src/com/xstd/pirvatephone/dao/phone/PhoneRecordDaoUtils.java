package com.xstd.pirvatephone.dao.phone;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PhoneRecordDaoUtils {

	private static final String DATABASE_NAME = "phone_record.db";

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
    
    public static PhoneRecordDao getPhoneRecordDao(Context context) {
    	if(sDaoSession == null) {
    		return getDaoSession(context).getPhoneRecordDao();
    	}
    	return sDaoSession.getPhoneRecordDao();
    }
}
