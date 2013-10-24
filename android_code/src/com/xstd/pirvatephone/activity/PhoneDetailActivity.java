package com.xstd.pirvatephone.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.privatephone.adapter.PhoneDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class PhoneDetailActivity extends BaseActivity {

	private Button back;
	private ListView listview;
	private String number;
	private String name;
	private RelativeLayout phone_detail_title;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_detail);

		name = getIntent().getStringExtra("Name");

		getContactNumber();

		initView();
	}

	private void getContactNumber() {
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(this);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		Cursor query = contactDatabase.query(ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Display_name.columnName + "=?",
				new String[] { name }, null, null, null);
		if (query != null && query.getCount() > 0) {
			while (query.moveToNext()) {
				number = query
						.getString(query
								.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
			}
			query.close();
		}

		Tools.logSh("Number====" + number);
	}

	private void initView() {
		phone_detail_title = (RelativeLayout) findViewById(R.id.phone_detail_title);
		btn_back = (Button) phone_detail_title.findViewById(R.id.btn_back);
		btn_edit = (Button) phone_detail_title.findViewById(R.id.btn_edit);
		btn_edit.setVisibility(View.GONE);
		tv_title = (TextView) phone_detail_title.findViewById(R.id.tv_title);
		tv_title.setText(number);

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_detail, menu);
		return true;
	}

}
