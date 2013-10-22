package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WritePhoneRecordUtils {
	
	public static void writePhoneRecord(Context mContext,String[] mSelectPhones){
		
		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(mContext);
		ContentResolver resolver = mContext.getContentResolver();
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];

			Tools.logSh("phone=====" + phone);
			Cursor phoneRecordCursor = resolver.query(
					CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER
							+ "=?", new String[] { phone }, null);
			Tools.logSh("phoneRecordCursor=====" + phoneRecordCursor.getCount());
			if (phoneRecordCursor != null) {
				while (phoneRecordCursor.moveToNext()) {
					PhoneRecord mPhoneRecord = new PhoneRecord();
					// 得到手机号码
					String number = phoneRecordCursor
							.getString(phoneRecordCursor
									.getColumnIndex(CallLog.Calls.NUMBER));
					// 得到时间
					Long date = phoneRecordCursor.getLong(phoneRecordCursor
							.getColumnIndex(CallLog.Calls.DATE));
					// 通话类型
					int type = phoneRecordCursor.getInt(phoneRecordCursor
							.getColumnIndex(CallLog.Calls.TYPE));
					// 通化人姓名
					String name = phoneRecordCursor.getString(phoneRecordCursor
							.getColumnIndex("name"));

					if (name == null) {
						name = number;
					}

					Tools.logSh(number + "::" + date + "::" + type + "::"
							+ name);

					mPhoneRecord.setName(name);
					mPhoneRecord.setDate(date);

					Cursor query = resolver.query(CallLog.Calls.CONTENT_URI,
							null, CallLog.Calls.NUMBER + "=?",
							new String[] { phone }, null);
					int count = query.getCount();

					Tools.logSh(number + "::" + date + "::" + type + "::"
							+ name + "count==" + count);

					mPhoneRecord.setContact_times(count);
					mPhoneRecord.setPhone_number(number);

					// 添加到我们数据库
					phoneRecordDao.insert(mPhoneRecord);
					mPhoneRecord = null;
					Tools.logSh("向phoneRecord插入了一条数据");
					// 取第一行数据
					phoneRecordCursor.close();
					break;
				}
			}
		}

	}
}
