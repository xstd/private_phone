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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.adapter.SmsDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class SmsDetailActivity extends BaseActivity {

	private ListView listview;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;
	private Button btn_send;
	private EditText send_content;
	private Cursor smsDetailCursor;
	private SmsDetailAdapter smsDetailAdapter;
	private String name = "";
	private String number = "";
	private RelativeLayout sms_detail_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_detail);

		name = getIntent().getStringExtra("Name");
		Tools.logSh("Name====" + name);
		//查询该name对应的number
		getContactNumber();

		initData();
		initView();
	}
	
	private void getContactNumber(){
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils.getContactInfoDao(this);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		Cursor query = contactDatabase.query(ContactInfoDao.TABLENAME, null, ContactInfoDao.Properties.Display_name.columnName+"=?", new String[]{name}, null, null, null);
		if(query!=null && query.getCount()>0){
			while(query.moveToNext()){
				number = query.getString(query.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
			}
			query.close();
		}
		
		Tools.logSh("Number====" + number);
	}

	private void initView() {
		//title
		sms_detail_title = (RelativeLayout) findViewById(R.id.sms_detail_title);
		btn_back = (Button)sms_detail_title.findViewById(R.id.btn_back);
		btn_edit = (Button) sms_detail_title.findViewById(R.id.btn_edit);
		tv_title = (TextView) sms_detail_title.findViewById(R.id.tv_title);
		tv_title.setText(number);
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
					//隐藏软键盘
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(SmsDetailActivity.this
                                    .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
					send_content.setText("");
					//让textview失去焦点
					send_content.clearFocus();

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
				new String[] { number }, null, null, "date desc");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
				
				R.menu.sms_detail, menu);
		return true;
	}

	public void sendSms(String content) {
		Tools.logSh("短信粉条发送::" + content+":::"+number);
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
				Tools.logSh("短信粉条发送::" + str);
				Toast.makeText(SmsDetailActivity.this, "发送成功！",
						Toast.LENGTH_LONG).show();
			}
		} else {
			smsManager.sendTextMessage(number, null, content, sentIntent, null);
			Tools.logSh("短信发送::" + content);

			Toast.makeText(SmsDetailActivity.this, "发送成功！", Toast.LENGTH_LONG)
					.show();
			smsDetailCursor.requery();
			smsDetailAdapter.notifyDataSetChanged();
		}
	}
}
