package com.xstd.pirvatephone.activity;

import com.xstd.pirvatephone.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PrivacySpaceActivity extends BaseActivity {

	protected static final String TAG = "PrivacySpaceActivity";
	private TextView return_bt;
	private ListView lv_privacy;
	private PrivacySpaceAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_space);
		initViews();
		setListener();
	}

	/**
	 * ���ÿؼ��ļ����¼�
	 */
	private void setListener() {
		return_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "���˷��ذ�ť");
				finish();
			}
		});
		lv_privacy.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(PrivacySpaceActivity.this,
						PrivacyShowActivity.class);
				intent.putExtra("privacy_type", position);
				startActivity(intent);
			}

		});
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initViews() {
		return_bt = (TextView) findViewById(R.id.ll_return_btn);
		lv_privacy = (ListView) findViewById(R.id.lv_privacy_main);
		lv_privacy.setDivider(new ColorDrawable(Color.WHITE));
		lv_privacy.setDividerHeight(7);
		adapter = new PrivacySpaceAdapter();
		lv_privacy.setAdapter(adapter);
	}

	int[] home_privacy_pic = new int[] { R.drawable.privacy_mainview_ic_image,
			R.drawable.home_user_center, R.drawable.privacy_mainview_ic_video,
			R.drawable.privacy_mainview_ic_file };

	static String[] home_privacy_title = new String[] { "ͼƬ", "¼��", "��Ƶ", "�ļ�" };

	private class PrivacySpaceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(PrivacySpaceActivity.this,
						R.layout.item_privacy_space, null);
				holder.privacy_pic = (ImageView) convertView
						.findViewById(R.id.privacy_pic);
				holder.privacy_title = (TextView) convertView
						.findViewById(R.id.privacy_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.privacy_pic.setImageResource(home_privacy_pic[position]);
			holder.privacy_title.setText(home_privacy_title[position]);
			return convertView;
		}

	}

	static class ViewHolder {
		ImageView privacy_pic;
		TextView privacy_title;
	}
}
