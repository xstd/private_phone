package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class DelectOurPhoneRecordsUtils {
	private Context mContext;
	private String[] mPhoneNumbers;
	
	public DelectOurPhoneRecordsUtils(Context context, String[] phoneNumbers){
		this.mContext = context;
		this.mPhoneNumbers = phoneNumbers;
	}
	
	public void deletePhoneRecords(){
		// 通话记record录移除
		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(mContext);
		SQLiteDatabase phoneRecordDatebase = phoneRecordDao.getDatabase();
		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			int isDelete = phoneRecordDatebase.delete(PhoneRecordDao.TABLENAME,
					PhoneRecordDao.Properties.Phone_number.columnName + "=?",
					new String[] { number });

			if (isDelete == 0) {
				Tools.logSh("删除通话记录record失败！");
			} else {
				Tools.logSh("删除通话记录record成功！");
			}
		}
	}
}
