package com.xstd.pirvatephone.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.CallLog;

import com.xstd.pirvatephone.activity.SimulateCallActivity;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public class SimulateSendPhoneReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateSendPhoneReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		SimulateComm simu = (SimulateComm) intent.getSerializableExtra("simu");
		ContentValues values = new ContentValues();
		values.put(CallLog.Calls.NUMBER, simu.getPhonenumber());
		values.put(CallLog.Calls.DATE, simu.getFuturetime().getTime());
		values.put(CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
		values.put(CallLog.Calls.NEW, 1);
		context.getContentResolver().insert(CallLog.CONTENT_URI, values);
		Intent activity = new Intent();
		activity.setClass(context, SimulateCallActivity.class);
		activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(activity);
	}

}
