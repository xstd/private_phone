package com.xstd.pirvatephone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.xstd.pirvatephone.receiver.ObserveSMS;
import com.xstd.pirvatephone.receiver.PrivateCommSmsRecevier;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.privatephone.tools.Tools;

public class SmsService extends Service {
	private static final String TAG = "SmsService";
	private PrivateCommSmsRecevier smsGetRecevier;
	private ObserveSMS observeSMS;
	private ContentResolver resolver;
	private IntentFilter smsRecevierFilter;
	private Context mContext;
	private InnerReceiver receiver;

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(TAG, "发短信le");
			String data = getResultData();
			Log.e(TAG, data);
			mContext = context;
			boolean b = ContactUtils.isPrivateContactNumber(mContext, data);
			if (b) {
				Log.e(TAG, "发短信--隐私联系人");
				SharedPreferences sp = getSharedPreferences(
						"IntereptNumberFlag", Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putInt("smsflag", 1);
				editor.commit();
				return;
			}
			Log.e(TAG, "发短信--非隐私联系人");
		}
	}

	@Override
	public void onDestroy() {

		if (smsGetRecevier != null) {
			// 服务关闭时，外拨电话事件广播接收者也销毁
			unregisterReceiver(smsGetRecevier);
			smsGetRecevier = null;
		}
		
		if(receiver!=null){
			unregisterReceiver(receiver);
			receiver= null;
		}
		if (observeSMS != null) {
			unregistObserver();
		}
		
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		Tools.logSh("SmsService被创建了");
		registObserver();

		smsGetRecevier = new PrivateCommSmsRecevier();
		smsRecevierFilter = new IntentFilter();
		smsRecevierFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		smsRecevierFilter.setPriority(Integer.MAX_VALUE);
		smsRecevierFilter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(smsGetRecevier, smsRecevierFilter);

		receiver = new InnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SEND);
		filter.setPriority(Integer.MAX_VALUE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		registerReceiver(receiver, filter);

		super.onCreate();

	}

	private void registObserver() {
		Log.e(TAG, "registObserver()被调用了");

		// ”表“内容观察者 ，通过测试我发现只能监听此Uri -----> content://sms
		// 监听不到其他的Uri 比如说 content://sms/outbox
		observeSMS = new ObserveSMS(new Handler(), mContext);
		resolver = getContentResolver();
		resolver.registerContentObserver(Uri.parse("content://sms"), true,
				observeSMS);
	}

	private void unregistObserver() {
		Tools.logSh("unregistObserver()被调用了");
		resolver.unregisterContentObserver(observeSMS);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}