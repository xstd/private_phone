package com.xstd.pirvatephone.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.android.internal.telephony.ITelephony;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.receiver.ObservePhone;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.ShowNotificationUtils;
import com.xstd.privatephone.tools.Tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneService extends Service {
	private static final String TAG = "PhoneService";

	private long Ringtime;// 开始外拨电话的时间
	private long Dtime;// 外拨电话通话时间

	private long recevierTime;// 接到电话
	private long durationTime;// 接到电话通话时间
	private InnerReceiver receiver;// 监听外拨电话的广播接收者
	private TelephonyManager tm;
	private Mylistener listener;
	private String outingNumber = "";
	private Context mContext;
	private ContentResolver resolver;
	private ObservePhone observePhone;

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext = context;
			// 记录开始外拨时的时间
			Ringtime = System.currentTimeMillis();
			outingNumber = getResultData();
			// dosometing you want
			boolean b = ContactUtils.isPrivateContactNumber(mContext, outingNumber);
			if(b){
				Log.e(TAG, "外拨电话--隐私联系人");
				SharedPreferences sp = getSharedPreferences("IntereptNumberFlag", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putInt("flag", 2);
				editor.commit();
				return ;
			}
			Log.e(TAG, "外拨电话--非隐私联系人");
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
		
		unRegistObserver();
	}

	@Override
	public void onCreate() {
		Tools.logSh("PhoneService被创建了");
		mContext = getApplicationContext();
		registObserver();
		// 监听来电
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new Mylistener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 代码的方式 注册广播接受者.---外拨电话
		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		filter.setPriority(Integer.MAX_VALUE);
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
		public void onCallStateChanged(int state, final String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
				if (Ringtime != 0) {// 避免记录来电时的时间
					Dtime = System.currentTimeMillis() - Ringtime;// 获得通话时间
					Tools.logSh("通话持续时间为：" + Dtime);
					//removeDail(outingNumber);

				}

				Ringtime = 0;

				if (recevierTime != 0) {

					durationTime = System.currentTimeMillis() - recevierTime;
					Tools.logSh("接电通话持续时间为：" + durationTime);
					//removeReceive(incomingNumber);
					
				}
				recevierTime = 0;
				break;

			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				Tools.logSh("来电了" + incomingNumber);
				recevierTime = System.currentTimeMillis();

				// 判断该号码的拦截状态：1，是否存在情景模式的拦截号码中，2，是否存在隐私联系人的号码中(分为直接挂断与正常接听)
				ContextModelUtils contextModelUtils = new ContextModelUtils();
				ArrayList<String> modelIntereptnumbers = contextModelUtils
						.getIntereptNumbers(mContext, null);
				ArrayList<String> privateIntereptNumbers = ContactUtils
						.queryIntereptNumber(mContext);
				ArrayList<String> privateNotIntereptNumbers = ContactUtils
						.queryPrivateNotIntereptNumber(mContext);

				if (modelIntereptnumbers != null && modelIntereptnumbers.contains(incomingNumber)) {
					Tools.logSh("情景模式拦截号码" + modelIntereptnumbers);
					SharedPreferences sp = getSharedPreferences("IntereptNumberFlag", Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putInt("flag", 1);
					editor.commit();
					endCall();
					return;
				} 
				
				if (privateIntereptNumbers != null
						&& privateIntereptNumbers.contains(incomingNumber)) {
					Tools.logSh("隐私联系人拦截号码" + privateIntereptNumbers);
					SharedPreferences sp = getSharedPreferences("IntereptNumberFlag", Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putInt("flag", 1);
					editor.commit();
					endCall();
					return;
				}
				
				if(privateNotIntereptNumbers!=null && privateNotIntereptNumbers.contains(incomingNumber)){
					Tools.logSh("隐私联系人不拦截号码" + privateIntereptNumbers);
					SharedPreferences sp = getSharedPreferences("IntereptNumberFlag", Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putInt("flag", 1);
					editor.commit();
				}
				
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private void endCall() {

		new ShowNotificationUtils().showNotification(mContext);
		new ShowNotificationUtils().startShake(mContext);
		// ShowNotificationUtils.startVoice(mContext, 1, 1);

		try {
			// 反射获取ITelephony对象
			TelephonyManager telephonyManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<TelephonyManager> telephonyManagerClazz = TelephonyManager.class;
			Method getITelephonyMethod = telephonyManagerClazz
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);//
			ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(
					telephonyManager, (Object[]) null);

			Tools.logSh("iTelephony不为空");
			iTelephony.endCall();

		} catch (Exception e) {
		}
	}
	
	
	private void registObserver() {
		Tools.logSh("registObserver()被调用了");
		
		observePhone = new ObservePhone(new Handler(), mContext);
		resolver = getContentResolver();
		resolver.registerContentObserver(Calls.CONTENT_URI, true,
				observePhone);
	}
	
	private void unRegistObserver() {
		
		Tools.logSh("unRegistObserver()被调用了");
		if(resolver!=null){
			resolver.unregisterContentObserver(observePhone);
		}
	}

	private void removeReceive(String number) {

		Tools.logSh("接收到隐私联系人的电话" + number);

		// 第2步:查询系统通话记录(该号码的最近一次记录)
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneDetailCursor = resolver.query(CallLog.Calls.CONTENT_URI,
				null, CallLog.Calls.NUMBER + "=?", new String[] { number },
				"date desc limit 1");

		if (phoneDetailCursor != null && phoneDetailCursor.getCount() > 0) {
			Tools.logSh("phoneDetailCursor.count()==="
					+ phoneDetailCursor.getCount());
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

				/*
				 * String name = phoneDetailCursor.getString(phoneDetailCursor
				 * .getColumnIndex("name"));
				 */

				String name = ContactUtils.queryContactName(mContext, number);

				Tools.logSh(number + "::" + date + "::" + duration + "::"
						+ type + "::" + name);

				// 第三步：将此通话记录插入到我们的数据库中，先判断记录中是否有这个号码的记录
				PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
						.getPhoneRecordDao(mContext);
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
								.getPhoneDetailDao(mContext);
						PhoneDetail phoneDetail = new PhoneDetail();
						phoneDetail.setPhone_number(number);
						phoneDetail.setDate(date);
						phoneDetail.setDuration(duration);
						phoneDetail.setType(type);
						phoneDetail.setName(name);
						phoneDetailDao.insert(phoneDetail);

						// 查询该联系人名称
						Tools.logSh("向我们数据库插入了一条数据");
						break;
					}

				} else {
					// 通话记录中还没有此号码的记录，直接插入一条新纪录
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord.setPhone_number(number);
					phoneRecord.setName(name);
					phoneRecord.setDate(System.currentTimeMillis());
					phoneRecord.setType(1);
					phoneRecord.setContact_times(1);
					phoneRecordDao.insert(phoneRecord);

					// 将此通话记录插入到我们的数据库中
					PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
							.getPhoneDetailDao(mContext);
					PhoneDetail phoneDetail = new PhoneDetail();
					phoneDetail.setPhone_number(number);
					phoneDetail.setDate(date);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(1);
					phoneDetail.setName(name);
					phoneDetailDao.insert(phoneDetail);
				}
				phoneRecordCursor.close();
				// 清除该通话记录

				mContext.getContentResolver().delete(
						Uri.parse("content://call_log/calls"), "date=?",
						new String[] { date.toString() });

			}
			phoneDetailCursor.close();
		}
	}

	private void removeDail(String number) {

		Tools.logSh("removeDail被调用了" + number);
		// 1、获取当前的情景模式-拦截号码，判断外拨电话是否是拦截号码
		ContextModelUtils contextModelUtils = new ContextModelUtils();
		ArrayList<String> numbers = contextModelUtils.getIntereptNumbers(
				mContext, null);
		Tools.logSh("numbers===" + numbers);

		if (numbers != null && numbers.contains(number)) {
			Tools.logSh("拦截号码外拨。。。");
			// 第2步:查询系统通话记录(该号码的最近一次记录)
			ContentResolver contentResolver = mContext.getContentResolver();
			Cursor cursor = contentResolver.query(
					Uri.parse("content://call_log/calls"), null, null, null,
					"date desc limit 1");
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String address = cursor.getString(cursor
							.getColumnIndex("number"));

					// String name =
					// cursor.getString(cursor.getColumnIndex("name"));
					int type = cursor.getInt(cursor.getColumnIndex("type"));
					Long date = cursor.getLong(cursor.getColumnIndex("date"));
					Long duration = cursor.getLong(cursor
							.getColumnIndex("duration"));

					Tools.logSh("address=" + address + "::" + "type=" + type
							+ ":::" + "date=" + date + ":::" + "duration="
							+ duration);

					PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
							.getPhoneRecordDao(mContext);
					SQLiteDatabase phoneRecordDatabase = phoneRecordDao
							.getDatabase();
					Cursor phoneRecordCursor = phoneRecordDatabase.query(
							PhoneRecordDao.TABLENAME, null,
							PhoneRecordDao.Properties.Phone_number.columnName
									+ "=?", new String[] { address }, null,
							null, null);
					// 3.如果我们数据库中已有该号码的通话记录phoneRecord
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
							phoneRecord.setType(2);
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
							phoneDetail.setType(2);

							phoneDetailDao.insert(phoneDetail);
							break;
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
						phoneDetail.setType(2);

						ContactInfoDao contactInfoDao2 = ContactInfoDaoUtils
								.getContactInfoDao(mContext);
						SQLiteDatabase database = contactInfoDao2.getDatabase();
						Cursor query = database
								.query(ContactInfoDao.TABLENAME,
										null,
										ContactInfoDao.Properties.Phone_number.columnName
												+ "=?",
										new String[] { address }, null, null,
										null);
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
					phoneRecordCursor.close();
					mContext.getContentResolver().delete(
							Uri.parse("content://call_log/calls"), "date=?",
							new String[] { date.toString() });

					cursor.close();
					break;
				}
			}
		}
	}
}