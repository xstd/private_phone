package com.xstd.pirvatephone.dao.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ProxyServiceDaoUtils {

	private static final String DATABASE_NAME = "proxy_service.db";

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

	public static ProxyTalkDao getProxyTalkDao(Context context) {
		if (sDaoSession == null) {
			return getDaoSession(context).getProxyTalkDao();
		}
		return sDaoSession.getProxyTalkDao();
	}

}
