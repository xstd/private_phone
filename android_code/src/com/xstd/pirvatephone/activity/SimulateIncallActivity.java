package com.xstd.pirvatephone.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public class SimulateIncallActivity extends BaseActivity implements View.OnClickListener {

	private SimulateComm simu;

	/**
	 * 通话中没有选择键盘时显示的信息
	 */
	@ViewMapping(ID = R.id.info)
	public ViewGroup info;

	/**
	 * 通话中选择键盘时候输入的
	 */
	@ViewMapping(ID = R.id.number)
	public TextView number;

	@ViewMapping(ID = R.id.name)
	public TextView name;
	@ViewMapping(ID = R.id.phonenumber)
	public TextView phonenumber;
	@ViewMapping(ID = R.id.duringtime)
	public TextView duringtime;

	@ViewMapping(ID = R.id.normal_pic)
	public ViewGroup normal_pic;

	@ViewMapping(ID = R.id.keyboard)
	public ViewGroup keyboard;

	@ViewMapping(ID = R.id.btn1_middle_normal)
	public Button btn1_middle_normal;
	@ViewMapping(ID = R.id.btn2_middle_normal)
	public Button btn2_middle_normal;
	@ViewMapping(ID = R.id.btn3_middle_normal)
	public Button btn3_middle_normal;

	@ViewMapping(ID = R.id.b1)
	public ImageView mB1;

	@ViewMapping(ID = R.id.b2)
	public ImageView mB2;

	@ViewMapping(ID = R.id.b3)
	public ImageView mB3;

	@ViewMapping(ID = R.id.b4)
	public ImageView mB4;

	@ViewMapping(ID = R.id.b5)
	public ImageView mB5;

	@ViewMapping(ID = R.id.b6)
	public ImageView mB6;

	@ViewMapping(ID = R.id.b7)
	public ImageView mB7;

	@ViewMapping(ID = R.id.b8)
	public ImageView mB8;

	@ViewMapping(ID = R.id.b9)
	public ImageView mB9;

	@ViewMapping(ID = R.id.b0)
	public ImageView mB0;

	@ViewMapping(ID = R.id.star)
	public ImageView mBStar;

	@ViewMapping(ID = R.id.bj)
	public ImageView mBj;

	@ViewMapping(ID = R.id.btn1_bottm)
	public ImageView btn1_bottm;
	@ViewMapping(ID = R.id.btn2_bottm)
	public ImageView btn2_bottm;
	@ViewMapping(ID = R.id.btn3_bottm)
	public ImageView btn3_bottm;
	@ViewMapping(ID = R.id.btn4_bottm)
	public ImageView btn4_bottm;
	@ViewMapping(ID = R.id.btn5_bottm)
	public ImageView btn5_bottm;
	@ViewMapping(ID = R.id.btn6_bottm)
	public ImageView btn6_bottm;

	private boolean displayKeyboard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simulate_incall);

		simu = (SimulateComm) getIntent().getSerializableExtra("simu");

		initUI();
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		if (simu != null) {
			name.setText(simu.getName());
			phonenumber.setText(simu.getPhonenumber());
		}

		btn2_bottm.setOnClickListener(this);
		btn3_bottm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn2_bottm:
			changeDisplay();
			break;
		case R.id.btn3_bottm:
			finish();
			break;
		}
	}

	private void changeDisplay() {
		if (displayKeyboard) {
			info.setVisibility(View.VISIBLE);
			number.setVisibility(View.GONE);
			normal_pic.setVisibility(View.VISIBLE);
			keyboard.setVisibility(View.GONE);
		} else {
			info.setVisibility(View.GONE);
			number.setVisibility(View.VISIBLE);
			normal_pic.setVisibility(View.GONE);
			keyboard.setVisibility(View.VISIBLE);
		}
		displayKeyboard = !displayKeyboard;
	}
}
