package com.xstd.pirvatephone.receiver;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public class SimulateSendSMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateSendSMSReceiver";

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		SimulateComm simu = (SimulateComm) intent.getSerializableExtra("simu");
		Log.w(TAG, simu.getPhonenumber() + "");
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
				new String[] { String.valueOf(simu.getPhonenumber()) }, null);
		if (cursor == null) {
			Log.w(TAG, "cursor is null");
		} else if (cursor.getCount() < 1) {
			Log.w(TAG, "cursor not null but no data");
			Log.w(TAG, cursor.toString());
		} else {
			Log.w(TAG, "has data");
		}
		Notification notification = new Notification();
		notification.icon = R.drawable.perm_group_messages;
		notification.largeIcon = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.perm_group_messages);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.when = simu.getFuturetime().getTime();
		notification.tickerText = simu.getPhonenumber() + ":"
				+ simu.getContemt();
		Intent intent2 = new Intent();
		intent2.setAction("android.intent.action.MAIN");
		intent2.addCategory("android.intent.category.DEFAULT");
		intent2.setType("vnd.android.cursor.dir/mms");
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				getResultCode(), intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, simu.getPhonenumber() + "",
				simu.getContemt(), pendingIntent);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(new Random().nextInt(), notification);
	}
}
