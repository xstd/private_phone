package com.xstd.pirvatephone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.BackgroundSoundManager;
import com.xstd.privatephone.view.JumpSoftwareWindowView;

public class UserCenterActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_SET_JUMP_APP_CODE = 1;
    private Button btn_setting;

	@ViewMapping(ID = R.id.bgs_switch_status)
	public TextView bgs_switch_status;

	@ViewMapping(ID = R.id.bgs_kg)
	public Switch bgs_kg;

	@ViewMapping(ID = R.id.bgs_select_bg_media)
	public Button bgs_select_bg_media;
	
	@ViewMapping(ID = R.id.exit_switch_status)
	public TextView exit_switch_status;

	@ViewMapping(ID = R.id.exit_kg)
	public Switch exit_kg;

	@ViewMapping(ID = R.id.exit_select_bg_media)
	public Button exit_select_bg_media;
	
	private static final String SETTING_SP = "setting";

	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);

		sp = getSharedPreferences(SETTING_SP, MODE_PRIVATE);

		initView();
	}

	private void initView() {
		ViewMapUtil.viewMapping(this, getWindow());

		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserCenterActivity.this,
						SettingActivity.class);
				startActivity(intent);
			}
		});
		boolean play_background_sound = sp.getBoolean("play_background_sound",
				false);
		if (play_background_sound) {
			bgs_switch_status.setText(R.string.rs_on_background_sound);
			bgs_select_bg_media.setVisibility(View.VISIBLE);
		} else {
			bgs_switch_status.setText(R.string.rs_off_background_sound);
			bgs_select_bg_media.setVisibility(View.GONE);
		}
		boolean jump_other = sp.getBoolean("jump_other_software", false);
		if (jump_other) {
			exit_switch_status.setText(R.string.rs_on_jump_software);
			exit_select_bg_media.setVisibility(View.VISIBLE);
		} else {
			exit_switch_status.setText(R.string.rs_off_jump_software);
			exit_select_bg_media.setVisibility(View.GONE);
		}
		bgs_kg.setChecked(play_background_sound);
		bgs_kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,
					boolean b) {
				sp.edit().putBoolean("play_background_sound", b).commit();
				if (b) {
					bgs_switch_status.setText(R.string.rs_on_background_sound);
					bgs_select_bg_media.setVisibility(View.VISIBLE);
					BackgroundSoundManager.getInstance(getApplicationContext())
							.playBackgroundSound();
				} else {
					bgs_switch_status.setText(R.string.rs_off_background_sound);
					bgs_select_bg_media.setVisibility(View.GONE);
					BackgroundSoundManager.getInstance(getApplicationContext())
							.stopBackGroundSound();
				}
			}
		});
		exit_kg.setChecked(jump_other);
		exit_kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,
					boolean b) {
				sp.edit().putBoolean("jump_other_software", b).commit();
				if (b) {
					exit_switch_status.setText(R.string.rs_on_jump_software);
					exit_select_bg_media.setVisibility(View.VISIBLE);
					//TODO 界面显示一键切出图标
                    JumpSoftwareWindowView.getInstance(getApplicationContext()).show();
				} else {
					exit_switch_status.setText(R.string.rs_off_jump_software);
					exit_select_bg_media.setVisibility(View.GONE);
					//TODO 界面隐藏一键切出图标
                    JumpSoftwareWindowView.getInstance(getApplicationContext()).dismiss();
				}
			}
		});
		bgs_select_bg_media.setOnClickListener(this);
		exit_select_bg_media.setOnClickListener(this);
		displayBtn();
	}

	private void displayBtn() {
		int resID = sp.getInt("background_sound_resid", R.raw.ddz);
		if (resID == R.raw.ddz)
			bgs_select_bg_media.setText(getResources().getStringArray(
					R.array.background_sound_list)[0]);
		else if (resID == R.raw.ttkp)
			bgs_select_bg_media.setText(getResources().getStringArray(
					R.array.background_sound_list)[1]);
		String jump_software_name = sp.getString("jump_software_name", getApplicationInfo().loadLabel(getPackageManager()).toString());
		exit_select_bg_media.setText(jump_software_name);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_center, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bgs_select_bg_media:
			showSelectBackGroundSoundDialog();
			break;
		case R.id.exit_select_bg_media:
			Intent intent = new Intent(getApplicationContext(), ShowAllInstallAppActivity.class);
			startActivityForResult(intent, REQUEST_SET_JUMP_APP_CODE);
			break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_SET_JUMP_APP_CODE) {
            PackageInfo pi = data.getParcelableExtra("pi");
            sp.edit().putString("jump_software_name",pi.applicationInfo.loadLabel(getPackageManager()).toString()).commit();
            sp.edit().putString("jump_software_package_name",pi.applicationInfo.packageName).commit();
            displayBtn();
        }
    }

    private void showSelectBackGroundSoundDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.rs_background_sound_title)
				.setItems(R.array.background_sound_list,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								BackgroundSoundManager.getInstance(
										getApplicationContext())
										.stopBackGroundSound();
								if (i == 0) {
									sp.edit()
											.putInt("background_sound_resid",
													R.raw.ddz).commit();
								} else if (i == 1) {
									sp.edit()
											.putInt("background_sound_resid",
													R.raw.ttkp).commit();
								}
								displayBtn();
								BackgroundSoundManager.getInstance(
										getApplicationContext())
										.playBackgroundSound();
							}
						}).create();
		dialog.show();
	}
}
