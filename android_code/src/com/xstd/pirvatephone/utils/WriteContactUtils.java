package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteContactUtils {

	private Context mContext;

	public WriteContactUtils(Context context) {
		this.mContext = context;
	}

	/**
	 * 获取未重复的号码以及已经存在于我们数据库的隐私联系人号码
	 */
	public ArrayList<String> removeRepeat(ArrayList<String> mSelectPhones) {

		ArrayList<String> mNoRepeatPhones = new ArrayList<String>();
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		// 去除重复
		for (int i = 0; i < mSelectPhones.size(); i++) {
			if (!mNoRepeatPhones.contains(mSelectPhones.get(i))) {
				mNoRepeatPhones.add(mSelectPhones.get(i));
			}
		}

		// 该号码已经存在于隐私联系人中,则不再添加
		for (int i = 0; i < mSelectPhones.size(); i++) {
			String num = mSelectPhones.get(i);
			Cursor cursor = contactDatabase.query(ContactInfoDao.TABLENAME,
					null, ContactInfoDao.Properties.Phone_number.columnName
							+ "=?", new String[] { num }, null, null, null);

			if (cursor != null && cursor.getCount() > 0) {
				mNoRepeatPhones.remove(num);
			}
		}

		return mNoRepeatPhones;
	}

	/**
	 * 若仅仅移动一个，且存在于私密联系人
	 * 
	 * @return
	 */
	public boolean isPrivateContact(String[] mSelectPhones) {

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		String number = mSelectPhones[0];
		Cursor cursor = contactDatabase.query(ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public boolean isMoreContact(String number) {
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				new String[] { Phone.DISPLAY_NAME }, Phone.NUMBER + "=?",
				new String[] { number }, null);
		if (phoneCursor != null && phoneCursor.getCount() > 1) {
			return true;
		}

		return false;
	}

	public void writeContactBySmsRecord(String[] mSelectPhones) {
		writeContactBySmsRecord(ArrayUtils.arrayToList(mSelectPhones));
	}

	public void writeContactByPhoneRecord(String[] mSelectPhones) {
		writeContactByPhoneRecord(ArrayUtils.arrayToList(mSelectPhones));
	}

	public void writeContact(String[] mSelectPhones) {
		writeContact(ArrayUtils.arrayToList(mSelectPhones));
	}

	public void writeContactBySmsRecord(ArrayList<String> mSelectPhones) {
		if (mSelectPhones != null && mSelectPhones.size() > 0) {
			mSelectPhones = removeRepeat(mSelectPhones);
			ContactInfoDao contactInfoDao = ContactInfoDaoUtils
					.getContactInfoDao(mContext);

			ContentResolver resolver = mContext.getContentResolver();

			for (int i = 0; i < mSelectPhones.size(); i++) {
				String num = mSelectPhones.get(i);

				// 判断该号码对应的联系人的个数，如果大于1，说明多个联系人号码相同
				boolean moreContact = isMoreContact(num);
				if (moreContact) {
					writeContact(new String[] { num });
				} else {

					// 获取手机短信记录
					Cursor detailCursor = resolver.query(
							Uri.parse("content://sms/"), null, "address=?",
							new String[] { num }, null);

					if (detailCursor != null) {
						while (detailCursor.moveToNext()) {
							ContactInfo contactInfo = new ContactInfo();

							// 得到手机号码
							String number = detailCursor.getString(detailCursor
									.getColumnIndex("address"));

							// 查询联系人姓名---是否存在该联系人
							String contactName = ContactsUtils
									.queryContactName(mContext,
											Long.valueOf(number));

							if (TextUtils.isEmpty(contactName)) {
								contactName = number;
							}

							Tools.logSh("name==" + contactName + "    number=="
									+ number);
							contactInfo.setPhone_number(number);
							contactInfo.setDisplay_name(contactName);
							contactInfo.setType(0);// 0不拦截
							// 添加到我们数据库
							contactInfoDao.insert(contactInfo);
							Tools.logSh("phoneCursor插入了一条数据");

							detailCursor.close();
							break;
						}
					}
				}
			}
		}
	}

	public void writeContactByPhoneRecord(ArrayList<String> mSelectPhones) {
		if (mSelectPhones != null && mSelectPhones.size() > 0) {
			mSelectPhones = removeRepeat(mSelectPhones);
			ContactInfoDao contactInfoDao = ContactInfoDaoUtils
					.getContactInfoDao(mContext);

			ContentResolver resolver = mContext.getContentResolver();

			for (int i = 0; i < mSelectPhones.size(); i++) {
				String num = mSelectPhones.get(i);

				// 判断该号码对应的联系人的个数，如果大于1，说明多个联系人号码相同
				boolean moreContact = isMoreContact(num);
				if (moreContact) {
					writeContact(new String[] { num });
				} else {

					// 获取手机通话记录
					Cursor phoneCursor = resolver.query(
							CallLog.Calls.CONTENT_URI, null,
							CallLog.Calls.NUMBER + "=?", new String[] { num },
							null);

					if (phoneCursor != null) {
						while (phoneCursor.moveToNext()) {
							ContactInfo contactInfo = new ContactInfo();
							// 得到联系人名称
							String contactName = phoneCursor
									.getString(phoneCursor
											.getColumnIndex("name"));

							// 得到手机号码
							String number = phoneCursor.getString(phoneCursor
									.getColumnIndex("number"));

							if (TextUtils.isEmpty(contactName)) {
								contactName = number;
							}

							Tools.logSh("name==" + contactName + "    number=="
									+ number);
							contactInfo.setPhone_number(number);
							contactInfo.setDisplay_name(contactName);
							contactInfo.setType(0);// 0不拦截
							// 添加到我们数据库
							contactInfoDao.insert(contactInfo);
							Tools.logSh("phoneCursor插入了一条数据");

							phoneCursor.close();
							break;
						}
					}
				}
			}
		}
	}

	public void writeContact(ArrayList<String> mSelectPhones) {
		if (mSelectPhones != null && mSelectPhones.size() > 0) {
			mSelectPhones = removeRepeat(mSelectPhones);
			ContactInfoDao contactInfoDao = ContactInfoDaoUtils
					.getContactInfoDao(mContext);
			SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

			ContentResolver resolver = mContext.getContentResolver();

			for (int i = 0; i < mSelectPhones.size(); i++) {
				String num = mSelectPhones.get(i);
				// 获取手机联系人
				Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
						new String[] { Phone.CONTACT_ID, Phone.DISPLAY_NAME,
								Phone.NUMBER }, Phone.NUMBER + "=?",
						new String[] { num }, null);

				if (phoneCursor != null) {
					while (phoneCursor.moveToNext()) {
						ContactInfo contactInfo = new ContactInfo();
						// 得到联系人名称
						String contactName = phoneCursor.getString(1);
						// 判断该联系人（号码）是否已存在
						Cursor query = contactDatabase
								.query(ContactInfoDao.TABLENAME,
										null,
										ContactInfoDao.Properties.Phone_number.columnName
												+ "=?", new String[] { num },
										null, null, null);
						if (query != null && query.getCount() > 0) {
							Tools.logSh("该联系人已经存在");
							break;
						} else {
							String number = phoneCursor.getString(2);

							contactInfo.setPhone_number(number);
							contactInfo.setDisplay_name(contactName);
							contactInfo.setType(0);// 0不拦截
							// 添加到我们数据库
							contactInfoDao.insert(contactInfo);
							Tools.logSh("phoneCursor插入了一条数据");
						}
					}
					phoneCursor.close();
				}
			}
		}
	}
}
