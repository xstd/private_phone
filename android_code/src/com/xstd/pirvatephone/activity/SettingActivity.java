package com.xstd.pirvatephone.activity;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.id;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.privatephone.adapter.SettingAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

public class SettingActivity extends BaseActivity {

	private SharedPreferences sp;

	@ViewMapping(ID = R.id.back)
	public ImageView back;

	@ViewMapping(ID = R.id.settingList)
	public ListView settingList;

	private boolean show_Notification;
	private boolean show_Voice;
	private boolean show_shake;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("Setting_Info", 0);

		initView();
	}

	private void initView() {
		ViewMapUtil.viewMapping(this, getWindow());

		settingList.setAdapter(new SettingAdapter(SettingActivity.this));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		settingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox btn_check = (CheckBox) view
						.findViewById(R.id.check);
				btn_check.setChecked(!btn_check.isChecked());
				switch (position) {
				case 0:
					sp.edit()
							.putBoolean("Show_Notification",
									btn_check.isChecked()).commit();
					break;
				case 1:
					sp.edit().putBoolean("Show_Voice", btn_check.isChecked())
							.commit();
					break;
				case 2:
					sp.edit().putBoolean("Show_Shake", btn_check.isChecked())
							.commit();
					break;
				case 3:
					Intent intent = new Intent(SettingActivity.this,NotificationModifyActivity.class);
					startActivity(intent);
					break;
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
