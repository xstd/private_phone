package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.adapter.PhoneDetailAdapter;
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

public class PhoneDetailActivity extends BaseActivity {

	private Button back;
	private ListView listview;
	private String number;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_detail);
		
		number = getIntent().getStringExtra("Number");
		
		
		
		initView();
	}

	private void initView() {
		back = (Button) findViewById(R.id.phone_detail_title_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		

		listview = (ListView) findViewById(R.id.phone_detail_listview);

		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils.getPhoneDetailDao(getApplicationContext());
		SQLiteDatabase database = phoneDetailDao.getDatabase();
		Cursor phoneDetailCursor = database.query("PHONE_DETAIL", null,
				PhoneDetailDao.Properties.Phone_number.columnName+"=?", new String[] { number }, null, null, null);
		
		Tools.logSh("phoneDetailCursor的长度为："+phoneDetailCursor.getCount());
		
		listview.setAdapter(new PhoneDetailAdapter(getApplicationContext(),
				phoneDetailCursor));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_detail, menu);
		return true;
	}

}
