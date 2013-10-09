package com.xstd.pirvatephone.receiver;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public class SimulateSendSMSReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		SimulateComm simu = (SimulateComm) intent.getSerializableExtra("simu");
		//向系统sms数据库中插入一条短信
		ContentValues values = new ContentValues();
		values.put("address", simu.getPhonenumber());
		values.put("date", simu.getFuturetime().getTime());
		values.put("read", 0);
		values.put("status", -1);
		values.put("type", 1);
		values.put("body", simu.getContent());
		context.getContentResolver().insert(Uri.parse("content://sms/inbox"),values);
		//想用户发送有条短信到来的通知
		Notification notification = new Notification();
		notification.icon = R.drawable.perm_group_messages;
		notification.largeIcon = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.perm_group_messages);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.when = simu.getFuturetime().getTime();
		//点击通知可以进入系统默认短信界面
		Intent intent2 = new Intent();
		intent2.setAction("android.intent.action.MAIN");
		intent2.addCategory("android.intent.category.DEFAULT");
		intent2.setType("vnd.android.cursor.dir/mms");
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				getResultCode(), intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
		if (TextUtils.isEmpty(simu.getName())) {
			notification.tickerText = simu.getPhonenumber() + ":"
					+ simu.getContent();
			notification.setLatestEventInfo(context,
					simu.getPhonenumber() + "", simu.getContent(),
					pendingIntent);
		} else {
			notification.tickerText = simu.getName() + ":" + simu.getContent();
			notification.setLatestEventInfo(context, simu.getName() + "",
					simu.getContent(), pendingIntent);
		}
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		//通过notificationmanager发送通知
		nm.notify(new Random().nextInt(), notification);
	}
}
