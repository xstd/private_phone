package com.xstd.pirvatephone.dao.use.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.dao.demo.DaoMaster;
import com.xstd.pirvatephone.dao.demo.DaoSession;

public class PushDaoUtils {

    private static final String DATABASE_NAME = "pm_push";

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

}
