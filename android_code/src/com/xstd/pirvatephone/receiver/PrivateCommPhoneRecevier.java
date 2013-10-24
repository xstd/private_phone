package com.xstd.pirvatephone.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class PrivateCommPhoneRecevier extends BroadcastReceiver {

	private boolean isPrivateContact;
	private boolean flags;

	@Override
	public void onReceive(Context context, Intent intent) {
		Tools.logSh(intent.getAction());

		Tools.logSh("接收到拨入电话广播");

		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Tools.logSh(state);

		String number = intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if (number.contains("+86")) {
			number = number.substring(2);
			Tools.logSh("号码：" + number);
		}

		Tools.logSh(state + ":::" + number);

		if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
			flags = true;
			// 电话正在响铃
			Tools.logSh("EXTRA_STATE_RINGING");
		}
		Tools.logSh("EXTRA_STATE_IDLE::" + flags);
		flags = false;
		// 第二步:确认该短信号码是否满足过滤条件（在隐私联系人中）
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		// 获取通话记录表中所有的隐私通讯的电话号码
		Cursor recordCursor = contactDatabase.query(ContactInfoDao.TABLENAME,
				null, null, null, null, null, null);

		if (recordCursor != null && recordCursor.getCount() > 0) {
			while (recordCursor.moveToNext()) {
				String privatePhone = recordCursor
						.getString(recordCursor
								.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
				if (number.equals(privatePhone)) {// 屏蔽发来的短信
					Tools.logSh("接收到隐私联系人的电话" + number);
					isPrivateContact = true;

				}
			}
		}

		if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE) && flags
				&& isPrivateContact) {
			Tools.logSh("接收到隐私来电而且通话结束了");
			removePhoneRecord(context, number);
		}

	}

	private void removePhoneRecord(Context context, String number) {
		// 查询通话记录
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneDetailCursor = resolver.query(CallLog.Calls.CONTENT_URI,
				null, CallLog.Calls.NUMBER + "=?", new String[] { number },
				"date desc limit 1");
		
		if (phoneDetailCursor != null && phoneDetailCursor.getCount()>0) {
			while (phoneDetailCursor.moveToNext()) {
				// 得到手机号码
				String address = phoneDetailCursor.getString(phoneDetailCursor
						.getColumnIndex("number"));
				// 得到开始时间
				Long date = phoneDetailCursor.getLong(phoneDetailCursor
						.getColumnIndex("date"));
				// 通话持续时间

				Long duration = phoneDetailCursor.getLong(phoneDetailCursor
						.getColumnIndex("duration"));
				// 通话类型
				int type = phoneDetailCursor.getInt(phoneDetailCursor
						.getColumnIndex("type"));
				// 通化人姓名
				String name = phoneDetailCursor.getString(phoneDetailCursor
						.getColumnIndex("name"));

				Tools.logSh(number + "::" + date + "::" + duration + "::"
						+ type + "::" + name);

				// 将此通话记录插入到我们的数据库中，先判断记录中是否有这个号码的记录
				PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
						.getPhoneRecordDao(context);
				SQLiteDatabase phoneRecordDatabase = phoneRecordDao
						.getDatabase();
				Cursor phoneRecordCursor = phoneRecordDatabase.query(
						PhoneRecordDao.TABLENAME, null,
						PhoneRecordDao.Properties.Phone_number.columnName
								+ "=?", new String[] { number }, null, null,
						null);
				if (phoneRecordCursor != null
						&& phoneRecordCursor.getCount() > 0) {// 通话记录中已经有此号码的记录，跟新

					while (phoneRecordCursor.moveToNext()) {
						// 跟新Phonerecord
						PhoneRecord phoneRecord = new PhoneRecord();
						phoneRecord
								.setId(phoneRecordCursor.getLong(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
						phoneRecord
								.setPhone_number(phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName)));
						phoneRecord
								.setName(phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
						phoneRecord.setDate(date);
						phoneRecord.setType(type);
						phoneRecord
								.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);

						phoneRecordDao.update(phoneRecord);

						// 将此通话记录插入到我们的数据库中
						PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
								.getPhoneDetailDao(context);
						PhoneDetail phoneDetail = new PhoneDetail();
						phoneDetail.setPhone_number(number);
						phoneDetail.setDate(date);
						phoneDetail.setDuration(duration);
						phoneDetail.setType(type);
						phoneDetailDao.insert(phoneDetail);

						// 查询该联系人名称
						Tools.logSh("向我们数据库插入了一条数据");
						break;
					}

				} else {
					// 通话记录中还没有此号码的记录，直接插入一条新纪录
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord.setPhone_number(number);
					phoneRecord.setName(number);
					phoneRecord.setDate(System.currentTimeMillis());
					phoneRecord.setType(2);
					phoneRecord.setContact_times(1000);

					// 将此通话记录插入到我们的数据库中
					PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
							.getPhoneDetailDao(context);
					PhoneDetail phoneDetail = new PhoneDetail();
					phoneDetail.setPhone_number(number);
					phoneDetail.setDate(date);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(2);
					phoneDetailDao.insert(phoneDetail);
				}

				// 清除该通话记录

				context.getContentResolver().delete(
						Uri.parse("content://call_log/calls"), "date=?",
						new String[] { date.toString() });

			}

		}

	}

	private void systemPhoneRecord(Context context, String address) {

		Tools.logSh("开始清除系统通话记录");
		ContentResolver resolver = context.getContentResolver();
		/* 这里涉及到内容提供者的知识，其实这里是直接在操作 Android 的数据库，十分痛苦 */
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
				new String[] { "_id" }, "number=? and (type=1 or type=3)",
				new String[] { address }, "_id desc limit 1");
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(0);
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
					new String[] { id + "" });
			Tools.logSh("清除通话记录+1");
		}
	}
}