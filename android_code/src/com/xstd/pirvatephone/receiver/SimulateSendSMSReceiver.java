package com.xstd.pirvatephone.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public class SimulateSendSMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SimulateSendSMSReceiver";

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		SimulateComm simu = (SimulateComm)intent.getSerializableExtra("simu");
		ContentValues values = new ContentValues();
		values.put("address", simu.getPhonenumber());
		values.put("date", simu.getFuturetime().getTime());
		values.put("read", 0);
		values.put("status", -1);
		values.put("type", 1);
		values.put("body", simu.getContent());
		context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
		// Notification notification = new Notification();
		// notification.icon = R.drawable.perm_group_messages;
		// notification.largeIcon = BitmapFactory.decodeResource(
		// context.getResources(), R.drawable.perm_group_messages);
		// notification.defaults = Notification.DEFAULT_ALL;
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// notification.when = simu.getFuturetime().getTime();
		// Intent intent2 = new Intent();
		// intent2.setAction("android.intent.action.MAIN");
		// intent2.addCategory("android.intent.category.DEFAULT");
		// intent2.setType("vnd.android.cursor.dir/mms");
		// PendingIntent pendingIntent = PendingIntent.getActivity(context,
		// getResultCode(), intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		// if (TextUtils.isEmpty(simu.getName())) {
		// notification.tickerText = simu.getPhonenumber() + ":"
		// + simu.getContent();
		// notification.setLatestEventInfo(context,
		// simu.getPhonenumber() + "", simu.getContent(),
		// pendingIntent);
		// } else {
		// notification.tickerText = simu.getName() + ":" + simu.getContent();
		// notification.setLatestEventInfo(context, simu.getName() + "",
		// simu.getContent(), pendingIntent);
		// }
		// NotificationManager nm = (NotificationManager) context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// nm.notify(new Random().nextInt(), notification);
	}
}
