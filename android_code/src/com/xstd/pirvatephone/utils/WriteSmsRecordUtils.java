package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteSmsRecordUtils {

	private static final int ID_COLUMN_INDEX = 0;
	private static final int SNIPPET_COLUMN_INDEX = 1;
	private static final int MSG_COUNT_COLUMN_INDEX = 2;
	private static final int ADDRESS_COLUMN_INDEX = 3;
	private static final int DATE_COLUMN_INDEX = 4;

	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");

	// 查询的字段
	private static String[] PROJECTION = new String[] { "sms.thread_id AS _id",
			"sms.body AS snippet", "groups.msg_count AS msg_count",
			" sms.address AS address", " sms.date AS date" };

	public static void writeSmsRecord(Context mContext, String[] mSelectPhones) {

		SmsRecordDao smsRecordDao = SmsRecordDaoUtils.getSmsRecordDao(mContext);
		SQLiteDatabase smsRecordDatabase = smsRecordDao.getDatabase();
		ContentResolver resolver = mContext.getContentResolver();

		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];
			Cursor smsCursor = resolver.query(CONVERSATIONS_URI, PROJECTION,
					"address=?", new String[] { phone }, null);

			if (smsCursor != null && smsCursor.getCount() > 0) {
				while (smsCursor.moveToNext()) {

					String phone_number = smsCursor
							.getString(ADDRESS_COLUMN_INDEX);

					Tools.logSh("--------" + 3);
					SmsRecord smsRecord = new SmsRecord();

					// String idStr = smsCursor.getString(ID_COLUMN_INDEX);
					String smsBody = smsCursor.getString(SNIPPET_COLUMN_INDEX);
					int count = smsCursor.getInt(MSG_COUNT_COLUMN_INDEX);

					long smsDate = smsCursor.getLong(DATE_COLUMN_INDEX);
					// 判断该联系人SmsRecord是否存在于我们数据库
					Cursor smsRecordCursor = smsRecordDatabase.query(
							SmsRecordDao.TABLENAME, null,
							SmsRecordDao.Properties.Phone_number.columnName
									+ "=?", new String[] { phone }, null, null,
							null);

					if (smsRecordCursor != null
							&& smsRecordCursor.getCount() > 0) {// 已经存在该号码

						smsRecord.setPhone_number(phone);
						smsRecord
								.setCount(smsRecordCursor.getInt(smsRecordCursor
										.getColumnIndex(SmsRecordDao.Properties.Count.columnName)) + 1);
						smsRecord
								.setId(smsRecordCursor.getLong(smsRecordCursor
										.getColumnIndex(SmsRecordDao.Properties.Id.columnName)));
						smsRecord.setLasted_contact(smsDate);
						smsRecord.setLasted_data(smsBody);
						// 向我们的数据库跟新SmsRecord
						smsRecordDao.update(smsRecord);
						Tools.logSh("向我们smsRecord数据库中跟新了smsRecord");
						Tools.logSh("短信record" + "address" + phone);

					} else {// 还不存在该号码
						Tools.logSh("" + smsCursor.getCount());

						smsRecord.setPhone_number(phone_number);
						smsRecord.setCount(count);
						smsRecord.setLasted_contact(smsDate);
						smsRecord.setLasted_data(smsBody);

						// 添加到我们数据库
						smsRecordDao.insert(smsRecord);
						smsRecord = null;
						Tools.logSh("向smsRecord插入了一条数据");
						break;

					}
				}
				smsCursor.close();
			}
		}
	}
}
