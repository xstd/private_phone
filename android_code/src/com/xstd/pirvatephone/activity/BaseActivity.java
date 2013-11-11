package com.xstd.pirvatephone.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.xstd.pirvatephone.app.PrivatePhoneApplication;
import com.xstd.pirvatephone.utils.BackgroundSoundManager;
import com.xstd.privatephone.view.ComeMySelfWindowView;
import com.xstd.privatephone.view.JumpSoftwareWindowView;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-9-9 Time: PM3:08 To change
 * this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {

	protected static int count = -1;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		PrivatePhoneApplication app = (PrivatePhoneApplication) getApplication();
		if (app.isComeon()) {
			ComeMySelfWindowView.getInstance(getApplicationContext()).dismiss();
			app.setComeon(false);
		}
		if (sp.getBoolean("play_background_sound", false) && count == -1) {
			BackgroundSoundManager.getInstance(getApplicationContext())
					.playBackgroundSound();
		}
		if (sp.getBoolean("jump_other_software", false) && count == -1) {
			JumpSoftwareWindowView.getInstance(getApplicationContext()).show();
		}
		count++;
	}

	@Override
	protected void onStop() {
		super.onStop();
		count--;
		if (count == -1) {
			BackgroundSoundManager.getInstance(getApplicationContext())
					.pauseBackgroundSound();
		}
		if (sp.getBoolean("jump_other_software", false) && count == -1) {
			JumpSoftwareWindowView.getInstance(getApplicationContext())
					.dismiss();
		}
	}

}
