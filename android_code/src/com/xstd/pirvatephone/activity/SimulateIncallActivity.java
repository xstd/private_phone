package com.xstd.pirvatephone.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
	public TextView mNumber;

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

	private String mNumberStr = "";

	private int calltime;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			duringtime.setText(formatTime(calltime));
		}

	};

	/**
	 * 把时间格式化成00:00的方式
	 * 
	 * @param calltime
	 * @return
	 */
	private CharSequence formatTime(int calltime) {
		StringBuilder sb = new StringBuilder();
		int x = calltime / 60;
		int y = calltime % 60;
		if (x < 1) {
			sb.append("00:");
		} else if (x < 10) {
			sb.append("0" + x + ":");
		} else if (x > 9) {
			sb.append(x + ":");
		}
		if (y < 1) {
			sb.append("00");
		} else if (y < 10) {
			sb.append("0" + y);
		} else if (y > 9) {
			sb.append(y + "");
		}
		return sb.toString();
	}

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

		mB1.setOnClickListener(this);
		mB2.setOnClickListener(this);
		mB3.setOnClickListener(this);
		mB4.setOnClickListener(this);
		mB5.setOnClickListener(this);
		mB6.setOnClickListener(this);
		mB7.setOnClickListener(this);
		mB8.setOnClickListener(this);
		mB9.setOnClickListener(this);
		mB0.setOnClickListener(this);
		mBStar.setOnClickListener(this);
		mBj.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		duringtime.setText("00:00");
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				calltime++;
				handler.sendEmptyMessage(0);
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000, 1000);
	}

	@Override
	public void onClick(View v) {
		String curPressData = "-1";
		switch (v.getId()) {
		case R.id.b0:
			curPressData = "0";
			break;
		case R.id.b1:
			curPressData = "1";
			break;
		case R.id.b2:
			curPressData = "2";
			break;
		case R.id.b3:
			curPressData = "3";
			break;
		case R.id.b4:
			curPressData = "4";
			break;
		case R.id.b5:
			curPressData = "5";
			break;
		case R.id.b6:
			curPressData = "6";
			break;
		case R.id.b7:
			curPressData = "7";
			break;
		case R.id.b8:
			curPressData = "8";
			break;
		case R.id.b9:
			curPressData = "9";
			break;
		case R.id.star:
			curPressData = "*";
			break;
		case R.id.bj:
			curPressData = "#";
			break;
		case R.id.btn2_bottm:
			changeDisplay();
			break;
		case R.id.btn3_bottm:
			finish();
			break;
		}

		if (!curPressData.equals("-1") && !curPressData.equals("-2")) {
			mNumberStr = mNumberStr + curPressData;
		} else if (curPressData.equals("-1")) {
			// TODO:
		} else if (curPressData.equals("-2")) {
			// del action
			if (mNumberStr.length() > 0) {
				mNumberStr = mNumberStr.substring(0, mNumberStr.length() - 1);
			}
		}

		mNumber.setText(mNumberStr);

	}

	private void changeDisplay() {
		if (displayKeyboard) {
			info.setVisibility(View.VISIBLE);
			mNumber.setVisibility(View.GONE);
			normal_pic.setVisibility(View.VISIBLE);
			keyboard.setVisibility(View.GONE);
		} else {
			info.setVisibility(View.GONE);
			mNumber.setVisibility(View.VISIBLE);
			normal_pic.setVisibility(View.GONE);
			keyboard.setVisibility(View.VISIBLE);
		}
		displayKeyboard = !displayKeyboard;
	}
}
