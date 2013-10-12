package com.xstd.pirvatephone.activity;

import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.adapter.SmsDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class SmsDetailActivity extends BaseActivity {

	private ListView listview;
	private String number;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;
	private Button btn_send;
	private EditText send_content;
	private Cursor smsDetailCursor;
	private SmsDetailAdapter smsDetailAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_detail);

		number = getIntent().getStringExtra("Number");

		Tools.logSh("Number====" + number);

		initData();
		initView();
	}

	private void initView() {
		// title
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("隐私短信");
		btn_edit.setVisibility(View.GONE);
		// content
		listview = (ListView) findViewById(R.id.sms_detail_listview);
		// bottom
		send_content = (EditText) findViewById(R.id.et_send_content);
		btn_send = (Button) findViewById(R.id.btn_send);

		smsDetailAdapter = new SmsDetailAdapter(getApplicationContext(),
				smsDetailCursor);
		listview.setAdapter(smsDetailAdapter);

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String sms_content = send_content.getText().toString();
				if (!TextUtils.isEmpty(sms_content)) {
					sendSms(sms_content);
				} else {
					Toast.makeText(SmsDetailActivity.this, "短信不能为空！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void initData() {
		// TODO Auto-generated method stub
		SmsDetailDao smsDetailDao = SmsDetailDaoUtils
				.getSmsDetailDao(getApplicationContext());
		SQLiteDatabase database = smsDetailDao.getDatabase();
		smsDetailCursor = database.query("SMS_DETAIL", null, "phone_number=?",
				new String[] { number }, null, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sms_detail, menu);
		return true;
	}

	public void sendSms(String content) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(
				SmsDetailActivity.this, 0, new Intent(), 0);

		if (content.length() >= 70) {
			// 短信字数大于70，自动分条
			List<String> ms = smsManager.divideMessage(content);

			for (String str : ms) {
				// 短信发送
				smsManager.sendTextMessage(number, null, str, sentIntent, null);
				smsDetailCursor.requery();
				smsDetailAdapter.notifyDataSetChanged();
			}
		} else {
			smsManager.sendTextMessage(number, null, content, sentIntent, null);
			smsDetailCursor.requery();
			smsDetailAdapter.notifyDataSetChanged();
		}

		Toast.makeText(SmsDetailActivity.this, "发送成功！", Toast.LENGTH_LONG)
				.show();
	}
}
