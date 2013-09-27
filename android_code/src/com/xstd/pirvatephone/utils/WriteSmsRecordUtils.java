package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteSmsRecordUtils {
	private Context mContext;
	private String[] mSelectPhones;
	private SmsRecord mSmsRecord;
	
	private static final int ID_COLUMN_INDEX = 0;
	private static final int SNIPPET_COLUMN_INDEX = 1;
	private static final int MSG_COUNT_COLUMN_INDEX = 2;
	private static final int ADDRESS_COLUMN_INDEX = 3;
	private static final int DATE_COLUMN_INDEX = 4;
	
	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");
	
	// 查询的字段
		private String[] PROJECTION = new String[] { "sms.thread_id AS _id",
				"sms.body AS snippet", "groups.msg_count AS msg_count",
				" sms.address AS address", " sms.date AS date" };

	
	public WriteSmsRecordUtils(Context context, String[] selectPhones){
		this.mContext = context;
		this.mSelectPhones = selectPhones;
	}
	
	public void writeSmsRecord(){
		
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(mContext);
		ContentResolver resolver = mContext.getContentResolver();
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];
			Cursor smsCursor = resolver.query(CONVERSATIONS_URI, PROJECTION,
					"address=?", new String[] { phone }, null);

			Tools.logSh("" + smsCursor.getCount());
			if (smsCursor != null) {
				while (smsCursor.moveToNext()) {
					Tools.logSh("--------" + 3);
					mSmsRecord = new SmsRecord();
					
					// String idStr = smsCursor.getString(ID_COLUMN_INDEX);
					String body = smsCursor.getString(SNIPPET_COLUMN_INDEX);
					int count = smsCursor.getInt(MSG_COUNT_COLUMN_INDEX);
					String phone_number = smsCursor
							.getString(ADDRESS_COLUMN_INDEX);
					long date = smsCursor.getLong(DATE_COLUMN_INDEX);

					mSmsRecord.setPhone_number(phone_number);
					mSmsRecord.setCount(count);
					mSmsRecord.setLasted_contact(date);
					mSmsRecord.setLasted_data(body);

					// 添加到我们数据库
					smsRecordDao.insert(mSmsRecord);
					mSmsRecord = null;
					Tools.logSh("向smsRecord插入了一条数据");

				}
				smsCursor.close();
			}
		}

	}
}
