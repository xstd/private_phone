package com.xstd.pirvatephone.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.service.LightScreenService;

public class SimulateCallActivity extends BaseActivity implements
		OnClickListener {

	private MediaPlayer mMediaPlayer;
	private Vibrator vibrator;

	@ViewMapping(ID = R.id.hangup)
	public ImageView hangup;

	@ViewMapping(ID = R.id.dial)
	public ImageView dial;

	@ViewMapping(ID = R.id.pickup)
	public ImageView pickup;

	@ViewMapping(ID = R.id.incalling)
	public ImageView incalling;

	@ViewMapping(ID = R.id.name)
	public TextView name;

	@ViewMapping(ID = R.id.phoneNumber)
	public TextView phonenumber;

	private SimulateComm simu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = getLayoutInflater().inflate(
				R.layout.activity_simulate_call, null);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = android.view.WindowManager.LayoutParams.TYPE_PHONE;
		params.format = PixelFormat.RGBA_8888;
		params.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		params.gravity = Gravity.LEFT | Gravity.TOP;
		view.setLayoutParams(params);
		
		setContentView(view);

		simu = (SimulateComm) getIntent().getSerializableExtra("simu");

		initUI();

		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this, RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 1000, 2000, 1000, 2000 }; // OFF/ON/OFF/ON...
			vibrator.vibrate(pattern, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		hangup.setOnClickListener(this);
		dial.setOnClickListener(this);
		pickup.setOnClickListener(this);

		if (simu != null) {
			name.setText(simu.getName());
			phonenumber.setText(simu.getPhonenumber() + "");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					this.mMediaPlayer.stop();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (null != vibrator) {
				vibrator.cancel();
				vibrator = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.setClass(this, LightScreenService.class);
		stopService(intent);
	}

	@Override
	public void onClick(View v) {
		if (v == hangup) {
			finish();
		}
		if (v == pickup) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), SimulateIncallActivity.class);
			intent.putExtra("simu", simu);
			startActivity(intent);
			finish();
		}
	}
}
