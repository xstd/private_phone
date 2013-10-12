package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class DelectOurSmsDetailsUtils {
	private Context mContext;
	private String[] mPhoneNumbers;
	
	public DelectOurSmsDetailsUtils(Context context, String[] phoneNumbers){
		this.mContext = context;
		this.mPhoneNumbers = phoneNumbers;
	}
	
	public void deleteSmsDetails(){
		SmsDetailDao smsDetailDao = SmsDetailDaoUtils
				.getSmsDetailDao(mContext);
		SQLiteDatabase smsDetailDatabase = smsDetailDao.getDatabase();


		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			int isDelete = smsDetailDatabase.delete(SmsDetailDao.TABLENAME,
					SmsDetailDao.Properties.Phone_number.columnName + "=?",
					new String[] { number });

			if (isDelete == 0) {
				Tools.logSh("删除detail短信失败！");
			} else {
				Tools.logSh("删除detail短信成功！");
			}
		}
	}
}
