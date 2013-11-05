package com.xstd.pirvatephone.activity;

import com.plugin.common.utils.view.ViewMapUtil;
import com.plugin.common.utils.view.ViewMapping;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.id;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.utils.BackgroundSoundManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class UserCenterActivity extends BaseActivity implements OnClickListener {

    private Button btn_setting;

    @ViewMapping(ID = id.switch_status)
    public TextView switch_status;

    @ViewMapping(ID = id.kg)
    public Switch kg;

    @ViewMapping(ID = id.select_bg_media)
    public Button select_bg_media;

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
        boolean play_background_sound = sp.getBoolean("play_background_sound", false);
        if (play_background_sound) {
            switch_status.setText(R.string.rs_on_background_sound);
            select_bg_media.setVisibility(View.VISIBLE);
        } else {
            switch_status.setText(R.string.rs_off_background_sound);
            select_bg_media.setVisibility(View.GONE);
        }
        kg.setChecked(play_background_sound);
        kg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sp.edit().putBoolean("play_background_sound", b).commit();
                if (b) {
                    Log.w("TAG", "开关开启了，音乐要start");
                    switch_status.setText(R.string.rs_on_background_sound);
                    select_bg_media.setVisibility(View.VISIBLE);
                    BackgroundSoundManager.getInstance(getApplicationContext()).playBackgroundSound();
                } else {
                    Log.w("TAG", "开关关闭了，音乐也要stop");
                    switch_status.setText(R.string.rs_off_background_sound);
                    select_bg_media.setVisibility(View.GONE);
                    BackgroundSoundManager.getInstance(getApplicationContext()).stopBackGroundSound();
                }
            }
        });
        select_bg_media.setOnClickListener(this);
        int resID = sp.getInt("background_sound_resid", R.raw.ddz);
        if (resID == R.raw.ddz)
            select_bg_media.setText(getResources().getStringArray(R.array.background_sound_list)[0]);
        else if (resID == R.raw.ttkp)
            select_bg_media.setText(getResources().getStringArray(R.array.background_sound_list)[1]);
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
            case id.select_bg_media:
                showSelectBackGroundSoundDialog();
                break;
        }
    }

    private void showSelectBackGroundSoundDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.rs_background_sound_title).setItems(R.array.background_sound_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BackgroundSoundManager.getInstance(getApplicationContext()).stopBackGroundSound();
                if (i == 0) {
                    sp.edit().putInt("background_sound_resid", R.raw.ddz).commit();
                } else if (i == 1) {
                    sp.edit().putInt("background_sound_resid", R.raw.ttkp).commit();
                }
                BackgroundSoundManager.getInstance(getApplicationContext()).playBackgroundSound();
            }
        }).create();
        dialog.show();
    }
}
