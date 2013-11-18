package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;

import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WritePhoneRecordUtils {
	
	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	
	public static String getSystemContactName(Context mContext,String number){
		
		String name = "";
		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, Phone.NUMBER+"=?", new String[]{number}, null);
		
		if(phoneCursor!=null && phoneCursor.getCount()>0){
			while(phoneCursor.moveToNext()){
				// 得到联系人名称
				name = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

			}
		}
		
		return name;
	}
	

	public static void writePhoneRecord(Context mContext, String[] mSelectPhones) {

		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(mContext);
		SQLiteDatabase phoneDatabase = phoneRecordDao.getDatabase();

		ContentResolver resolver = mContext.getContentResolver();

		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];

			Tools.logSh("phone=====" + phone);
			Cursor phoneCursor = resolver.query(CallLog.Calls.CONTENT_URI,
					null, CallLog.Calls.NUMBER + "=?", new String[] { phone },
					null);
			Tools.logSh("phoneRecordCursor=====" + phoneCursor.getCount());
			if (phoneCursor != null && phoneCursor.getCount()>0) {
				while (phoneCursor.moveToNext()) {
					PhoneRecord mPhoneRecord = new PhoneRecord();
					// 得到手机号码
					String number = phoneCursor.getString(phoneCursor
							.getColumnIndex(CallLog.Calls.NUMBER));
					// 得到时间
					Long date = phoneCursor.getLong(phoneCursor
							.getColumnIndex(CallLog.Calls.DATE));
					// 通话类型
					int type = phoneCursor.getInt(phoneCursor
							.getColumnIndex(CallLog.Calls.TYPE));
					
					long duration = phoneCursor.getLong(phoneCursor.getColumnIndex(CallLog.Calls.DURATION));
					
					//name
					
					//对该姓名进行处理---替换成我们数据库的新姓名。
					String name = ContactUtils.queryContactName(mContext, phone);
					
					// 判断该联系人PhoneRecord是否存在于我们数据库
					Cursor phoneRecordCursor = phoneDatabase.query(
							PhoneRecordDao.TABLENAME, null,
							PhoneRecordDao.Properties.Phone_number.columnName
									+ "=?", new String[] { phone }, null, null,
							null);
					if (phoneRecordCursor != null
							&& phoneRecordCursor.getCount() > 0) {// 已经存在该号码
						while(phoneRecordCursor.moveToNext()){
							// _id
							mPhoneRecord
									.setId(phoneRecordCursor.getLong(phoneCursor
											.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
							// number
							mPhoneRecord.setPhone_number(phone);
							// Contact_times
							mPhoneRecord
									.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
											.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);
							// date
							mPhoneRecord.setDate(date);
							mPhoneRecord.setName(name);
							mPhoneRecord.setType(type);
							mPhoneRecord.setDuration(duration);
							// 向我们的数据库跟新SmsRecord
							phoneRecordDao.update(mPhoneRecord);
							Tools.logSh(number + "::" + date + "::" + type + "::"
									+ number);

							Tools.logSh("向我们phoneRecord数据库中跟新了smsRecord");
							phoneRecordCursor.close();
							break;
						}
					} else {// 还不存在该号码
						Tools.logSh(number + "::" + date + "::" + type + "::"
								+ number);

						mPhoneRecord.setName(name);
						mPhoneRecord.setDate(date);

						Cursor query = resolver.query(
								CallLog.Calls.CONTENT_URI, null,
								CallLog.Calls.NUMBER + "=?",
								new String[] { phone }, null);
						int count = query.getCount();

						Tools.logSh(number + "::" + date + "::" + type + "::"
								+ number + "count==" + count);

						mPhoneRecord.setContact_times(count);
						mPhoneRecord.setPhone_number(number);
						mPhoneRecord.setDuration(duration);
						// 添加到我们数据库
						phoneRecordDao.insert(mPhoneRecord);
						mPhoneRecord = null;
						Tools.logSh("向phoneRecord插入了一条数据");
						// 取第一行数据
						phoneRecordCursor.close();
						break;

					}
				}
				phoneCursor.close();
			}
		}
	}
}
