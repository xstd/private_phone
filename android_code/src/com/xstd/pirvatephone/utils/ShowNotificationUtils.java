package com.xstd.pirvatephone.utils;

import java.util.HashMap;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.activity.PrivateCommActivity;
import com.xstd.privatephone.tools.Tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

public class ShowNotificationUtils {
	private NotificationManager mNotificationManager;
	private Notification notification;
	private Vibrator vibrator;

	@SuppressWarnings("deprecation")
	public void showNotification(Context context) {

		// 1.获取当前的设置
		SharedPreferences sp = context.getSharedPreferences("Setting_Info", 0);
		boolean show_Notification = sp.getBoolean("Show_Notification", true);
		if (show_Notification) {
			// 消息通知栏
			notification = null;
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			String[] mTitleStrs = context.getResources().getStringArray(
					R.array.s_setting_statusbar_titles);
			String[] mContentStrs = context.getResources().getStringArray(
					R.array.s_setting_statusbar_contents);
			// 定义通知栏展现的内容信息

			int icon = sp.getInt("Icon", R.drawable.ic_statusbar_0);
			String tickerText = "";

			long when = System.currentTimeMillis();

			notification = new Notification(icon, tickerText, when);

			// 定义下拉通知栏时要展现的内容信息,
			int checkedId = sp.getInt("CheckedId", 0);
			String cont = sp.getString("" + checkedId, "");
			String title;
			String desc;

			if (cont == "") {
				String[] strings = cont.split(":");
				title = strings[0];
				desc = strings[1];
			} else {
				Tools.logSh("count==" + cont);
				title = sp.getString("Title", mTitleStrs[0]);
				desc = sp.getString("Desc", mContentStrs[0]);
			}

			System.out.println("Title=" + title + "  Desc=" + desc);

			Intent notificationIntent = new Intent(context,
					PrivateCommActivity.class);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			notification
					.setLatestEventInfo(context, title, desc, contentIntent);
			// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(1, notification);
		}
	}

	public void clearNotifacation() {
		if (mNotificationManager != null && notification != null) {
			mNotificationManager.cancel(1);
		}
	}

	@SuppressWarnings("deprecation")
	public void startShake(Context context) {
		AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		@SuppressWarnings("deprecation")
		int vibrate_setting = mAudioManager
				.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);

		if (vibrate_setting == AudioManager.VIBRATE_SETTING_OFF) {
			SharedPreferences sp = context.getSharedPreferences("Setting_Info",
					0);
			boolean Show_Shake = sp.getBoolean("Show_Shake", true);
			if (Show_Shake) {
				vibrator = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
				vibrator.vibrate(pattern, -1); // 重复两次上面的pattern
												// 如果只想震动一次，index设为-1(2)
			}

		}
	}

	public void stopShake() {
		if (vibrator != null) {
			vibrator.cancel();
		}
	}

	public static void startVoice(Context context, int sound, int number) {

		SharedPreferences sp = context.getSharedPreferences("Setting_Info", 0);
		boolean show_Voice = sp.getBoolean("Show_Voice", true);

		SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		HashMap<Integer, Integer> spMap = new HashMap<Integer, Integer>();

		if (show_Voice) {
			playSounds(context, soundPool, spMap, sound, number);
		}

	}

	public static void playSounds(Context context, SoundPool soundPool,
			HashMap<Integer, Integer> spMap, int sound, int number) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

		soundPool.play((Integer) spMap.get(sound), volumnRatio, volumnRatio, 1,
				number, 1);
	}

}
