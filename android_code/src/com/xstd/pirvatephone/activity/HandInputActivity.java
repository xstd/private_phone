package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;

import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HandInputActivity extends Activity {

	private Button bt_cancle;
	private Button bt_back;
	private EditText et_name;
	private EditText et_phone;
	private Button bt_sure;
	
	private ContactInfo contactInfo = new ContactInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hand_input);
		
		initView();
	}

	private void initView() {
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		
		
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//若为空
				String name = et_name.getText().toString();
				String phone = et_phone.getText().toString();
				
				if(TextUtils.isEmpty(phone)){
					Toast.makeText(getApplicationContext(), "请填写正确的电话", Toast.LENGTH_SHORT).show();
				}else{//不为空,添加到我们数据库。
					ContactInfoDao contactInfoDao = ContactInfoDaoUtils.getContactInfoDao(getApplicationContext());
					
					if(name==null){
						name = "";
					}
					contactInfo.setPhone_number(phone);
					contactInfo.setDisplay_name(name);
							
					contactInfoDao.insert(contactInfo);		
					Toast.makeText(getApplicationContext(), "想联系人数据库中添加了一条新数据", Toast.LENGTH_SHORT).show();
					
					//返回到联系人界面
					finish();
				}
			}
		});
		
		
		bt_cancle = (Button) findViewById(R.id.bt_cancle);
		bt_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
