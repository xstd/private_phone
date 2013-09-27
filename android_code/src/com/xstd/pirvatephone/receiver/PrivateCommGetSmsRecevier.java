package com.xstd.pirvatephone.receiver;

import com.xstd.privatephone.tools.Tools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class PrivateCommGetSmsRecevier extends BroadcastReceiver {
	private String TAG = "smsreceiveandmask";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Tools.logSh(">>>>>>>onReceive start");
		// 第一步、获取短信的内容和发件人
		StringBuilder body = new StringBuilder();// 短信内容
		StringBuilder number = new StringBuilder();// 短信发件人
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] _pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] message = new SmsMessage[_pdus.length];
			for (int i = 0; i < _pdus.length; i++) {
				message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
			}
			for (SmsMessage currentMessage : message) {
				body.append(currentMessage.getDisplayMessageBody());
				number.append(currentMessage.getDisplayOriginatingAddress());
			}
			String smsBody = body.toString();
			String smsNumber = number.toString();
			if (smsNumber.contains("+86")) {
				smsNumber = smsNumber.substring(3);
			}
			
			
			// 第二步:确认该短信号码是否满足过滤条件（在隐私联系人中）
			
			boolean flags_filter = false;
			
			if (smsNumber.equals("10086")) {// 屏蔽10086发来的短信
				flags_filter = true;
				Log.v(TAG, "sms_number.equals(10086)");
			}
			// 第三步:取消
			if (flags_filter) {
				this.abortBroadcast();
			}
		}
		Tools.logSh(">>>>>>>onReceive end");
	}
}