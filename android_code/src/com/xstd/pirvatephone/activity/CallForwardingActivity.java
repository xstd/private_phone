package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.module.SimpleContact;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.tools.Tools;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CallForwardingActivity extends Activity {

	private static final int CHOOSE_CONTACT = 1;

	public static final String FORWARDING_STATUS = "FORWARDING_STATUS";
	public static final String FORWARD_CONTACT = "FORWARD_CONTACT";
	public static final String FORWARD_TYPE = "FORWARD_TYPE";
	public static int PICK_REQUEST = 0;
	public static final String SETTING_INFO = "setting_infos";
	private int FORWARDING_TYPE = 2;// 转移类型
	private ArrayAdapter<String> adapter;
	private Button btn_close = null;
	private Button btn_forwarding = null;
	private Button ib_find_contact1;
	private TelephonyManager manager;
	private EditText numberEditText = null;
	ArrayList<String> numberList = null;
	private int phoneType = 1;// 
	private boolean isCallForwarding;// 是否开启
	SharedPreferences settings;
	private Spinner sp_selectCmd;

	private SimpleContact contact;
	private String number;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_forwarding);

		sp = getSharedPreferences("isCallForwarding", 0);
		
		initView();
		
		initData();
	}

	private void initData() {
		
	}
	
	private void stopCallForwarding(String number){
		//取消来电转移
		
		Uri uri = getCancelUri(number);
				
		Intent localIntent2 = new Intent("android.intent.action.CALL",uri);
		localIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent2);
	}
	
	private Uri getCancelUri(String number) {
		this.manager = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
		  phoneType = this.manager.getPhoneType();
		  if(phoneType==TelephonyManager.PHONE_TYPE_NONE){
		  }else if(phoneType==TelephonyManager.PHONE_TYPE_GSM){
			  return Uri.fromParts("tel", "##67#", null);
		  }else if(phoneType==TelephonyManager.PHONE_TYPE_CDMA){
			  return Uri.fromParts("tel", "*900", null);
		  }
		return null;
	}

	private void startCallForwarding(String number){
		//开启来电转移
		
		Uri uri = getUri(number);
				
		Intent localIntent2 = new Intent("android.intent.action.CALL",uri);
		localIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent2);
	}
	
	private Uri getUri(String number) {
		this.manager = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
		  phoneType = this.manager.getPhoneType();
		  if(phoneType==TelephonyManager.PHONE_TYPE_NONE){
		  }else if(phoneType==TelephonyManager.PHONE_TYPE_GSM){
			  return Uri.fromParts("tel", "**67*"+number+"#", null);
		  }else if(phoneType==TelephonyManager.PHONE_TYPE_CDMA){
			  return Uri.fromParts("tel", "*90"+number, null);
		  }
		return null;
	}

	private void initView() {
		numberEditText = (EditText) findViewById(R.id.callnumber);
		ib_find_contact1 = (Button) findViewById(R.id.ib_find_contact1);
		sp_selectCmd = (Spinner) findViewById(R.id.sp_selectCmd);
		btn_forwarding = (Button) findViewById(R.id.forwarding);
		btn_close = (Button) findViewById(R.id.close);

		isCallForwarding = sp.getBoolean("isCallForwarding", false);
		if(isCallForwarding){
			btn_forwarding.setText("关闭转移服务");
			Tools.logSh("isCallForwarding==="+isCallForwarding);
		}else{
			btn_forwarding.setText("开启转移服务");
			Tools.logSh("isCallForwarding==="+isCallForwarding);
		}

		
		String[] arrayOfString = { "转移所有来电", "正在接听时转移", "无人接听时转移", "关机与无信号时转移" };

		adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
				arrayOfString);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		sp_selectCmd.setAdapter(this.adapter);
		sp_selectCmd.setOnItemSelectedListener(new SpinnerSelectedListener());
		sp_selectCmd.setSelection(this.FORWARDING_TYPE);
		sp_selectCmd.setVisibility(0);

		ib_find_contact1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				CallForwardingActivity.this.getContact();
			}
		});

		btn_forwarding.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				number = numberEditText.getText().toString().trim();

				if(isCallForwarding){
					isCallForwarding=false;
					 sp.edit().putBoolean("isCallForwarding", isCallForwarding).commit();  

					stopCallForwarding(number);
					btn_forwarding.setText("开启转移服务");
					Tools.logSh("isCallForwarding==="+isCallForwarding);
					return ;
				}else{
					
					if(TextUtils.isEmpty(number)){
						Toast.makeText(CallForwardingActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					isCallForwarding=true;
					 sp.edit().putBoolean("isCallForwarding", isCallForwarding).commit();  
					
					startCallForwarding(number);
					btn_forwarding.setText("关闭转移服务");
					Tools.logSh("isCallForwarding==="+isCallForwarding);
					return ;
				}
				
			}
		});

		btn_close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				finish();
			}
		});

	}

	public void getContact() {
		startActivityForResult(new Intent("android.intent.action.PICK",
				ContactsContract.Contacts.CONTENT_URI), CHOOSE_CONTACT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_forwarding, menu);
		return true;
	}

	class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		SpinnerSelectedListener() {
		}

		public void onItemSelected(AdapterView<?> paramAdapterView,
				View paramView, int paramInt, long paramLong) {
			CallForwardingActivity.this.FORWARDING_TYPE = paramInt;
		}

		public void onNothingSelected(AdapterView<?> paramAdapterView) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_CONTACT && resultCode == RESULT_OK) {
			contact = ContactsUtils.readContact(this, data.getData());
			Tools.logSh("phone==" + contact.getPhone());
			numberEditText.setText(contact.getPhone());
		}
	}

}
