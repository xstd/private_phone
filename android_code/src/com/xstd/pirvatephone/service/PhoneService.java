package com.xstd.pirvatephone.service;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.pirvatephone.utils.WritePhoneDetailUtils;
import com.xstd.pirvatephone.utils.WritePhoneRecordUtils;
import com.xstd.privatephone.tools.Tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneService extends Service {

	private long Ringtime;// 开始外拨电话的时间
	private long Dtime;// 外拨电话通话时间

	private long recevierTime;// 接到电话
	private long durationTime;// 接到电话通话时间
	private InnerReceiver receiver;// 监听外拨电话的广播接收者
	private Context mContext;
	private TelephonyManager tm;
	private Mylistener listener;

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext = context;
			// 记录开始外拨时的时间
			Ringtime = System.currentTimeMillis();
			// dosometing you want
			Tools.logSh("接收到外拨电话了");
		}

	}
	@Override
	public void onDestroy() {
		if (listener != null) {
			// 当服务关闭时取消状态监听
			tm.listen(listener, PhoneStateListener.LISTEN_NONE);
			listener = null;
		}

		if (receiver != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(receiver);
			receiver = null;
			super.onDestroy();
		}
	}

	@Override
	public void onCreate() {

		Tools.logSh("PhoneService被创建了");

		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new Mylistener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电的挂断操作
		
		
		// 代码的方式 注册广播接受者.
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class Mylistener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
				if (Ringtime != 0) {// 避免记录来电时的时间
					Dtime = System.currentTimeMillis() - Ringtime;// 获得通话时间
					Tools.logSh("通话持续时间为：" + Dtime);
					removeDail(incomingNumber);

				}

				Ringtime = 0;

				if (recevierTime != 0) {
					durationTime = System.currentTimeMillis() - recevierTime;
					Tools.logSh("接电通话持续时间为：" + durationTime);
					removeReceive(incomingNumber);
				}
				recevierTime = 0;
				break;

			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				recevierTime = System.currentTimeMillis();

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通状态

				break;
			}
		}
	}

	private void removeReceive(String number) {
		// 第1步:确认该短信号码是否满足过滤条件（在隐私联系人中）
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
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
					// 查询通话记录
					ContentResolver resolver = mContext.getContentResolver();
					Cursor phoneDetailCursor = resolver.query(
							CallLog.Calls.CONTENT_URI, null,
							CallLog.Calls.NUMBER + "=?",
							new String[] { number }, "date desc limit 1");

					if (phoneDetailCursor != null
							&& phoneDetailCursor.getCount() > 0) {
						while (phoneDetailCursor.moveToNext()) {
							// 得到手机号码
							String address = phoneDetailCursor
									.getString(phoneDetailCursor
											.getColumnIndex("number"));
							// 得到开始时间
							Long date = phoneDetailCursor
									.getLong(phoneDetailCursor
											.getColumnIndex("date"));
							// 通话持续时间

							Long duration = phoneDetailCursor
									.getLong(phoneDetailCursor
											.getColumnIndex("duration"));
							// 通话类型
							int type = phoneDetailCursor
									.getInt(phoneDetailCursor
											.getColumnIndex("type"));
							// 通化人姓名
							String name = phoneDetailCursor
									.getString(phoneDetailCursor
											.getColumnIndex("name"));

							Tools.logSh(number + "::" + date + "::" + duration
									+ "::" + type + "::" + name);

							// 将此通话记录插入到我们的数据库中，先判断记录中是否有这个号码的记录
							PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
									.getPhoneRecordDao(mContext);
							SQLiteDatabase phoneRecordDatabase = phoneRecordDao
									.getDatabase();
							Cursor phoneRecordCursor = phoneRecordDatabase
									.query(PhoneRecordDao.TABLENAME,
											null,
											PhoneRecordDao.Properties.Phone_number.columnName
													+ "=?",
											new String[] { number }, null,
											null, null);
							if (phoneRecordCursor != null
									&& phoneRecordCursor.getCount() > 0) {// 通话记录中已经有此号码的记录，跟新

								while (phoneRecordCursor.moveToNext()) {
									// 跟新Phonerecord
									PhoneRecord phoneRecord = new PhoneRecord();
									phoneRecord
											.setId(phoneRecordCursor.getLong(phoneRecordCursor
													.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
									phoneRecord
											.setPhone_number(phoneRecordCursor
													.getString(phoneRecordCursor
															.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName)));
									phoneRecord
											.setName(phoneRecordCursor
													.getString(phoneRecordCursor
															.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
									phoneRecord.setDate(date);
									phoneRecord.setType(type);
									phoneRecord
											.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
													.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);

									phoneRecordDao.update(phoneRecord);

									// 将此通话记录插入到我们的数据库中
									PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
											.getPhoneDetailDao(mContext);
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
										.getPhoneDetailDao(mContext);
								PhoneDetail phoneDetail = new PhoneDetail();
								phoneDetail.setPhone_number(number);
								phoneDetail.setDate(date);
								phoneDetail.setDuration(duration);
								phoneDetail.setType(2);
								phoneDetailDao.insert(phoneDetail);
							}

							// 清除该通话记录

							mContext.getContentResolver().delete(
									Uri.parse("content://call_log/calls"),
									"date=?", new String[] { date.toString() });

						}

					}

				}
			}
		}
	}

	private void removeDail(String number) {

		// 判断该号码是不是当前情景模式下需要拦截的号码。(以下为测试代码)
		Cursor cursor = mContext.getContentResolver().query(
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
						phoneRecord.setPhone_number(address);
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
						phoneDetail
								.setName(phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
						phoneDetail.setDate(date);
						phoneDetail.setDuration(duration);
						phoneDetail.setType(type);

						phoneDetailDao.insert(phoneDetail);
						break;
					}
				} else {// 如果还没有该号码的通话记录
					Tools.logSh("暂无该号码的通话记录");
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord.setPhone_number(address);

					phoneRecord.setDate(System.currentTimeMillis());
					phoneRecord.setType(1);
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

				break;

			}
			cursor.close();
			break;
		}

		WritePhoneDetailUtils.writePhoneDetail(mContext,
				new String[] { number });
		WritePhoneRecordUtils.writePhoneRecord(mContext,
				new String[] { number });
	}
}