package com.xstd.pirvatephone.receiver;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class ObserveSMSRecevier extends ContentObserver {
	private final String SMSSEND_TYPE = "smsInfo";

	private final String TYPE = "send";

	private static final String TAG = "SystemSens_sendsms";

	private Context mContext;

	public ObserveSMSRecevier(Handler handler, Context context) {
		super(handler);
		this.mContext = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		Tools.logSh("接收到了新的短信");

		Cursor cursor = this.mContext.getContentResolver()
				.query(Uri.parse("content://sms/inbox"), null, null, null,
						null);
		while (cursor.moveToNext()) {

			String address = cursor.getString(cursor.getColumnIndex("address"));
			Long date = cursor.getLong(cursor.getColumnIndex("date"));

			String body = cursor.getString(cursor.getColumnIndex("body"));
			// 1是接受，2是发送
			int type = 3;

			// 查询该号码是否是隐私通信号码
			ContactInfoDao contactInfoDao = ContactInfoDaoUtils
					.getContactInfoDao(mContext);
			SQLiteDatabase contactInfoDaoDatabase = contactInfoDao
					.getDatabase();
			Cursor contactInfoCursor = contactInfoDaoDatabase.query(
					ContactInfoDao.TABLENAME, null,
					ContactInfoDao.Properties.Phone_number.columnName + "=?",
					new String[] { address }, null, null, null);

			if (contactInfoCursor != null && contactInfoCursor.getCount() > 0) {

				// 向我们的数据库添加一条SmsDetail
				SmsDetail mSmsDetail = new SmsDetail();

				SmsDetailDao smsDetailDao = SmsDetailDaoUtils
						.getSmsDetailDao(mContext);

				mSmsDetail.setThread_id(type);
				mSmsDetail.setPhone_number(address);
				mSmsDetail.setDate(date);
				mSmsDetail.setData(body);

				Tools.logSh("短信详细" + "type" + type + "::" + "phone_number"
						+ address + "::" + "date" + date + "::" + "body" + body);
				smsDetailDao.insert(mSmsDetail);
				mSmsDetail = null;

				// 查询我们数据库中SmsRecord信息(旧)
				SmsRecordDao smsRecordDao = SmsRecordDaoUtils
						.getSmsRecordDao(mContext);
				SQLiteDatabase smsRecordDatabase = smsRecordDao.getDatabase();
				Cursor smsRecordCursor = smsRecordDatabase.query(
						SmsRecordDao.TABLENAME, null,
						SmsRecordDao.Properties.Phone_number.columnName + "=?",
						new String[] { address }, null, null, null);
				if (smsRecordCursor != null && smsRecordCursor.getCount() > 0) {
					while (smsRecordCursor.moveToNext()) {
						SmsRecord smsRecord = new SmsRecord();
						smsRecord.setPhone_number(address);
						smsRecord
								.setCount(smsRecordCursor.getInt(smsRecordCursor
										.getColumnIndex(SmsRecordDao.Properties.Count.columnName)) + 1);
						smsRecord
								.setId(smsRecordCursor.getLong(smsRecordCursor
										.getColumnIndex(SmsRecordDao.Properties.Id.columnName)));
						smsRecord.setLasted_contact(date);
						smsRecord.setLasted_data(body);
						// 向我们的数据库跟新SmsRecord
						smsRecordDao.update(smsRecord);
						Tools.logSh("向我们smsRecord数据库中跟新了smsRecord");
						Tools.logSh("短信record" + "address" + address);
					}
				} else {
					SmsRecord smsRecord = new SmsRecord();
					smsRecord.setPhone_number(address);
					smsRecord.setCount(1);
					smsRecord.setLasted_contact(date);
					smsRecord.setLasted_data(body);
					// 向我们的数据库跟新SmsRecord
					smsRecordDao.insert(smsRecord);
					Tools.logSh("向我们smsRecord数据库中插入了一条smsRecord");
					Tools.logSh("短信record" + "address" + address);
				}

				this.mContext.getContentResolver().delete(
						Uri.parse("content://sms"), "date=?",
						new String[] { date.toString() });

				cursor.close();
				break ;
			}
		}
	}
}
