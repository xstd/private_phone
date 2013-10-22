package com.xstd.pirvatephone.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;

import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class RestoreSystemPhoneUtils {

	public static void restorePhone(Context mContext,String[] mPhoneNumbers) {
		// 通话记录恢复到手机上
		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
				.getPhoneDetailDao(mContext);
		SQLiteDatabase phoneDetailDatebase = phoneDetailDao.getDatabase();

		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			Cursor phoneDetailCursor = phoneDetailDatebase.query(
					PhoneDetailDao.TABLENAME, null,
					PhoneDetailDao.Properties.Phone_number.columnName + "=?",
					new String[] { number }, null, null, null);

			Tools.logSh("向系统通话记录数据库中插入数据cursor.count="
					+ phoneDetailCursor.getCount());

			if (phoneDetailCursor != null) {
				while (phoneDetailCursor.moveToNext()) {

					String address = phoneDetailCursor
							.getString(phoneDetailCursor
									.getColumnIndex(PhoneDetailDao.Properties.Phone_number.columnName));
					int type = phoneDetailCursor
							.getInt(phoneDetailCursor
									.getColumnIndex(PhoneDetailDao.Properties.Type.columnName));
					Long duration = phoneDetailCursor
							.getLong(phoneDetailCursor
									.getColumnIndex(PhoneDetailDao.Properties.Duration.columnName));

					Long date = phoneDetailCursor
							.getLong(phoneDetailCursor
									.getColumnIndex(PhoneDetailDao.Properties.Date.columnName));

					Tools.logSh("通话记录：：：" + address + "::::" + type + ":::"
							+ duration + "::::" + date);

					// TODO Auto-generated method stub
					ContentValues values = new ContentValues();
					values.put(CallLog.Calls.NUMBER, address);
					values.put(CallLog.Calls.DATE, date);
					values.put(CallLog.Calls.DURATION, duration);
					values.put(CallLog.Calls.TYPE, type);// 未接
					values.put(CallLog.Calls.NEW, 1);// 0已看1未看

					mContext.getContentResolver().insert(
							CallLog.Calls.CONTENT_URI, values);
				}
			}

		}

	}
}
