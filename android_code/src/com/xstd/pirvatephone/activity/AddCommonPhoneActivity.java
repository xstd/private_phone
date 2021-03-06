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
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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
public class AddCommonPhoneActivity extends BaseActivity implements
		View.OnClickListener, SeekBar.OnSeekBarChangeListener {

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

	@ViewMapping(ID = R.id.phoneType)
	public ViewGroup phoneType;

	@ViewMapping(ID = R.id.phoneDate)
	public ViewGroup phoneDate;

	@ViewMapping(ID = R.id.phoneTime)
	public ViewGroup phoneTime;

	@ViewMapping(ID = R.id.tv_type)
	public TextView tv_type;

	@ViewMapping(ID = R.id.tv_date)
	public TextView tv_date;

	@ViewMapping(ID = R.id.tv_time)
	public TextView tv_time;

	@ViewMapping(ID = R.id.during)
	public TextView during;

	@ViewMapping(ID = R.id.sb)
	public SeekBar sb;

	private SimpleContact contact;

	private Calendar calendar;

	private long time;

	private int currentType = -1;

	private int duringTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_common_phone);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_title_text.setText(R.string.rs_create_system_phone);
		ll_toools.setBackgroundResource(R.drawable.btn_simulate_create);
		ll_toools.setVisibility(View.VISIBLE);
		during.setText(getString(R.string.rs_calling_during) + "0秒");

		ll_return_btn.setOnClickListener(this);
		chooseContact.setOnClickListener(this);
		phoneType.setOnClickListener(this);
		phoneDate.setOnClickListener(this);
		phoneTime.setOnClickListener(this);
		ll_toools.setOnClickListener(this);
		sb.setOnSeekBarChangeListener(this);

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
		case R.id.phoneType:
			showTypeDialog();
			break;
		case R.id.phoneDate:
			showDatePicker();
			break;
		case R.id.phoneTime:
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
			Toasts.getInstance(this).show(R.string.rs_error_type, 1);
			return;
		}
		long timeInMillis = calendar.getTimeInMillis();
		if (timeInMillis > System.currentTimeMillis()) {
			Toasts.getInstance(this).show(R.string.rs_error_datetime, 1);
			return;
		}
		AddCallLog();
		finish();
	}

	private void AddCallLog() {
		ContentValues values = new ContentValues();
		values.put(CallLog.Calls.NUMBER, phoneNumber.getText().toString()
				.trim());
		int phoneType = CallLog.Calls.OUTGOING_TYPE;
		switch (currentType) {
		case CallLog.Calls.OUTGOING_TYPE:
			phoneType = CallLog.Calls.OUTGOING_TYPE;
			break;
		case CallLog.Calls.INCOMING_TYPE:
			phoneType = CallLog.Calls.INCOMING_TYPE;
			break;
		case CallLog.Calls.MISSED_TYPE:
			phoneType = CallLog.Calls.MISSED_TYPE;
			break;
		}
		values.put(CallLog.Calls.TYPE, phoneType);
		values.put(CallLog.Calls.DATE, calendar.getTimeInMillis());
		values.put(CallLog.Calls.DURATION, duringTime);
		values.put(CallLog.Calls.NEW, 0);
		getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		duringTime = progress;
		during.setText(getString(R.string.rs_calling_during)
				+ formatTime(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private String formatTime(int progress) {
		if (progress == 3600)
			return "1小时";
		int m = progress / 60;
		int s = progress % 60;
		return m + "分钟" + s + "秒";
	}

	private void showTypeDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.rs_select_phone_type)
				.setItems(R.array.phone_type,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which + 1) {
								case CallLog.Calls.INCOMING_TYPE:
									currentType = CallLog.Calls.INCOMING_TYPE;
									break;
								case CallLog.Calls.OUTGOING_TYPE:
									currentType = CallLog.Calls.OUTGOING_TYPE;
									break;
								case CallLog.Calls.MISSED_TYPE:
									currentType = CallLog.Calls.MISSED_TYPE;
									break;
								}
								tv_type.setText(getResources().getStringArray(
										R.array.phone_type)[which]);
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
