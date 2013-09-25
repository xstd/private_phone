package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.adapter.SmsDetailAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SmsDetailActivity extends Activity {

	private Button back;
	private ListView listview;
	private String number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_detail);

		number = getIntent().getStringExtra("Number");

		Tools.logSh("Number====" + number);

		initView();
	}

	private void initView() {
		back = (Button) findViewById(R.id.sms_detail_title_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		listview = (ListView) findViewById(R.id.sms_detail_listview);

		SmsDetailDao smsDetailDao = SmsDetailDaoUtils
				.getSmsDetailDao(getApplicationContext());
		SQLiteDatabase database = smsDetailDao.getDatabase();
		Cursor smsDetailCursor = database.query("SMS_DETAIL", null,
				"phone_number=?", new String[] { number }, null, null, null);

		listview.setAdapter(new SmsDetailAdapter(getApplicationContext(),
				smsDetailCursor));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sms_detail, menu);
		return true;
	}

}
