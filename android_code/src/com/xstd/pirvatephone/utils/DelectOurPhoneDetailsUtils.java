package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class DelectOurPhoneDetailsUtils {
	private Context mContext;
	private String[] mPhoneNumbers;
	
	public DelectOurPhoneDetailsUtils(Context context, String[] phoneNumbers){
		this.mContext = context;
		this.mPhoneNumbers = phoneNumbers;
	}
	
	public void deletePhoneDetails(){
		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
				.getPhoneDetailDao(mContext);
		SQLiteDatabase phoneDetailDatebase = phoneDetailDao.getDatabase();
		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			int isDelete = phoneDetailDatebase.delete(PhoneDetailDao.TABLENAME,
					PhoneDetailDao.Properties.Phone_number.columnName + "=?",
					new String[] { number });

			if (isDelete == 0) {
				Tools.logSh("删除通话记录detail失败！");
			} else {
				Tools.logSh("删除联系人通话记录detail成功！");
			}
		}
	}
}
