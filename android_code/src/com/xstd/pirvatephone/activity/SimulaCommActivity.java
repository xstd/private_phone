package com.xstd.pirvatephone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;
import com.xstd.privatephone.adapter.SimulaCommAdapter;

public class SimulaCommActivity extends BaseActivity implements
		OnItemLongClickListener {

	/**
	 * 代表模拟短信的类型
	 */
	public final static int SIMULA_SMS = 1;

	/**
	 * 代表模拟通话的类型
	 */
	public final static int SIMULA_PHONE = 2;

	@ViewMapping(ID = R.id.lv_phone)
	public ListView lv_phone;

	@ViewMapping(ID = R.id.lv_sms)
	public ListView lv_sms;

	@ViewMapping(ID = R.id.btn_add)
	public Button btn_add;

	private int type = SIMULA_PHONE;

	private SimulaCommAdapter adapter_phone;

	private SimulaCommAdapter adapter_sms;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simula_comm);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		adapter_phone = new SimulaCommAdapter(this, SIMULA_PHONE);
		lv_phone.setAdapter(adapter_phone);
		adapter_sms = new SimulaCommAdapter(this, SIMULA_SMS);
		lv_sms.setAdapter(adapter_sms);

		lv_phone.setOnItemLongClickListener(this);
		lv_sms.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (type == SIMULA_PHONE)
			adapter_phone.changeDatas();
		else if (type == SIMULA_SMS)
			adapter_sms.changeDatas();
		displayWhich();
	}

	private void displayWhich() {
		if (type == SIMULA_PHONE) {
			lv_phone.setVisibility(View.VISIBLE);
			lv_sms.setVisibility(View.GONE);
		} else if (type == SIMULA_SMS) {
			lv_phone.setVisibility(View.GONE);
			lv_sms.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示模拟短信记录按钮
	 * 
	 * @param view
	 */
	public void simulaSms(View view) {
		btn_add.setText(R.string.simula_add_sms);
		type = SIMULA_SMS;
		adapter_sms.changeDatas();
		displayWhich();
	}

	/**
	 * 显示模拟电话记录按钮
	 * 
	 * @param view
	 */
	public void simulaPhone(View view) {
		btn_add.setText(R.string.simula_add_phone);
		type = SIMULA_PHONE;
		adapter_phone.changeDatas();
		displayWhich();
	}

	/**
	 * 添加一条模拟通讯
	 * 
	 * @param view
	 */
	public void addSimula(View view) {
		Intent intent = new Intent();
		if (type == SIMULA_SMS) {
			intent.setClass(this, AddSimulaSmsActivity.class);
		} else if (type == SIMULA_PHONE) {
			intent.setClass(this, AddSimulaPhoneActivity.class);
		}
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showDialog((SimulateComm) parent.getAdapter().getItem(position));
		return false;
	}

	private void showDialog(final SimulateComm entity) {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.s_del_title).setMessage(R.string.s_del_msg)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SimulateDaoUtils
								.getSimulateDao(getApplicationContext())
								.delete(entity);
						if (type == SIMULA_PHONE)
							adapter_phone.changeDatas();
						else if (type == SIMULA_SMS)
							adapter_sms.changeDatas();
					}
				}).create();
		dialog.show();
	}

}