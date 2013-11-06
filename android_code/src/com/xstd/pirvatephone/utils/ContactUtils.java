package com.xstd.pirvatephone.utils;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactUtils {
	public static String queryContactNumber(Context context, String name) {
		String phone = "";
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase contactInDatabase = contactInfoDao.getDatabase();
		Cursor query = contactInDatabase.query(ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Display_name.columnName + "=?",
				new String[] { name }, null, null, null);
		if (query != null && query.getCount() > 0) {
			while (query.moveToNext()) {
				phone = query
						.getString(query
								.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
				query.close();
				return phone;
			}

		}
		return phone;
	}

	public static String queryContactName(Context context, String number) {
		String name = "";
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase contactInfoDatabase = contactInfoDao.getDatabase();
		Cursor contactQuery = contactInfoDatabase.query(
				ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, null);

		if (contactQuery != null && contactQuery.getCount() > 0) {
			while (contactQuery.moveToNext()) {
				name = contactQuery
						.getString(contactQuery
								.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
				contactQuery.close();
				return name;
			}
		}
		return number;
	}
}
