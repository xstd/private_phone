package com.xstd.pirvatephone.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class RestoreSystemContactUtils {
	private Context mContext;
	private String[] mPhoneNumbers;
	
	public RestoreSystemContactUtils(Context context, String[] phoneNumbers){
		this.mContext = context;
		this.mPhoneNumbers = phoneNumbers;
	}
	
	public void restoreContacts(){
		// 向系统联系人数据库添加联系人

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		for (int i = 0; i < mPhoneNumbers.length; i++) {

			String phone = mPhoneNumbers[i];

			// 查询我们的数据库获取number对应的名字
			Cursor cursor = contactDatabase.query(ContactInfoDao.TABLENAME,
					null, ContactInfoDao.Properties.Phone_number.columnName
							+ "=?", new String[] { phone }, null, null, null);

			Tools.logSh("向系统数据库中插入数据cursor.count=" + cursor.getCount()
					+ "::::+phone=" + phone);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String name = cursor
							.getString(cursor
									.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
					Tools.logSh("向系统数据库中插入数据name====" + name);

					ContentValues values = new ContentValues();

					values.put(People.NAME, name);

					Uri uri = mContext.getContentResolver().insert(People.CONTENT_URI,
							values);

					Uri numberUri = Uri.withAppendedPath(uri,
							People.Phones.CONTENT_DIRECTORY);

					values.clear();

					values.put(Contacts.Phones.TYPE, People.Phones.TYPE_MOBILE);

					values.put(People.NUMBER, phone);

					mContext.getContentResolver().insert(numberUri, values);
					Tools.logSh("成功的向系统联系人数据库插入一个联系人name+" + name + "phone="
							+ phone);
				}
				cursor.close();
			}
		}
	}
}
