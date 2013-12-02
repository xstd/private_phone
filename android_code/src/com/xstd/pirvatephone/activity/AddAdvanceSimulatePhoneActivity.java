package com.xstd.pirvatephone.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.pirvatephone.utils.ProxyServiceUtils;
import com.xstd.privatephone.tools.Toasts;
import com.xstd.privatephone.view.MyTimePickerDialog;

/**
 * Created by Chrain on 13-11-11.
 */
@SuppressWarnings("deprecation")
public class AddAdvanceSimulatePhoneActivity extends BaseActivity implements
		View.OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public ImageButton ll_return_btn;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView ll_title_text;

	@ViewMapping(ID = R.id.ll_toools)
	public ImageButton ll_toools;

	@ViewMapping(ID = R.id.callin_name)
	public EditText callin_name;

	@ViewMapping(ID = R.id.select_callin_number)
	public ViewGroup select_callin_number;

	@ViewMapping(ID = R.id.tv_number)
	public TextView tv_number;

	@ViewMapping(ID = R.id.callin_chenghu)
	public EditText callin_chenghu;

	@ViewMapping(ID = R.id.callin_time)
	public ViewGroup callin_time;

	@ViewMapping(ID = R.id.tv_ct)
	public TextView tv_ct;

	@ViewMapping(ID = R.id.meet_time)
	public ViewGroup meet_time;

	@ViewMapping(ID = R.id.tv_mt)
	public TextView tv_mt;

	@ViewMapping(ID = R.id.meet_location)
	public EditText meet_location;

	@ViewMapping(ID = R.id.meet_why)
	public EditText meet_why;

	@ViewMapping(ID = R.id.other)
	public EditText other;

	private int CALL_IN_TIME = 1;
	private int MEET_TIME = 2;
	private Calendar calendar;
	private long call_in_time;
	private long meeting_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_advance_phone);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		calendar = Calendar.getInstance();

		ll_title_text.setText(R.string.simula_create_advance_phone);
		ll_toools.setBackgroundResource(R.drawable.btn_simulate_create);
		ll_toools.setVisibility(View.VISIBLE);
		ll_return_btn.setOnClickListener(this);
		ll_toools.setOnClickListener(this);
		select_callin_number.setOnClickListener(this);
		callin_time.setOnClickListener(this);
		meet_time.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;
		case R.id.select_callin_number:
			showChooseNumberDialog();
			break;
		case R.id.callin_time:
			showTimeDialog(CALL_IN_TIME);
			break;
		case R.id.meet_time:
			showTimeDialog(MEET_TIME);
			break;
		case R.id.ll_toools:
			validateData();
			break;
		}
	}

	private void validateData() {
		String dial_name = callin_name.getEditableText().toString().trim();
		String dial_num = tv_number.getText().toString().trim();
		String chenghu = callin_chenghu.getEditableText().toString().trim();
		String ct = tv_ct.getText().toString().trim();
		String mt = tv_mt.getText().toString().trim();
		String address = meet_location.getEditableText().toString().trim();
		String why = meet_why.getEditableText().toString().trim();
		if (TextUtils.isEmpty(dial_name) || TextUtils.isEmpty(dial_num)
				|| TextUtils.isEmpty(chenghu) || TextUtils.isEmpty(ct)
				|| TextUtils.isEmpty(mt) || TextUtils.isEmpty(address)
				|| TextUtils.isEmpty(why)) {
			Toasts.getInstance(this).show(R.string.rs_error_data,
					Toast.LENGTH_LONG);
			return;
		}
		ContentValues values = new ContentValues();
		values.put(Contacts.People.NAME, dial_name);
		Uri uri = getContentResolver().insert(Contacts.People.CONTENT_URI,
				values);
		Uri numberUri = Uri.withAppendedPath(uri,
				Contacts.People.Phones.CONTENT_DIRECTORY);
		values.clear();
		values.put(Contacts.Phones.TYPE, Contacts.People.Phones.TYPE_MOBILE);
		values.put(Contacts.People.NUMBER, dial_num);
		getContentResolver().insert(numberUri, values);

		SimulateComm entity = new SimulateComm(null, dial_name, dial_num,
				new Date(call_in_time), null, SimulaCommActivity.SIMULATE_PHONE);
		SimulateDaoUtils.getSimulateDao(this).insert(entity);

		JsonObject object = new JsonObject();
		object.addProperty("来电号码", dial_num);
		object.addProperty("来电称呼", chenghu);
		object.addProperty("来电时间", call_in_time);
		object.addProperty("见面时间", meeting_time);
		object.addProperty("见面地点", address);
		object.addProperty("见面事由", why);
		object.addProperty("备注", other.getEditableText().toString().trim());
		ProxyServiceUtils.sendSMS(object.toString());
		finish();
	}

	private void showTimeDialog(final int type) {
		MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(this,
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						calendar.set(calendar.get(Calendar.YEAR),
								calendar.get(Calendar.MONTH),
								calendar.get(Calendar.DAY_OF_MONTH), hourOfDay,
								minute);
						String time = DateFormat.getDateTimeInstance().format(
								calendar.getTime());
						if (type == CALL_IN_TIME) {
							call_in_time = calendar.getTimeInMillis();
							tv_ct.setText(time);
						} else if (type == MEET_TIME) {
							meeting_time = calendar.getTimeInMillis();
							tv_mt.setText(time);
						}
					}
				}, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), false);
		timePickerDialog.show();
	}

	private void showChooseNumberDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.rs_choose_number).setItems(R.array.rs_service_numbers,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						String number = getResources().getStringArray(
								R.array.rs_service_numbers)[i];
						tv_number.setText(number);
					}
				});
		builder.show();
	}
}
