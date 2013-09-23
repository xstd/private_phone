package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.SimulaCommAdapter;

public class SimulaCommActivity extends BaseActivity {

	public final static int SIMULA_SMS = 1;
	public final static int SIMULA_PHONE = 2;

	@ViewMapping(ID = R.id.lv)
	public ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simula_comm);

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		SimulaCommAdapter adapter = new SimulaCommAdapter(
				getApplicationContext());
		lv.setAdapter(adapter);
	}

	/**
	 * 显示模拟短信记录按钮
	 * 
	 * @param view
	 */
	public void simulaSms(View view) {

	}

	/**
	 * 显示模拟电话记录按钮
	 * 
	 * @param view
	 */
	public void simulaPhone(View view) {

	}

	/**
	 * 添加一条模拟通讯
	 * 
	 * @param view
	 */
	public void addSimula(View view) {

	}
}
