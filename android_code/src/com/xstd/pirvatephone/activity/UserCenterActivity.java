package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.id;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UserCenterActivity extends Activity {

	private Button btn_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		
		initView();
	}

	private void initView() {
		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserCenterActivity.this,
						SettingActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_center, menu);
		return true;
	}

}
