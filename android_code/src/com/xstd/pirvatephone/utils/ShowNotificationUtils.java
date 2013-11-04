package com.xstd.pirvatephone.utils;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.activity.PrivateCommActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ShowNotificationUtils {
	private static NotificationManager mNotificationManager;
	private static Notification notification;

	@SuppressWarnings("deprecation")
	public static void showNotification(Context context){
		//消息通知栏
		notification = null;
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sp = context.getSharedPreferences("Setting_Info", 0);
        String[] mTitleStrs = context.getResources().getStringArray(
				R.array.s_setting_statusbar_titles);
    	String[] mContentStrs = context.getResources().getStringArray(
				R.array.s_setting_statusbar_contents);
        //定义通知栏展现的内容信息

        int icon = sp.getInt("Title",R.drawable.ic_statusbar_0);
        CharSequence tickerText = "我的通知栏标题";

        long when = System.currentTimeMillis();

        notification = new Notification(icon, tickerText, when);

        //定义下拉通知栏时要展现的内容信息,
    	
        String title = sp.getString("Title", mTitleStrs[0]);
        String desc = sp.getString("Desc", mContentStrs[0]);
        
        Intent notificationIntent = new Intent(context, PrivateCommActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        notification.setLatestEventInfo(context, title, desc,contentIntent);  
        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);


	}
	
	public static void clearNotifacation(){
		if(mNotificationManager!=null && notification!=null){
			mNotificationManager.cancel(1);
		}
	}
}
