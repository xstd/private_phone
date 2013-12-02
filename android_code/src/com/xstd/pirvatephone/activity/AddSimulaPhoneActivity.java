package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.pirvatephone.module.SimpleContact;
import com.xstd.pirvatephone.receiver.SimulateSendPhoneReceiver;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.view.MyDatePickerDialog;
import com.xstd.privatephone.view.MyTimePickerDialog;

public class AddSimulaPhoneActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "AddSimulaPhoneActivity";

	public static final int CHOOSE_CONTACT = 1;

	public static final int REQUES_REVEIVER_CODE = 3;

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

	@ViewMapping(ID = R.id.phoneDate)
	public ViewGroup phoneDate;

	@ViewMapping(ID = R.id.phoneTime)
	public ViewGroup phoneTime;

	@ViewMapping(ID = R.id.tv_date)
	public TextView tv_date;

	@ViewMapping(ID = R.id.tv_time)
	public TextView tv_time;

	private Calendar calendar;

	private long time;

	private SimpleContact contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_simula_phone);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_title_text.setText(R.string.simula_add_phone);
		ll_toools.setBackgroundResource(R.drawable.btn_simulate_create);
		ll_toools.setVisibility(View.VISIBLE);
		ll_return_btn.setOnClickListener(this);
		ll_toools.setOnClickListener(this);
		chooseContact.setOnClickListener(this);
		phoneDate.setOnClickListener(this);
		phoneTime.setOnClickListener(this);

		time = System.currentTimeMillis() + 60 * 1000;
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		updateDateTime();
	}

	private void updateDateTime() {
		tv_date.setText(getString(R.string.s_simulate_date)
				+ DateFormat.getDateInstance().format(calendar.getTime()));
		tv_time.setText(getString(R.string.s_simulate_time)
				+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
						calendar.getTime()));
	}

	@Override
	public void onClick(View v) {
		if (v == ll_return_btn) {
			finish();
		} else if (v == chooseContact) {
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, CHOOSE_CONTACT);
		} else if (v == phoneDate) {
			showDatePicker();
		} else if (v == phoneTime) {
			showTimePicker();
		} else if (v == ll_toools) {
			checkData();
		}

	}

	private void checkData() {
		String phone = phoneNumber.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, R.string.s_phonenumber_empty,
					Toast.LENGTH_LONG).show();
			return;
		} else if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			Toast.makeText(this, R.string.s_not_passby, Toast.LENGTH_LONG)
					.show();
			return;
		}
		SimulateComm entity = new SimulateComm(null, contact == null ? null
				: contact.getName(), phone, calendar.getTime(), null,
				SimulaCommActivity.SIMULATE_PHONE);
		SimulateDaoUtils.getSimulateDao(this).insert(entity);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(),
				SimulateSendPhoneReceiver.class);
		intent.putExtra("simu", entity);
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), REQUES_REVEIVER_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtMillis = calendar.getTimeInMillis()
				- System.currentTimeMillis() + SystemClock.elapsedRealtime();
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis,
				pendIntent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_CONTACT && resultCode == RESULT_OK) {
			contact = ContactsUtils.readContact(this, data.getData());
			phoneNumber.setText(contact.getPhone());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showTimePicker() {
		MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this,
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
