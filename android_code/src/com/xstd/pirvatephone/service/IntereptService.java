package com.xstd.pirvatephone.service;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.receiver.PrivateCommPhoneRecevier;
import com.xstd.pirvatephone.receiver.PrivateCommSmsRecevier;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.GetContactUtils;
import com.xstd.pirvatephone.utils.GetModelUtils;
import com.xstd.privatephone.tools.Tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

/**
 * 拦截服务
 * 
 * @author Administrator
 * 
 */
public class IntereptService extends Service {

	private PrivateCommSmsRecevier smsRecevier;
	private IntentFilter smsRecevierFilter;
	private PrivateCommPhoneRecevier phoneRecevier;
	private IntentFilter phoneRecevierFilter;
	private String[] intereptNumbers;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Tools.logSh("服务被创建了");
		startInterept();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		stopRecevierInterept();
		super.onDestroy();
	}

	private void startInterept() {// // sms发送
		Tools.logSh("startInterept被调用了");
	// ObserveSMSSend send = new ObserveSMSSend(new Handler(), this);
	// this.getContentResolver().registerContentObserver(
	// Uri.parse("content://sms/"), true, send);
	// // sms接收
	// ObserveSMSRecevier recevier = new ObserveSMSRecevier(new Handler(),
	// this);
	// this.getContentResolver().registerContentObserver(
	// Uri.parse("content://sms/"), true, recevier);

		/*
		 * phoneDail = new ObservePhoneDail(new Handler(), this);
		 * this.getContentResolver().registerContentObserver(
		 * Uri.parse("content://call_log/"), true, phoneDail);
		 */

		smsRecevier = new PrivateCommSmsRecevier();
		smsRecevierFilter = new IntentFilter();
		smsRecevierFilter.setPriority(Integer.MAX_VALUE);
		smsRecevierFilter.addAction("android.prosvider.Telephony.SMS_RECEIVED");// 为IntentFilter
		registerReceiver(smsRecevier, smsRecevierFilter);// 将BroadcastReceiver对象注册到系统中

		phoneRecevier = new PrivateCommPhoneRecevier();
		phoneRecevierFilter = new IntentFilter();
		phoneRecevierFilter.setPriority(Integer.MAX_VALUE);
		phoneRecevierFilter.addAction("android.intent.action.PHONE_STATE"); // 为IntentFilter
		registerReceiver(phoneRecevier, phoneRecevierFilter);// 将BroadcastReceiver对象注册到系统中

	}

	private void stopRecevierInterept() {
		if (smsRecevier != null && phoneRecevier != null) {
			unregisterReceiver(smsRecevier);
			unregisterReceiver(phoneRecevier);
		}
	}

	private void stopSendinterept() {

	}

	public class IntereptControlRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		
			intent.getStringExtra("");

		}

		public void stopRecevier() {
			stopRecevierInterept();
		}

		public void stopSend() {
			stopSendinterept();
		}

	}

}
