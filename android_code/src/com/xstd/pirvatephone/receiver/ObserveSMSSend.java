package com.xstd.pirvatephone.receiver;

import java.util.ArrayList;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;

public class ObserveSMSSend extends ContentObserver {

	private Context mContext;

	public ObserveSMSSend(Handler handler, Context context) {
		super(handler);
		this.mContext = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Tools.logSh("短信数据库变化了");

		// 1.查询最近一次fa短信记录
		Cursor cursor = mContext.getContentResolver().query(
				Uri.parse("content://sms/"), null, null, null,
				"date desc limit 1");

		Tools.logSh("count===" + cursor.getCount());
		if (cursor != null && cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String smsNumber = cursor.getString(cursor
						.getColumnIndex("address"));
				Long smsDate = cursor.getLong(cursor.getColumnIndex("date"));

				String smsBody = cursor.getString(cursor.getColumnIndex("" + ""
						+ "body"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				
				Tools.logSh("smsNumber="+smsNumber+"   "+"type="+type+"    "+"smsDate"+smsDate);

				// 2.判断是不是发送短信（需要拦截的）
				if (type == 2) {
					SmsDetailDao smsDetailDao = SmsDetailDaoUtils
							.getSmsDetailDao(mContext);

					ContextModelUtils contextModelUtils = new ContextModelUtils();
					ArrayList<String> numbers = contextModelUtils
							.getIntereptNumbers(mContext,null);

					// 第1步:确认该短信号码是否满足过滤条件（在拦截中）
					if (numbers != null && numbers.contains(smsNumber)) {

						Tools.logSh("发现需要拦截的号码：：" + smsNumber);

						// 2. 若是拦截联系人，将此短信插入到我们的数据库中
						SmsDetail mSmsDetail = new SmsDetail();

						SmsRecordDao smsRecordDao = SmsRecordDaoUtils
								.getSmsRecordDao(mContext);
						SQLiteDatabase smsRecordDatabase = smsRecordDao
								.getDatabase();
						Cursor smsRecordCursor = smsRecordDatabase.query(
								SmsRecordDao.TABLENAME, null,
								SmsRecordDao.Properties.Phone_number.columnName
										+ "=?", new String[] { smsNumber },
								null, null, null);
						// 2.1我们数据库中已经有smsRecord
						if (smsRecordCursor != null
								&& smsRecordCursor.getCount() > 0) {
							while (smsRecordCursor.moveToNext()) {
								
								//3.判断最近一次的发送时间比我们数据库中的时间（以上操作的缺陷在于当我们数据库像系统中返回时会出现异常）
								long date = smsRecordCursor.getLong(smsRecordCursor.getColumnIndex(SmsRecordDao.Properties.Lasted_contact.columnName));
								Tools.logSh("date=-=="+date+":::"+"smsDate===="+smsDate);
								
								if(date>smsDate){
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
									Tools.logSh("短信record" + "address" + smsNumber);
								}
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

						mSmsDetail.setThread_id(2);
						mSmsDetail.setPhone_number(smsNumber);
						mSmsDetail.setDate(System.currentTimeMillis());
						mSmsDetail.setData(smsBody);

						Tools.logSh("短信详细" + "type" + 2 + "::" + "phone_number"
								+ smsNumber + "::" + "date"
								+ System.currentTimeMillis() + "::" + "body"
								+ smsBody);
						smsDetailDao.insert(mSmsDetail);
						mSmsDetail = null;
						Tools.logSh("向smsDetail插入了一条数据");

						mContext.getContentResolver().delete(
								Uri.parse("content://sms"), "date=?",
								new String[] { smsDate.toString() });

					}
				}
				cursor.close();
				break;
			}
		}
	}
}
