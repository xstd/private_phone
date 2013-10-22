package com.xstd.pirvatephone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;

import com.xstd.pirvatephone.receiver.PrivateCommSmsRecevier;
import com.xstd.privatephone.tools.Tools;


public class SmsService extends Service {

	private InnerReceiver receiver;

	private IntentFilter filter;

	private IntentFilter filter2;

	private PrivateCommSmsRecevier smsRecevier;


	@Override
	public void onDestroy() {

		if (receiver != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(receiver);
			receiver = null;
			super.onDestroy();
		}
		if (smsRecevier != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(smsRecevier);
			smsRecevier = null;
			super.onDestroy();
		}
	}

	@Override
	public void onCreate() {

		Tools.logSh("SmsService被创建了");


		receiver = new InnerReceiver();
		filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);
		
		smsRecevier = new PrivateCommSmsRecevier();
		filter2 = new IntentFilter();
		filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsRecevier, filter2);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 自定义mServiceReceiver覆盖BroadcastReceiver聆听短信状态信息 */
	public class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Tools.logSh("监听到发短信操作");
		}
	
	}

}