package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.SimpleContact;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.tools.Toasts;
import com.xstd.privatephone.view.MyDatePickerDialog;
import com.xstd.privatephone.view.MyTimePickerDialog;

/**
 * Created by Chrain on 13-10-31.
 */
public class AddCommonSmsActivity extends BaseActivity implements
		View.OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public ImageButton ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.ll_toools)
	public ImageButton ll_toools;

	@ViewMapping(ID = R.id.phoneNumber)
	public EditText phoneNumber;

	@ViewMapping(ID = R.id.chooseContact)
	public ImageView chooseContact;

	@ViewMapping(ID = R.id.smsType)
	public ViewGroup smsType;

	@ViewMapping(ID = R.id.smsDate)
	public ViewGroup smsDate;

	@ViewMapping(ID = R.id.smsTime)
	public ViewGroup smsTime;

	@ViewMapping(ID = R.id.tv_type)
	public TextView tv_type;

	@ViewMapping(ID = R.id.tv_date)
	public TextView tv_date;

	@ViewMapping(ID = R.id.tv_time)
	public TextView tv_time;

	@ViewMapping(ID = R.id.smsContent)
	public EditText smsContent;

	private SimpleContact contact;

	private Calendar calendar;

	private long time;

	public static final int SEND = 1;

	public static final int RECEIVE = 0;

	private int currentType = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_common_sms);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		ll_toools.setOnClickListener(this);
		chooseContact.setOnClickListener(this);
		smsType.setOnClickListener(this);
		smsDate.setOnClickListener(this);
		smsTime.setOnClickListener(this);

		ll_title_text.setText(getString(R.string.rs_create_system_sms));
		ll_toools.setBackgroundResource(R.drawable.btn_simulate_create);
		ll_toools.setVisibility(View.VISIBLE);

		time = System.currentTimeMillis();
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		updateDateTime();
	}

	private void updateDateTime() {
		tv_date.setText(getString(R.string.s_happen_date)
				+ DateFormat.getDateInstance().format(calendar.getTime()));
		tv_time.setText(getString(R.string.s_happen_time)
				+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
						calendar.getTime()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;
		case R.id.chooseContact:
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent,
					AddSimulaPhoneActivity.CHOOSE_CONTACT);
			break;
		case R.id.smsType:
			showTypeDialog();
			break;
		case R.id.smsDate:
			showDatePicker();
			break;
		case R.id.smsTime:
			showTimePicker();
			break;
		case R.id.ll_toools:
			validate();
			break;
		}
	}

	private void validate() {
		if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
			Toasts.getInstance(this).show(R.string.s_phonenumber_empty, 1);
			return;
		}
		if (currentType == -1) {
			Toasts.getInstance(this).show(R.string.rs_error_sms_type, 1);
			return;
		}
		long timeInMillis = calendar.getTimeInMillis();
		if (timeInMillis > System.currentTimeMillis()) {
			Toasts.getInstance(this).show(R.string.rs_error_datetime, 1);
			return;
		}
		if (TextUtils.isEmpty(smsContent.getText().toString().trim())) {
			Toasts.getInstance(this).show(R.string.s_empty_sms_content, 1);
			return;
		}
		AddSms();
		finish();
	}

	private void AddSms() {
		Uri uri = Uri.parse("content://sms");
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber.getText().toString().trim());
		values.put("date", calendar.getTimeInMillis());
		values.put("read", 0);
		values.put("type", currentType);
		values.put("body", smsContent.getText().toString().trim());
		getContentResolver().insert(uri, values);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AddSimulaPhoneActivity.CHOOSE_CONTACT
				&& resultCode == RESULT_OK) {
			contact = ContactsUtils.readContact(this, data.getData());
			phoneNumber.setText(contact.getPhone());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showTypeDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.rs_input_sms_type)
				.setItems(R.array.sms_type,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case SEND:
									currentType = SEND;
									break;
								case RECEIVE:
									currentType = RECEIVE;
									break;
								}
								tv_type.setText(getResources().getStringArray(
										R.array.sms_type)[currentType]);
							}
						}).create();
		dialog.show();
	}

	private void showTimePicker() {
		MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						calendar.set(calendar.get(Calendar.YEAR),
								calendar.get(Calendar.MONTH),
								calendar.get(Calendar.DAY_OF_MONTH), hourOfDay,
								minute);
						updateDateTime();
					}
				}, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), false);
		timePickerDialog.show();
	}

	private void showDatePicker() {
		MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						calendar.set(year, monthOfYear, dayOfMonth);
						updateDateTime();
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}
}
