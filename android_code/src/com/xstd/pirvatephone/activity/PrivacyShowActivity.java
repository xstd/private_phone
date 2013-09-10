package com.xstd.pirvatephone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

public class PrivacyShowActivity extends BaseActivity {

	protected static final String TAG = "PrivacyShowActivity";
	private TextView return_bt;
	private TextView title_text;
	private int privacy_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_show);
		privacy_type = getIntent().getIntExtra("privacy_type", 0);
		initViews();
		setListener();
	}

	private void setListener() {
		return_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "按了返回按钮");
				finish();
			}
		});
	}

	private void initViews() {
		return_bt = (TextView) findViewById(R.id.ll_return_btn);
		title_text = (TextView) findViewById(R.id.ll_title_text);
		title_text.setText("隐私"
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
		add_privacy = (Button) findViewById(R.id.add_privacy);
		add_privacy.setText("添加"
				+ PrivacySpaceActivity.home_privacy_title[privacy_type]);
	}

	CharSequence[] item_pic = new String[] { "添加相机图片", "自动扫描图片", "手动选择片图片" };
	CharSequence[] item_audio = new String[] { "添加录音", "自动扫描录音", "手动选择录音" };
	CharSequence[] item_vedio = new String[] { "添加视频", "自动扫描视频", "手动选择视频" };
	private Button add_privacy;

	/**
	 * 底部添加按钮监听事件
	 * 
	 * @param view
	 */
	public void add(View view) {
		CharSequence[] items = null;
		// 如果隐私类型为图片、音频、视频则弹出上下文菜单选择；如果隐私类型为文件，则跳到列出sd卡文件的activity。
		switch (privacy_type) {
		case 0:
			items = item_pic;
			break;
		case 1:
			items = item_audio;
			break;
		case 2:
			items = item_vedio;
			break;
		case 3:
			Intent intent = new Intent(PrivacyShowActivity.this,
					ShowSDCardFilesActivity.class);
			startActivity(intent);
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				switch (privacy_type) {
				case 0:
					intent.setType("image/*");
					break;
				case 1:
					intent.setType("audio/*");
					break;
				case 2:
					intent.setType("video/*");
					break;
				}
				startActivityForResult(intent, 1);
			}
		});
		builder.show();
	}
}
