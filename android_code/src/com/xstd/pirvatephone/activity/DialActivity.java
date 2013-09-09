package com.xstd.pirvatephone.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import com.xstd.pirvatephone.R;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-9-9 Time: PM3:11 To change
 * this template use File | Settings | File Templates.
 */
public class DialActivity extends BaseActivity {

	private int delay = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.emergency_dialer);

		goHomeActicity();
	}

	private void goHomeActicity() {
		TimerTask task = new TimerTask() {
			public void run() {
				Intent intent = new Intent(DialActivity.this,
						HomeActivity.class);

				startActivity(intent);
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, delay);
		
		/*timer.cancel();
		task = null;*/
	}
}