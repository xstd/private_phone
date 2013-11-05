package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.pirvatephone.module.SimpleContact;
import com.xstd.pirvatephone.receiver.SimulateSendSMSReceiver;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.view.MyDatePickerDialog;
import com.xstd.privatephone.view.MyTimePickerDialog;

public class AddSimulaSmsActivity extends BaseActivity implements
		OnClickListener {

	private static final int CHOOSE_CONTACT = 0;
	static final int TIME_DIALOG_ID = 1;
	static final int DATE_DIALOG_ID = 2;
	@SuppressWarnings("unused")
	private static final String TAG = "AddSimulaSmsActivity";
	private static final int REQUES_REVEIVER_CODE = 3;

	@ViewMapping(ID = R.id.back)
	public ImageView back;

	@ViewMapping(ID = R.id.phoneNumber)
	public EditText phoneNumber;

	@ViewMapping(ID = R.id.chooseContact)
	public ImageView chooseContact;

	@ViewMapping(ID = R.id.smsDate)
	public Button smsDate;

	@ViewMapping(ID = R.id.smsTime)
	public Button smsTime;

	@ViewMapping(ID = R.id.smsContent)
	public EditText smsContent;

	@ViewMapping(ID = R.id.save)
	public Button save;
	private long time;
	private Calendar calendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_simula_sms);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		back.setOnClickListener(this);
		chooseContact.setOnClickListener(this);
		smsDate.setOnClickListener(this);
		smsTime.setOnClickListener(this);
		save.setOnClickListener(this);

		time = System.currentTimeMillis() + 60 * 1000;
		calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));
		updateDateTime();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.chooseContact:
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, CHOOSE_CONTACT);
			break;
		case R.id.smsDate:
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.smsTime:
			showDialog(TIME_DIALOG_ID);
			break;
		case R.id.save:
			checkDateTime();
			break;
		}
	}

	private void checkDateTime() {
		String phone = phoneNumber.getText().toString().trim();
		String content = smsContent.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, R.string.s_phonenumber_empty,
					Toast.LENGTH_LONG).show();
			return;
		} else if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, R.string.s_empty_sms_content,
					Toast.LENGTH_LONG).show();
			return;
		} else if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			Toast.makeText(this, R.string.s_not_passby, Toast.LENGTH_LONG)
					.show();
			return;
		}

		SimulateComm entity = new SimulateComm(null, contact == null ? null
				: contact.getName(), phone, calendar.getTime(), content,
				SimulaCommActivity.SIMULATE_SMS);
		SimulateDaoUtils.getSimulateDao(this).insert(entity);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(),
				SimulateSendSMSReceiver.class);
		intent.putExtra("simu", entity);
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), REQUES_REVEIVER_CODE, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtMillis = calendar.getTimeInMillis()
				- System.currentTimeMillis() + SystemClock.elapsedRealtime();
		am.set(AlarmManager.ELAPSED_REALTIME, triggerAtMillis, pendIntent);
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new MyTimePickerDialog(this, mTimeSetListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), false);
		case DATE_DIALOG_ID:
			return new MyDatePickerDialog(this, mDateSetListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(calendar.get(Calendar.HOUR),
					calendar.get(Calendar.MINUTE));
			break;
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_CONTACT && resultCode == RESULT_OK) {
			contact = ContactsUtils.readContact(this, data.getData());
			phoneNumber.setText(contact.getPhone());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			calendar.set(year, monthOfYear, dayOfMonth);
			updateDateTime();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
			updateDateTime();
		}
	};
	private SimpleContact contact;

	private void updateDateTime() {
		smsDate.setText(getString(R.string.s_simulate_date)
				+ DateFormat.getDateInstance().format(calendar.getTime()));
		smsTime.setText(getString(R.string.s_simulate_time)
				+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
						calendar.getTime()));
	}
}
