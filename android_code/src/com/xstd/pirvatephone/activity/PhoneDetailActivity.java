package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.utils.MakeCallUtils;
import com.xstd.privatephone.adapter.PhoneDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class PhoneDetailActivity extends BaseActivity {

	private Button back;
	private ListView listview;
	private String number;
	private String name;
	private LinearLayout phone_detail_title;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;
	private Button btn_dial;
	private Button btn_send_sms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_detail);

		name = getIntent().getStringExtra("Name");
		number = getIntent().getStringExtra("Number");


		initView();
	}

	private void initView() {
		//title
		phone_detail_title = (LinearLayout) findViewById(R.id.phone_detail_title);
		btn_back = (Button) phone_detail_title.findViewById(R.id.btn_back);
		btn_edit = (Button) phone_detail_title.findViewById(R.id.btn_edit);
		btn_edit.setVisibility(View.GONE);
		tv_title = (TextView) phone_detail_title.findViewById(R.id.tv_title);
		tv_title.setText(number);
		
		//bottom
		btn_dial = (Button) findViewById(R.id.btn_dial);
		btn_send_sms = (Button) findViewById(R.id.btn_send_sms);

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listview = (ListView) findViewById(R.id.phone_detail_listview);

		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
				.getPhoneDetailDao(getApplicationContext());
		SQLiteDatabase database = phoneDetailDao.getDatabase();
		Cursor phoneDetailCursor = database.query("PHONE_DETAIL", null,
				PhoneDetailDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, "date desc");

		Tools.logSh("phoneDetailCursor的长度为：" + phoneDetailCursor.getCount());

		listview.setAdapter(new PhoneDetailAdapter(getApplicationContext(),
				phoneDetailCursor));
		
		btn_dial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MakeCallUtils.makeCall(PhoneDetailActivity.this, number);
				
			}
		});
		
		btn_send_sms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent smsDetailIntent = new Intent(PhoneDetailActivity.this,
						SmsDetailActivity.class);
				// 姓名带过去
				Tools.logSh("Name==" + name);
				smsDetailIntent.putExtra("Name", name);
				startActivity(smsDetailIntent);;
				
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_detail, menu);
		return true;
	}

}
