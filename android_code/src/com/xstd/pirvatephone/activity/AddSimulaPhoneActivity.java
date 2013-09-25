package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.view.MyDatePickerDialog;

public class AddSimulaPhoneActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "AddSimulaPhoneActivity";

	@ViewMapping(ID = R.id.back)
	public ImageView back;

	@ViewMapping(ID = R.id.phoneNumber)
	public EditText phoneNumber;

	@ViewMapping(ID = R.id.chooseContact)
	public ImageView chooseContact;

	@ViewMapping(ID = R.id.phoneDate)
	public Button phoneDate;

	@ViewMapping(ID = R.id.phoneTime)
	public Button phoneTime;

	@ViewMapping(ID = R.id.save)
	public Button save;

	private Calendar calendar;

	private long time;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_simula_phone);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		back.setOnClickListener(this);
		chooseContact.setOnClickListener(this);
		phoneDate.setOnClickListener(this);
		phoneTime.setOnClickListener(this);
		save.setOnClickListener(this);

		time = System.currentTimeMillis() + 60 * 1000;
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		updateDateTime();
	}

	private void updateDateTime() {
		phoneDate.setText(getString(R.string.s_simulate_date)
				+ DateFormat.getDateInstance().format(calendar.getTime()));
		phoneTime.setText(getString(R.string.s_simulate_time)
				+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
						calendar.getTime()));
	}

	@Override
	public void onClick(View v) {
		if (v == back) {
			finish();
		} else if (v == chooseContact) {
			Log.w(TAG, "选择联系人");
		} else if (v == phoneDate) {
			showDatePicker();
		} else if (v == phoneTime) {
			showTimePicker();
		} else if (v == save) {

		}

	}

	private void showTimePicker() {
		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

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
				new OnDateSetListener() {

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
