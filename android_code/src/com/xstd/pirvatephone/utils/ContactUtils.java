package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactUtils {

	private ArrayList<String> intereptNumbers = new ArrayList<String>();
	private ArrayList<String> notIntereptNumbers = new ArrayList<String>();

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

	public ArrayList<String> queryIntereptNumber(Context context) {
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		Cursor contactQuery = contactDatabase.query(ContactInfoDao.TABLENAME,
				null, null, null, null, null, null);

		if (contactQuery != null && contactQuery.getCount() > 0) {
			while (contactQuery.moveToNext()) {
				String number = contactQuery
						.getString(contactQuery
								.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
				int numbertype = contactQuery
						.getInt(contactQuery
								.getColumnIndex(ContactInfoDao.Properties.Type.columnName));

				if (numbertype == 1) {

					intereptNumbers.add(number);
				}
			}
		}
		return intereptNumbers;
	}

	public ArrayList<String> queryNotIntereptNumber(Context context) {
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		Cursor contactQuery = contactDatabase.query(ContactInfoDao.TABLENAME,
				null, null, null, null, null, null);

		if (contactQuery != null && contactQuery.getCount() > 0) {
			while (contactQuery.moveToNext()) {
				String number = contactQuery
						.getString(contactQuery
								.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
				int numbertype = contactQuery
						.getInt(contactQuery
								.getColumnIndex(ContactInfoDao.Properties.Type.columnName));

				if (numbertype == 0) {
					notIntereptNumbers.add(number);
				}

			}
		}
		return notIntereptNumbers;
	}

	public static void modelChangeContact(Context context) {
		Tools.logSh("modelChangeContact被调用了");
		
		ContextModelUtils contextModelUtils = new ContextModelUtils();
		String[] intereptNumbers = contextModelUtils.getIntereptNumber(context,
				null);
		String[] notIntereptNumbers = contextModelUtils.getNotIntereptNumber(
				context, null);
		Tools.logSh("intereptNumbers======"+intereptNumbers);
		Tools.logSh("notIntereptNumbers======"+notIntereptNumbers);
		if (intereptNumbers == null || intereptNumbers.length == 0) {

		} else {
			// 如果以前拦截的号码中有这个号码---设置为
			for (int j = 0; j < intereptNumbers.length; j++) {
				Tools.logSh("intereptNumbers[j]=======" + intereptNumbers[j]);

				ContactInfoDao contactInfoDao = ContactInfoDaoUtils
						.getContactInfoDao(context);
				SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
				Cursor contactQuery = contactDatabase.query(
						ContactInfoDao.TABLENAME, null,
						ContactInfoDao.Properties.Phone_number.columnName
								+ "=?", new String[] { intereptNumbers[j] },
						null, null, null);

				if (contactQuery != null && contactQuery.getCount() > 0) {
					while (contactQuery.moveToNext()) {
						ContactInfo contactInfo = new ContactInfo();

						long id = contactQuery
								.getLong(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Id.columnName));
						String number = contactQuery
								.getString(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
						String name = contactQuery
								.getString(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
						int numbertype = contactQuery
								.getInt(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Type.columnName));
						contactInfo.setId(id);
						contactInfo.setPhone_number(number);
						contactInfo.setDisplay_name(name);
						if (numbertype == 0) {
							contactInfo.setType(1);
						}
						
						Tools.logSh(number+"模式被更改===="+1);
						contactInfoDao.update(contactInfo);
					}
					contactQuery.close();
				}
			}
		}

		if (notIntereptNumbers == null || notIntereptNumbers.length == 0) {

		} else {
			// 如果以前拦截的号码中有这个号码---设置为
			for (int j = 0; j < notIntereptNumbers.length; j++) {
				Tools.logSh("notIntereptNumbers[j]======="
						+ notIntereptNumbers[j]);

				ContactInfoDao contactInfoDao = ContactInfoDaoUtils
						.getContactInfoDao(context);
				SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
				Cursor contactQuery = contactDatabase.query(
						ContactInfoDao.TABLENAME, null,
						ContactInfoDao.Properties.Phone_number.columnName
								+ "=?", new String[] { notIntereptNumbers[j] },
						null, null, null);

				if (contactQuery != null && contactQuery.getCount() > 0) {
					while (contactQuery.moveToNext()) {
						ContactInfo contactInfo = new ContactInfo();

						long id = contactQuery
								.getLong(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Id.columnName));
						String number = contactQuery
								.getString(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
						String name = contactQuery
								.getString(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
						int numbertype = contactQuery
								.getInt(contactQuery
										.getColumnIndex(ContactInfoDao.Properties.Type.columnName));
						contactInfo.setId(id);
						contactInfo.setPhone_number(number);
						contactInfo.setDisplay_name(name);
						if (numbertype == 1) {
							contactInfo.setType(0);
						}
						Tools.logSh(number+"模式被更改===="+0);
						contactInfoDao.update(contactInfo);
					}
					contactQuery.close();
				}
			}
		}
	}
}
