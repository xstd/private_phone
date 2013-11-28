package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.ContactsUtils;
import com.xstd.privatephone.adapter.AddCommonPhoneAdapter;
import com.xstd.privatephone.adapter.AddCommonSMSAdapter;
import com.xstd.privatephone.adapter.SimulateCommAdapter;

public class SimulaCommActivity extends BaseActivity implements
		View.OnClickListener {

	private static final String TAG = "SimulaCommActivity";

	/**
	 * 代表模拟类型
	 */
	public final static int SIMULATE_SMS = 0;
	public final static int SIMULATE_PHONE = 1;
	public static final int COMMON_SMS = 2;
	public static final int COMMON_PHONE = 3;

	@ViewMapping(ID = R.id.ll_return_btn)
	public ImageButton ll_return_btn;

	@ViewMapping(ID = R.id.lv_sms)
	public ListView lv_sms;

	@ViewMapping(ID = R.id.lv_phone)
	public ListView lv_phone;

	@ViewMapping(ID = R.id.lv_common_sms)
	public ListView lv_common_sms;

	@ViewMapping(ID = R.id.lv_common_phone)
	public ListView lv_common_phone;

	@ViewMapping(ID = R.id.btn_phone)
	public ViewGroup btn_phone;

	@ViewMapping(ID = R.id.btn_other)
	public Button btn_other;

	@ViewMapping(ID = R.id.rdts)
	public ImageView rdts;

	@ViewMapping(ID = R.id.simulate_sms)
	public TextView simulate_sms;

	@ViewMapping(ID = R.id.simulate_phone)
	public TextView simulate_phone;

	@ViewMapping(ID = R.id.common_sms)
	public TextView common_sms;

	@ViewMapping(ID = R.id.common_phone)
	public TextView common_phone;

	private int type = SIMULATE_SMS;

	private SimulateCommAdapter adapter_phone;
	private SimulateCommAdapter adapter_sms;
	private AddCommonPhoneAdapter adapter_common_phone;
	private AddCommonSMSAdapter adapter_common_sms;

	private int tw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simula_comm);

		Point outSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(outSize);
		tw = outSize.x / 4;

		initUI();
		displaySimulate(type);
	}

	private void initUI() {
		ViewMapUtil.viewMapping(this, getWindow());

		ll_return_btn.setOnClickListener(this);
		simulate_sms.setOnClickListener(this);
		simulate_phone.setOnClickListener(this);
		common_sms.setOnClickListener(this);
		common_phone.setOnClickListener(this);
		btn_other.setOnClickListener(this);

		adapter_phone = new SimulateCommAdapter(this, SIMULATE_PHONE);
		lv_phone.setAdapter(adapter_phone);

		adapter_sms = new SimulateCommAdapter(this, SIMULATE_SMS);
		lv_sms.setAdapter(adapter_sms);

		adapter_common_phone = new AddCommonPhoneAdapter(this,
				ContactsUtils.getCallLogByPeople(getApplicationContext()), true);
		lv_common_phone.setAdapter(adapter_common_phone);

		adapter_common_sms = new AddCommonSMSAdapter(getApplicationContext(),
				ContactsUtils.getSmsByPeople(getApplicationContext()), true);
		lv_common_sms.setAdapter(adapter_common_sms);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (type == SIMULATE_PHONE)
			adapter_phone.changeDatas();
		else if (type == SIMULATE_SMS)
			adapter_sms.changeDatas();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_return_btn:
			finish();
			break;
		case R.id.btn_other:
			Intent intent = new Intent();
			if (type == SIMULATE_SMS) {
				intent.setClass(this, AddSimulaSmsActivity.class);
			} else if (type == COMMON_SMS) {
				intent.setClass(this, AddCommonSmsActivity.class);
			} else if (type == COMMON_PHONE) {
				intent.setClass(this, AddCommonPhoneActivity.class);
			}
			startActivity(intent);
			break;
		case R.id.simulate_sms:
			displaySimulate(SIMULATE_SMS);
			break;
		case R.id.simulate_phone:
			displaySimulate(SIMULATE_PHONE);
			break;
		case R.id.common_sms:
			displaySimulate(COMMON_SMS);
			break;
		case R.id.common_phone:
			displaySimulate(COMMON_PHONE);
			break;
		}
	}

	private void displaySimulate(int simulateType) {
		if (SIMULATE_PHONE == simulateType) {
			adapter_phone.changeDatas();
			btn_phone.setVisibility(View.VISIBLE);
			btn_other.setVisibility(View.GONE);
			lv_sms.setVisibility(View.GONE);
			lv_phone.setVisibility(View.VISIBLE);
			lv_common_sms.setVisibility(View.GONE);
			lv_common_phone.setVisibility(View.GONE);
		} else {
			btn_phone.setVisibility(View.GONE);
			btn_other.setVisibility(View.VISIBLE);
			switch (simulateType) {
			case SIMULATE_SMS:
				adapter_sms.changeDatas();
				lv_sms.setVisibility(View.VISIBLE);
				lv_phone.setVisibility(View.GONE);
				lv_common_sms.setVisibility(View.GONE);
				lv_common_phone.setVisibility(View.GONE);
				btn_other.setText(R.string.simula_add_sms);
				break;
			case COMMON_SMS:
				lv_sms.setVisibility(View.GONE);
				lv_phone.setVisibility(View.GONE);
				lv_common_sms.setVisibility(View.VISIBLE);
				lv_common_phone.setVisibility(View.GONE);
				btn_other.setText(R.string.add_common_sms);
				break;
			case COMMON_PHONE:
				lv_sms.setVisibility(View.GONE);
				lv_phone.setVisibility(View.GONE);
				lv_common_sms.setVisibility(View.GONE);
				lv_common_phone.setVisibility(View.VISIBLE);
				btn_other.setText(R.string.add_common_phone);
				break;
			}
		}
		loadAnimation(simulateType);
		type = simulateType;
	}

	private void loadAnimation(final int simulateType) {
		Animation animation = new TranslateAnimation(type * tw, simulateType
				* tw, 0, 0);
		animation.setDuration(300);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				switch (simulateType) {
				case SIMULATE_SMS:
					simulate_sms.setTextColor(0xff114c81);
					simulate_phone.setTextColor(0xff9fc4f0);
					common_sms.setTextColor(0xff9fc4f0);
					common_phone.setTextColor(0xff9fc4f0);
					break;
				case SIMULATE_PHONE:
					simulate_sms.setTextColor(0xff9fc4f0);
					simulate_phone.setTextColor(0xff114c81);
					common_sms.setTextColor(0xff9fc4f0);
					common_phone.setTextColor(0xff9fc4f0);
					break;
				case COMMON_SMS:
					simulate_sms.setTextColor(0xff9fc4f0);
					simulate_phone.setTextColor(0xff9fc4f0);
					common_sms.setTextColor(0xff114c81);
					common_phone.setTextColor(0xff9fc4f0);
					break;
				case COMMON_PHONE:
					simulate_sms.setTextColor(0xff9fc4f0);
					simulate_phone.setTextColor(0xff9fc4f0);
					common_sms.setTextColor(0xff9fc4f0);
					common_phone.setTextColor(0xff114c81);
					break;
				}
			}
		});
		rdts.startAnimation(animation);
	}

	/**
	 * 增加模拟电话按钮
	 * 
	 * @param v
	 */
	public void addSimulate(View v) {
		Intent intent = new Intent(this, AddSimulaPhoneActivity.class);
		startActivity(intent);
	}

	/**
	 * 增加人工电话按钮
	 * 
	 * @param v
	 */
	public void addAdvanceSimulate(View v) {
		Intent intent = new Intent(this, AddAdvanceSimulatePhoneActivity.class);
		startActivity(intent);
	}

}