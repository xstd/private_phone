package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.SimulaCommAdapter;

public class SimulaCommActivity extends BaseActivity {

	/**
	 * 代表模拟短信的类型
	 */
	public final static int SIMULA_SMS = 1;

	/**
	 * 代表模拟通话的类型
	 */
	public final static int SIMULA_PHONE = 2;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	@ViewMapping(ID = R.id.btn_add)
	public Button btn_add;

	private int type = SIMULA_PHONE;

	private SimulaCommAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simula_comm);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		adapter = new SimulaCommAdapter(
				getApplicationContext());
		lv.setAdapter(adapter);
	}

	/**
	 * 显示模拟短信记录按钮
	 * 
	 * @param view
	 */
	public void simulaSms(View view) {
		btn_add.setText(R.string.simula_add_sms);
		type = SIMULA_SMS;
		adapter.changeDatas(type);
	}

	/**
	 * 显示模拟电话记录按钮
	 * 
	 * @param view
	 */
	public void simulaPhone(View view) {
		btn_add.setText(R.string.simula_add_phone);
		type = SIMULA_PHONE;
		adapter.changeDatas(type);
	}

	/**
	 * 添加一条模拟通讯
	 * 
	 * @param view
	 */
	public void addSimula(View view) {
		Intent intent = new Intent();
		if(type == SIMULA_SMS) {
			intent.setClass(this, AddSimulaSmsActivity.class);
		}else if(type == SIMULA_PHONE) {
			intent.setClass(this, AddSimulaPhoneActivity.class);
		}
		startActivity(intent);
	}

}
