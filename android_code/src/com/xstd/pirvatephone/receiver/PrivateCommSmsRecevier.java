package com.xstd.pirvatephone.receiver;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.pirvatephone.utils.WriteSmsDetailUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 4.0以后需要手动开启
 * 
 * @author Administrator
 * 
 */
public class PrivateCommSmsRecevier extends BroadcastReceiver {
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		Tools.logSh("接收到短信广播事件");
		// 第一步、获取短信的内容和发件人

		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		if (pdus != null && pdus.length > 0) {
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++) {
				byte[] pdu = (byte[]) pdus[i];
				messages[i] = SmsMessage.createFromPdu(pdu);
			}
			for (SmsMessage message : messages) {
				String smsBody = message.getMessageBody();// 得到短信内容
				String smsNumber = message.getOriginatingAddress();// 得到发信息的号码
				Long smsDate = message.getTimestampMillis();// 得到发信息的时间
				if (smsNumber.contains("+86")) {
					smsNumber = smsNumber.substring(3);
				}
				Tools.logSh("接收到短信广播事件"+"smsNumber="+smsNumber+"smsBody"+smsBody+"smsDate"+smsDate);
				// 第二步:确认该短信号码是否满足过滤条件（在隐私联系人中）
				ContactInfoDao contactInfoDao = ContactInfoDaoUtils
						.getContactInfoDao(context);
				SmsDetailDao smsDetailDao = SmsDetailDaoUtils
						.getSmsDetailDao(context);

				SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

				// 获取通话记录表中所有的电话号码
				Cursor recordCursor = contactDatabase.query(
						ContactInfoDao.TABLENAME, null, null, null, null, null,
						null);

				if (recordCursor != null) {
					while (recordCursor.moveToNext()) {
						String privatePhone = recordCursor
								.getString(recordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
						if (smsNumber.equals(privatePhone)) {
							// 若是隐私联系人，将此短信插入到我们的数据库中
							SmsDetail mSmsDetail = new SmsDetail();
							// thread_id

							SmsRecordDao smsRecordDao = SmsRecordDaoUtils
									.getSmsRecordDao(mContext);
							SQLiteDatabase smsRecordDatabase = smsRecordDao
									.getDatabase();
							Cursor smsRecordCursor = smsRecordDatabase
									.query(SmsRecordDao.TABLENAME,
											null,
											SmsRecordDao.Properties.Phone_number.columnName
													+ "=?",
											new String[] { smsNumber }, null,
											null, null);
							// 我们数据库中已经有smsRecord
							if (smsRecordCursor != null
									&& smsRecordCursor.getCount() > 0) {
								while (smsRecordCursor.moveToNext()) {
									SmsRecord smsRecord = new SmsRecord();
									smsRecord.setPhone_number(smsNumber);
									smsRecord
											.setCount(smsRecordCursor.getInt(smsRecordCursor
													.getColumnIndex(SmsRecordDao.Properties.Count.columnName)) + 1);
									smsRecord
											.setId(smsRecordCursor.getLong(smsRecordCursor
													.getColumnIndex(SmsRecordDao.Properties.Id.columnName)));
									smsRecord.setLasted_contact(smsDate);
									smsRecord.setLasted_data(smsBody);
									// 向我们的数据库跟新SmsRecord
									smsRecordDao.update(smsRecord);
									Tools.logSh("向我们smsRecord数据库中跟新了smsRecord");
									Tools.logSh("短信record" + "address"
											+ smsNumber);
								}
							} else {
								SmsRecord smsRecord = new SmsRecord();
								smsRecord.setPhone_number(smsNumber);
								smsRecord.setCount(1);
								smsRecord.setLasted_contact(smsDate);
								smsRecord.setLasted_data(smsBody);
								// 向我们的数据库跟新SmsRecord
								smsRecordDao.insert(smsRecord);
								Tools.logSh("向我们smsRecord数据库中插入了一条smsRecord");
								Tools.logSh("短信record" + "address" + smsNumber);
							}

							this.mContext.getContentResolver().delete(
									Uri.parse("content://sms"), "date=?",
									new String[] { smsDate.toString() });

							mSmsDetail.setThread_id(1);
							mSmsDetail.setPhone_number(smsNumber);
							mSmsDetail.setDate(System.currentTimeMillis());
							mSmsDetail.setData(smsBody);

							Tools.logSh("短信详细" + "type" + 1 + "::"
									+ "phone_number" + smsNumber + "::"
									+ "date" + System.currentTimeMillis()
									+ "::" + "body" + smsBody);
							smsDetailDao.insert(mSmsDetail);
							mSmsDetail = null;
							Tools.logSh("向smsDetail插入了一条数据");
							// 第三步:取消掉广播事件
							this.abortBroadcast();
						}
					}
				}
			}
		}
	}
}