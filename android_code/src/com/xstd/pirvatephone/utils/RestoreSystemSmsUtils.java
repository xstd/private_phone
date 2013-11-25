package com.xstd.pirvatephone.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class RestoreSystemSmsUtils {

	public static void restoreSms(Context mContext,String[] mPhoneNumbers) {
		//发送关闭观察者广播
		Intent intent = new Intent();
		intent.setAction("ObserverControlRecevier");
		intent.putExtra("IsOpen", 2);
		mContext.sendBroadcast(intent);
		
		
		// 短信恢复
		SmsDetailDao smsDetailDao = SmsDetailDaoUtils.getSmsDetailDao(mContext);
		SQLiteDatabase smsDetailDatabase = smsDetailDao.getDatabase();

		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String phone = mPhoneNumbers[i];
			Tools.logSh("phone============" + phone);

			Cursor smsDetailCursor = smsDetailDatabase.query(
					SmsDetailDao.TABLENAME, null,
					SmsDetailDao.Properties.Phone_number.columnName + "=?",
					new String[] { phone }, null, null, null);
			Tools.logSh("向系统sms数据库中插入数据cursor.count="
					+ smsDetailCursor.getCount());

			if (smsDetailCursor != null) {
				while (smsDetailCursor.moveToNext()) {

					String address = smsDetailCursor
							.getString(smsDetailCursor
									.getColumnIndex(SmsDetailDao.Properties.Phone_number.columnName));
					String body = smsDetailCursor
							.getString(smsDetailCursor
									.getColumnIndex(SmsDetailDao.Properties.Data.columnName));
					int type = smsDetailCursor
							.getInt(smsDetailCursor
									.getColumnIndex(SmsDetailDao.Properties.Thread_id.columnName));
					Long date = smsDetailCursor
							.getLong(smsDetailCursor
									.getColumnIndex(SmsDetailDao.Properties.Date.columnName));
					ContentValues values = new ContentValues();
					/* 手机号 */
					values.put("address", address);
					/* 时间 */
					values.put("date", date);
					values.put("body", body);
					values.put("status", -1);
					/* 类型1为收件箱，2为发件箱 */
					values.put("type", type);
					/* 短信体内容 */
					values.put("read", 1);
					/* 插入数据库操作 */
					Uri inserted = mContext.getContentResolver().insert(
							Uri.parse("content://sms"), values);

					Tools.logSh("成功的向系统联系人数据库插入一个联系人address+" + address
							+ ":::body=" + body + "::type=" + type + "::date="
							+ date + "::uri=" + inserted);
					
					// record短信息移除
					DelectOurSmsRecordsUtils.deleteSmsRecords(mContext, new String[]{phone});

					// detail短信息移除
					DelectOurSmsDetailsUtils.deleteSmsDetails(mContext, new String[]{phone});

				}
				smsDetailCursor.close();
			}
		}
		
		
		//发送开启观察者广播
		intent.putExtra("IsOpen", 1);
		mContext.sendBroadcast(intent);

	}
}
