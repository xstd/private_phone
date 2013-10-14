package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.service.ProxyServiceDaoUtils;
import com.xstd.pirvatephone.dao.service.ProxyTalk;
import com.xstd.pirvatephone.dao.service.ProxyTalkDao;
import com.xstd.privatephone.tools.Toasts;
import com.xstd.privatephone.view.MyTimePickerDialog;

/**
 * Created with IntelliJ IDEA. User: michael Date: 13-9-26 Time: PM2:29 To
 * change this template use File | Settings | File Templates.
 */
public class ProxyTalkActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private static final int REQUEST_CONTACTS_CODE = 0;

	private static final String TAG = "ProxyTalkActivity";

	@ViewMapping(ID = R.id.ok)
	public Button mOK;

	@ViewMapping(ID = R.id.sms_check)
	public CheckBox mSMSCheck;

	@ViewMapping(ID = R.id.sms)
	public TextView mSMSContacts;

	@ViewMapping(ID = R.id.weixin_check)
	public CheckBox mWeixinCheck;

	@ViewMapping(ID = R.id.weixin_accout)
	public TextView mWeixinAccount;

	@ViewMapping(ID = R.id.other_check)
	public CheckBox mOtherCheck;

	@ViewMapping(ID = R.id.self_check)
	public CheckBox mSelfCheck;

	@ViewMapping(ID = R.id.begin)
	public Button mBegin;

	@ViewMapping(ID = R.id.end)
	public Button mEnd;

	@ViewMapping(ID = R.id.default_text)
	public EditText mDefaultEditText;

	private boolean mIsSmsChecked;
	private boolean mIsWeixinChecked;
	private boolean mIsSelfChecked;
	private boolean mIsOtherChecked;

	private String mDefaultString;

	private String mWXName;
	private String mWXPasswd;

	private Date starttime;

	private Date endtime;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_proxy_talk);
		init();
	}

	private void init() {
		ViewMapUtil.viewMapping(this, this.getWindow());

		mOK.setOnClickListener(this);
		mBegin.setOnClickListener(this);
		mEnd.setOnClickListener(this);

		mSMSCheck.setOnCheckedChangeListener(this);
		mWeixinCheck.setOnCheckedChangeListener(this);
		mOtherCheck.setOnCheckedChangeListener(this);
		mSelfCheck.setOnCheckedChangeListener(this);
		mOtherCheck.setChecked(true);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ok:
			if (checkInfo()) {
				ProxyTalkDao dao = ProxyServiceDaoUtils.getProxyTalkDao(getApplicationContext());
				ProxyTalk entity = new ProxyTalk(null, null, mWXName, mWXPasswd, mDefaultString, starttime, endtime, mOtherCheck.isChecked() ? 0 : 1);
				dao.insert(entity);
				finish();
			}
			break;
		case R.id.begin:
			showTimeDialog(mBegin);
			break;
		case R.id.end:
			showTimeDialog(mEnd);
			break;
		}
	}

	/**
	 * 检查用户输入数据
	 * 
	 * @return
	 */
	private boolean checkInfo() {
		if (!mSMSCheck.isChecked() && !mWeixinCheck.isChecked()) {
			Toasts.getInstance(this).show(R.string.rs_no_select_proxy, 0);
			return false;
		}
		if (endtime.getTime() <= System.currentTimeMillis() || endtime.getTime() <= starttime.getTime()) {
			Toasts.getInstance(this).show(R.string.rs_error_time, 0);
			return false;
		}
		mDefaultString = mDefaultEditText.getText().toString().trim();
		if (TextUtils.isEmpty(mDefaultString)) {
			Toasts.getInstance(this).show(R.string.rs_no_talk_content, 0);
			return false;
		}
		return true;
	}

	private void showTimeDialog(final Button bt) {
		long time = System.currentTimeMillis() + 60 * 1000;
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
				if (bt == mBegin) {
					bt.setText(getString(R.string.proxy_time_begin) + DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
					starttime = calendar.getTime();
				}
				if (bt == mEnd) {
					bt.setText(getString(R.string.proxy_time_end) + DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
					endtime = calendar.getTime();
				}
			}
		}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
		timePickerDialog.show();
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		switch (compoundButton.getId()) {
		case R.id.sms_check:
			if (!mIsSmsChecked && b) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CopyContactsListMultiple.class);
				startActivityForResult(intent, REQUEST_CONTACTS_CODE);
			}
			if (!b) {
				mSMSContacts.setText("");
			}
			mIsSmsChecked = b;
			break;
		case R.id.weixin_check:
			if (!mIsWeixinChecked && b) {
				showWeiXinAccountDialog();
			}
			if (!b) {
				mWeixinAccount.setText("");
			}
			mIsWeixinChecked = b;
			break;
		case R.id.self_check:
			mIsSelfChecked = b;
			if (mIsSelfChecked) {
				mOtherCheck.setChecked(false);
			}
			break;
		case R.id.other_check:
			mIsOtherChecked = b;
			if (mIsOtherChecked) {
				mSelfCheck.setChecked(false);
			}
			break;
		}
	}

	private ArrayList<String> smsContacts;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CONTACTS_CODE) {
			smsContacts = data.getStringArrayListExtra("GET_CONTACT");
			if (smsContacts != null && smsContacts.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (String str : smsContacts) {
					sb.append(str.substring(str.lastIndexOf("\n") + 1) + "、");
				}
				sb.deleteCharAt(sb.length() - 1);
				mSMSContacts.setText(sb);
				return;
			}
		}
		mSMSCheck.setChecked(false);

	}

	private void showWeiXinAccountDialog() {
		View view = getLayoutInflater().inflate(R.layout.weixin_account, null);
		final EditText name = (EditText) view.findViewById(R.id.weixin_account);
		final EditText pwd = (EditText) view.findViewById(R.id.weixin_pwd);
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.proxy_weixin_title).setView(view).setPositiveButton(R.string.enter_btn_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mWXName = name.getText().toString();
				mWXPasswd = pwd.getText().toString();

				if (TextUtils.isEmpty(mWXName) || TextUtils.isEmpty(mWXPasswd)) {
					mWeixinCheck.setChecked(false);
					mWeixinAccount.setText("");
				} else {
					mWeixinCheck.setChecked(true);
					mWeixinAccount.setText(mWXName);
				}
			}
		}).setNegativeButton(R.string.enter_btn_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mWeixinCheck.setChecked(false);
				mWeixinAccount.setText("");
			}
		}).setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					mWeixinCheck.setChecked(false);
					mWeixinAccount.setText("");
				}
				return false;
			}
		}).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
}