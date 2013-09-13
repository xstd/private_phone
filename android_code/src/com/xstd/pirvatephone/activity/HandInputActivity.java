package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HandInputActivity extends Activity {

	private Button bt_cancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hand_input);
		
		initView();
	}

	private void initView() {
		bt_cancle = (Button) findViewById(R.id.bt_cancle);
		
		bt_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hand_input, menu);
		return true;
	}

}
