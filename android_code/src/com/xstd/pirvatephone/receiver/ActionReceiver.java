package com.xstd.pirvatephone.receiver;

import java.util.ArrayList;

import com.xstd.pirvatephone.service.PhoneService;
import com.xstd.pirvatephone.service.SmsService;
import com.xstd.privatephone.tools.Tools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionReceiver extends BroadcastReceiver {

	public Context mContext;
	public static final String ACTION = "android.intent.action.USER_PRESENT";

	@Override
	public void onReceive(Context context, Intent intent) {
		Tools.logSh("ActionReceiver");
		mContext = context;
		if (intent.getAction().equals(ACTION)) {
			
			if(!isWorked("com.xstd.pirvatephone.service.PhoneService")){
				Intent phoneIntent = new Intent(context, PhoneService.class);
				context.startService(phoneIntent);
			}
			if(!isWorked("com.xstd.pirvatephone.service.SmsService")){
			Intent smsIntent = new Intent(context, SmsService.class);
			context.startService(smsIntent);
			}
		}
	}

	public boolean isWorked(String serviceName) {
		ActivityManager myManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
