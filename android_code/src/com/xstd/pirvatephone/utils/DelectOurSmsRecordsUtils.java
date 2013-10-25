package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class DelectOurSmsRecordsUtils {
	
	public static void deleteSmsRecords(Context mContext,String[] mPhoneNumbers){
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(mContext);
		SQLiteDatabase smsRecordDatabase = smsRecordDao.getDatabase();

		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			int isDelete = smsRecordDatabase.delete(SmsRecordDao.TABLENAME,
					SmsRecordDao.Properties.Phone_number.columnName + "=?",
					new String[] { number });

			if (isDelete == 0) {
				Tools.logSh("删除record短信失败！");
			} else {
				Tools.logSh("删除record短信成功！");
			}
		}
	}
}
