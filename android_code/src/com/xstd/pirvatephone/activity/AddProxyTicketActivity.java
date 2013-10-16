package com.xstd.pirvatephone.activity;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import android.view.View;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.service.ProxyServiceDaoUtils;
import com.xstd.pirvatephone.dao.service.ProxyTicket;
import com.xstd.pirvatephone.utils.ProxyServiceUtils;
import com.xstd.privatephone.tools.Toasts;
import com.xstd.privatephone.view.MyDatePickerDialog;

public class AddProxyTicketActivity extends BaseActivity implements View.OnClickListener {

	@ViewMapping(ID = R.id.ll_return_btn)
	public TextView return_bt;

	@ViewMapping(ID = R.id.ll_title_text)
	public TextView title_text;

	@ViewMapping(ID = R.id.et_name)
	public EditText et_name;

	@ViewMapping(ID = R.id.et_card)
	public EditText et_card;

	@ViewMapping(ID = R.id.et_flight)
	public EditText et_flight;

	@ViewMapping(ID = R.id.btn_date)
	public TextView btn_date;

	@ViewMapping(ID = R.id.et_address)
	public EditText et_address;

	@ViewMapping(ID = R.id.et_receiver)
	public EditText et_receiver;

	@ViewMapping(ID = R.id.et_phone)
	public EditText et_phone;

	@ViewMapping(ID = R.id.ok)
	public Button btn_ok;

	@ViewMapping(ID = R.id.cancel)
	public Button btn_cancle;

	private Calendar calendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_proxy_ticket);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		return_bt.setOnClickListener(this);
		title_text.setText(R.string.service_add_jipiao);
		btn_date.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == return_bt || v == btn_cancle)
			finish();
		if (v == btn_date)
			showDateDialog();
		if (v == btn_ok)
			checkData();
	}

	private void checkData() {
		String name = et_name.getText().toString().trim();
		String card = et_card.getText().toString().trim();
		String flight = et_flight.getText().toString().trim();
		String date = btn_date.getText().toString().trim();
		String address = et_address.getText().toString().trim();
		String receiver = et_receiver.getText().toString().trim();
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(card) || TextUtils.isEmpty(flight) || TextUtils.isEmpty(date) || TextUtils.isEmpty(address) || TextUtils.isEmpty(receiver) || TextUtils.isEmpty(phone)) {
			Toasts.getInstance(getApplicationContext()).show(R.string.rs_error_filed, 0);
			return;
		}
		ProxyTicket ticket = new ProxyTicket(null, name, card, flight, calendar.getTime(), receiver, address, phone, null);
		ProxyServiceDaoUtils.getProxyTicketDao(getApplicationContext()).insert(ticket);
		String content = ProxyServiceUtils.getSMSContent(ticket);
		ProxyServiceUtils.sendSMS(content);
		finish();
	}

	private void showDateDialog() {
		calendar = Calendar.getInstance();
		MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calendar.set(year, monthOfYear, dayOfMonth);
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}
}
