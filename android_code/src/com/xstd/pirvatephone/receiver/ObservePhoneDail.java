package com.xstd.pirvatephone.receiver;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class ObservePhoneDail extends ContentObserver {

	private Context mContext;
	private boolean flags = true;

	public ObservePhoneDail(Handler handler, Context context) {
		super(handler);
		this.mContext = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		Tools.logSh("观察看通话记录数据库变化");

		Cursor cursor = this.mContext.getContentResolver().query(
				Uri.parse("content://call_log/calls"), null, null, null,
				"date desc limit 1");

		// 查询数据库
		while (cursor.moveToNext()) {
			String address = cursor.getString(cursor.getColumnIndex("number"));

			// String name = cursor.getString(cursor.getColumnIndex("name"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			Long date = cursor.getLong(cursor.getColumnIndex("date"));
			Long duration = cursor.getLong(cursor.getColumnIndex("duration"));

			Tools.logSh("address=" + address + "::" + "type=" + type + ":::"
					+ "date=" + date + ":::" + "duration=" + duration);

			// 判断该号码是否是隐私联系人
			ContactInfoDao contactInfoDao = ContactInfoDaoUtils
					.getContactInfoDao(mContext);
			SQLiteDatabase contactInfoDaoDatabase = contactInfoDao
					.getDatabase();
			Cursor contactInfoCursor = contactInfoDaoDatabase.query(
					ContactInfoDao.TABLENAME, null,
					ContactInfoDao.Properties.Phone_number.columnName + "=?",
					new String[] { address }, null, null, null);
			if (contactInfoCursor != null && contactInfoCursor.getCount() > 0) {// 确是隐私联系人
				Tools.logSh("发现隐私联系人");
				PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
						.getPhoneRecordDao(mContext);
				SQLiteDatabase phoneRecordDatabase = phoneRecordDao
						.getDatabase();
				Cursor phoneRecordCursor = phoneRecordDatabase.query(
						PhoneRecordDao.TABLENAME, null,
						PhoneRecordDao.Properties.Phone_number.columnName
								+ "=?", new String[] { address }, null, null,
						null);
				// 如果已有该号码的通话记录
				if (phoneRecordCursor != null
						&& phoneRecordCursor.getCount() > 0) {
					while (phoneRecordCursor.moveToNext()) {
					Tools.logSh("以后该号码的通话记录");
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord
							.setId(phoneRecordCursor.getLong(phoneRecordCursor
									.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
					phoneRecord
							.setPhone_number(address);
					phoneRecord
							.setName(phoneRecordCursor.getString(phoneRecordCursor
									.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
					phoneRecord.setDate(date);
					phoneRecord.setType(type);
					phoneRecord
							.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
									.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);

					phoneRecordDao.update(phoneRecord);

					PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
							.getPhoneDetailDao(mContext);
					PhoneDetail phoneDetail = new PhoneDetail();
					phoneDetail.setPhone_number(address);
					phoneDetail.setName(phoneRecordCursor.getString(phoneRecordCursor
							.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
					phoneDetail.setDate(date);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(type);
					
					phoneDetailDao.insert(phoneDetail);
					break ;
					}
				} else {// 如果还没有该号码的通话记录
					Tools.logSh("暂无该号码的通话记录");
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord.setPhone_number(address);

					phoneRecord.setDate(System.currentTimeMillis());
					phoneRecord.setType(2);
					phoneRecord.setContact_times(1);

					PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
							.getPhoneDetailDao(mContext);
					PhoneDetail phoneDetail = new PhoneDetail();
					phoneDetail.setPhone_number(address);
					phoneDetail.setDate(date);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(type);

					ContactInfoDao contactInfoDao2 = ContactInfoDaoUtils
							.getContactInfoDao(mContext);
					SQLiteDatabase database = contactInfoDao2.getDatabase();
					Cursor query = database.query(ContactInfoDao.TABLENAME,
							null,
							ContactInfoDao.Properties.Phone_number.columnName
									+ "=?", new String[] { address }, null,
							null, null);
					if (query != null && query.getCount() > 0) {
						while (query.moveToNext()) {
							String name = query
									.getString(query
											.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
							phoneDetail.setName(name);
							phoneRecord.setName(name);
						}
						phoneRecordDao.insert(phoneRecord);
						phoneDetailDao.insert(phoneDetail);
					}
				}
				
				mContext.getContentResolver().delete(
						Uri.parse("content://call_log/calls"), "date=?",
						new String[] { date.toString() });
				
				break ;

			}
			cursor.close();
			break;
		}
	}
}
/*
ContactInfoDao contactInfoDao2 = ContactInfoDaoUtils
		.getContactInfoDao(mContext);
SQLiteDatabase database = contactInfoDao2.getDatabase();
Cursor query = database.query(ContactInfoDao.TABLENAME,
		null,
		ContactInfoDao.Properties.Phone_number.columnName
				+ "=?", new String[] { address }, null,
		null, null);
if (query != null && query.getCount() > 0) {
	while (query.moveToNext()) {
		String name = query
				.getString(query
						.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
		phoneDetail.setName(name);
	}
}*/