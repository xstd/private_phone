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

	public static ArrayList<String> queryIntereptNumber(Context context) {

		ArrayList<String> intereptNumbers = null;
		intereptNumbers = new ArrayList<String>();
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
			contactQuery.close();
		}
		return intereptNumbers;
	}

	public static ArrayList<String> queryNotIntereptNumber(Context context) {
		ArrayList<String> notIntereptNumbers = null;
		notIntereptNumbers = new ArrayList<String>();
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
			contactQuery.close();
		}
		return notIntereptNumbers;
	}

	public static void modelChangeContact(Context context) {
		Tools.logSh("modelChangeContact被调用了");

		ContextModelUtils contextModelUtils = new ContextModelUtils();
		// 1.获取当前拦情景模式下拦截与不拦截的号码
		String[] intereptNumbers = contextModelUtils.getIntereptNumber(context,
				null);
		String[] notIntereptNumbers = contextModelUtils.getNotIntereptNumber(
				context, null);
		Tools.logSh("intereptNumbers======" + intereptNumbers);
		Tools.logSh("notIntereptNumbers======" + notIntereptNumbers);
		if (intereptNumbers != null && intereptNumbers.length > 0) {
			// 2.查看隐私通信中该号码的拦截情况
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
						// 若隐私联系人中该号码不是被拦截的，修改其模式为拦截
						Tools.logSh("number==="+number+"   type=="+numbertype);
						
						if (numbertype != 1) {
							contactInfo.setType(1);
							Tools.logSh(number + "模式被更改====" + 1);
							contactInfoDao.update(contactInfo);
						}
					}
					contactQuery.close();
				}
			}
		}

		if (notIntereptNumbers != null && notIntereptNumbers.length > 0) {
			// 1.查看隐私通信中该号码的拦截情况
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
						Tools.logSh("number==="+number+"   type=="+numbertype);
						
						if (numbertype != 0) {
							contactInfo.setType(0);
							Tools.logSh(number + "模式被更改====" + 0);
							contactInfoDao.update(contactInfo);
						}
					}
					contactQuery.close();
				}
			}
		}
	}
}
