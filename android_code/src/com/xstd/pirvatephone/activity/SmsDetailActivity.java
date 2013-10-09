package com.xstd.pirvatephone.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.adapter.SmsDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class SmsDetailActivity extends BaseActivity {

	private Button back;
	private ListView listview;
	private String number;
	private Button sms_btn_recover;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_detail);

		number = getIntent().getStringExtra("Number");

		Tools.logSh("Number====" + number);

		initView();
	}

	private void initView() {
		back = (Button) findViewById(R.id.sms_detail_title_back);
		/*sms_btn_recover = (Button) findViewById(R.id.bt_recover);
		sms_btn_recover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "hello", 0).show();
			}
		});*/
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
