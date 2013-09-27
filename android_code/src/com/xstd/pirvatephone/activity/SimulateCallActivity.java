package com.xstd.pirvatephone.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simulate_call);

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
			long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...
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
	}

	@Override
	public void onClick(View v) {
		if (v == hangup) {
			finish();
		}
	}
}
