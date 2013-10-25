package com.xstd.pirvatephone.dao.simulacomm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SimulateDaoUtils {

	private static final String DATABASE_NAME = "simulate.db";

	private static DaoSession sDaoSession;

	public static DaoSession getDaoSession(Context context) {

		if (sDaoSession == null) {

			DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(
					context, DATABASE_NAME, null);
			SQLiteDatabase database = helper.getWritableDatabase();
			DaoMaster m = new DaoMaster(database);

			sDaoSession = m.newSession();

		}

		return sDaoSession;
	}

	public static SimulateCommDao getSimulateDao(Context context) {
		if (sDaoSession == null) {
			return getDaoSession(context).getSimulateCommDao();
		}
		return sDaoSession.getSimulateCommDao();
	}

}
