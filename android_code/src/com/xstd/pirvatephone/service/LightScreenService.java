package com.xstd.pirvatephone.service;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class LightScreenService extends Service {

	private PowerManager mPM;
	private KeyguardManager mKM;
	private WakeLock mWL;
	private KeyguardLock mKL;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.w("TAG", "onCreate");
		mPM = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		mWL = mPM.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		mWL.acquire();
		// 初始化键盘锁，可以锁定或解开键盘锁
		mKL = mKM.newKeyguardLock("");
		// 禁用显示键盘锁定
		mKL.disableKeyguard();
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.w("TAG", "onDestroy");
		mWL.release();
	}

}
