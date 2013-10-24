package com.xstd.pirvatephone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.xstd.pirvatephone.globle.GlobleVaries;
import com.xstd.pirvatephone.receiver.CommSmsSendRecevier;
import com.xstd.pirvatephone.receiver.ObserveSMSSend;
import com.xstd.pirvatephone.receiver.PrivateCommSmsRecevier;
import com.xstd.privatephone.tools.Tools;

public class SmsService extends Service {

	private IntentFilter filter;

	private IntentFilter filter2;

	private PrivateCommSmsRecevier smsGetRecevier;

	private CommSmsSendRecevier smsSendRecevier;

	private ObserveSMSSend observeSMSsend;

	private ContentResolver resolver;

	private IntentFilter filter3;

	@Override
	public void onDestroy() {

		if (smsSendRecevier != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(smsSendRecevier);
			smsSendRecevier = null;
			super.onDestroy();
		}
		if (smsGetRecevier != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(smsGetRecevier);
			smsGetRecevier = null;
			super.onDestroy();
		}
		if(observeSMSsend != null){
			unregistObserver();
		}
		
	}

	@Override
	public void onCreate() {

		Tools.logSh("SmsService被创建了");

		smsGetRecevier = new PrivateCommSmsRecevier();
		filter2 = new IntentFilter();
		filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsGetRecevier, filter2);

		registObserver();
		registControl();

		super.onCreate();

	}

	private void registObserver() {
		Tools.logSh("registObserver()被调用了");
		
		// ”表“内容观察者 ，通过测试我发现只能监听此Uri -----> content://sms
		// 监听不到其他的Uri 比如说 content://sms/outbox
		observeSMSsend = new ObserveSMSSend(new Handler(), GlobleVaries.CONTEXT);
		resolver = GlobleVaries.CONTEXT.getContentResolver();
		resolver.registerContentObserver(Uri.parse("content://sms"), true,
				observeSMSsend);
	}
	
	private void registControl(){
		Tools.logSh("registControl()被调用了");
		ObserverControlRecevier controlRecevier = new ObserverControlRecevier();
		filter3 = new IntentFilter();
		filter3.addAction("ObserverControlRecevier");
		registerReceiver(controlRecevier, filter2);
	}

	private void unregistObserver() {
		Tools.logSh("unregistObserver()被调用了");
		resolver.unregisterContentObserver(observeSMSsend);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class ObserverControlRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Tools.logSh("ObserverControlRecevier ----onReceive()被调用了");
			
			int isOpen = intent.getIntExtra("IsOpen", 1);
			if (isOpen == 1) {
				Tools.logSh("接收到广播+isOpen===="+isOpen);
				
				registObserver();
			} else {
				Tools.logSh("接收到广播+isOpen===="+isOpen);
				unregistObserver();
			}

		}

	}

}