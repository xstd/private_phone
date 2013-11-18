package com.xstd.pirvatephone.activity;

import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.BoringLayout.Metrics;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.adapter.SmsDetailAdapter;
import com.xstd.privatephone.tools.Tools;

public class SmsDetailActivity extends BaseActivity {

	private ListView listview;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;
	private RelativeLayout btn_send;
	private EditText send_content;
	private Cursor smsDetailCursor;
	private SmsDetailAdapter smsDetailAdapter;
	private String name = "";
	private String number = "";
	private RelativeLayout sms_detail_title;
	private SmsDetailDao smsDetailDao;
	private SmsRecordDao smsRecordDao;
	private SQLiteDatabase smsDetailDatabase;
	private SQLiteDatabase smsRecordDatabase;
	private Cursor smsRecordCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms_detail);

		name = getIntent().getStringExtra("Name");
		number = getIntent().getStringExtra("Number");
		Tools.logSh("Name====" + name);
		initData();
		initView();
	}


	private void initView() {
		// title
		sms_detail_title = (RelativeLayout) findViewById(R.id.sms_detail_title);
		btn_back = (Button) sms_detail_title.findViewById(R.id.btn_back);
		btn_edit = (Button) sms_detail_title.findViewById(R.id.btn_edit);
		tv_title = (TextView) sms_detail_title.findViewById(R.id.tv_title);
		tv_title.setText(number);
		btn_edit.setVisibility(View.GONE);
		// content
		listview = (ListView) findViewById(R.id.sms_detail_listview);
		// bottom
		send_content = (EditText) findViewById(R.id.et_send_content);
		btn_send = (RelativeLayout) findViewById(R.id.btn_send);

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
					// 隐藏软键盘
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(SmsDetailActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					send_content.setText("");
					// 让textview失去焦点
					send_content.clearFocus();

				} else {
					Toast.makeText(SmsDetailActivity.this, "短信不能为空！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void initData() {
		smsDetailDao = SmsDetailDaoUtils.getSmsDetailDao(this);
		smsDetailDatabase = smsDetailDao.getDatabase();
		smsDetailCursor = smsDetailDatabase.query(SmsDetailDao.TABLENAME, null,
				SmsDetailDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, "date desc");

		smsRecordDao = SmsRecordDaoUtils.getSmsRecordDao(this);
		smsRecordDatabase = smsRecordDao.getDatabase();
		smsRecordCursor = smsRecordDatabase.query(SmsRecordDao.TABLENAME, null,
				SmsDetailDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, null);
	}
	
	@Override
	protected void onDestroy() {
		if(smsDetailCursor!=null){
			smsDetailCursor.close();
		}
		if(smsRecordCursor!=null){
			smsRecordCursor.close();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(

		R.menu.sms_detail, menu);
		return true;
	}

	public void sendSms(String content) {
		Tools.logSh("短信粉条发送::" + content + ":::" + number);
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(
				SmsDetailActivity.this, 0, new Intent(), 0);

		if (content.length() >= 70) {
			// 短信字数大于70，自动分条
			List<String> ms = smsManager.divideMessage(content);

			for (String str : ms) {
				// 短信发送
				smsManager.sendTextMessage(number, null, str, sentIntent, null);

				InsertMessage(number, str);

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
			InsertMessage(number, content);
			smsDetailCursor.requery();
			smsDetailAdapter.notifyDataSetChanged();
		}
	}

	private void InsertMessage(String number, String body) {
		// 向我们的smsDetail数据库插入一条记录，
		SmsDetail smsDetail = new SmsDetail();
		smsDetail.setData(body);
		smsDetail.setDate(System.currentTimeMillis());
		smsDetail.setPhone_number(number);
		smsDetail.setThread_id(2);
		// 向我们的smsRecord数据库插入跟新记录，
		smsDetailDao.insert(smsDetail);

		//判断有该号码的记录没有
		if(smsRecordCursor!=null && smsRecordCursor.getCount()>0){
			while(smsRecordCursor.moveToNext()){
				
				Long _id = smsRecordCursor.getLong(smsRecordCursor.getColumnIndex(SmsRecordDao.Properties.Id.columnName));
				String phone_number = smsRecordCursor.getString(smsRecordCursor
						.getColumnIndex(SmsRecordDao.Properties.Phone_number.columnName));
				int msg_count = smsRecordCursor.getInt(smsRecordCursor.getColumnIndex(SmsRecordDao.Properties.Count.columnName));
				Long lastedContact = smsRecordCursor.getLong(smsRecordCursor
						.getColumnIndex(SmsRecordDao.Properties.Lasted_contact.columnName));
				String lasted_data = smsRecordCursor.getString(smsRecordCursor
						.getColumnIndex(SmsRecordDao.Properties.Lasted_data.columnName));
				
				SmsRecord smsRecord = new SmsRecord();
				smsRecord.setId(_id);
				smsRecord.setPhone_number(phone_number);
				smsRecord.setCount(msg_count+1);
				smsRecord.setLasted_data(lasted_data);
				smsRecord.setLasted_contact(System.currentTimeMillis());
				smsRecordDao.update(smsRecord);
			}
			smsRecordCursor.close();
			return ;
			
		}else{
			SmsRecord smsRecord = new SmsRecord();
			smsRecord.setPhone_number(number);
			smsRecord.setCount(1);
			smsRecord.setLasted_data(body);
			smsRecord.setLasted_contact(System.currentTimeMillis());
			smsRecordDao.insert(smsRecord);
			smsRecordCursor.close();
			return ;
		}
	}
}
