package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class DelectOurContactUtils {
	private Context mContext;
	private String[] mPhoneNumbers;
	
	public DelectOurContactUtils(Context context, String[] phoneNumbers){
		this.mContext = context;
		this.mPhoneNumbers = phoneNumbers;
	}
	
	public void deleteContacts(){
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String number = mPhoneNumbers[i];
			int isDelete = contactDatabase.delete(ContactInfoDao.TABLENAME,
					ContactInfoDao.Properties.Phone_number.columnName + "=?",
					new String[] { number });

			if (isDelete == 0) {
				Tools.logSh("删除联系人失败！");
			} else {
				Tools.logSh("删除联系人成功！");
			}
		}
	}
}
