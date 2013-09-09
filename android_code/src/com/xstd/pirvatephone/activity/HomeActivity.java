package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

public class HomeActivity extends BaseActivity {
	private GridView gv_home;
	private MyGridViewAdapter adapter;

	private String[] menus = new String[] { "隐私通讯", "私密微信", "文件隐藏", "模拟通讯",
			"服务大厅", "用户中心" };

	private int[] pics = new int[] { R.drawable.home_privacy_comm,
			R.drawable.home_privacy_mincomm, R.drawable.home_privacy_file,
			R.drawable.home_moni_conm, R.drawable.home_privacy_service,
			R.drawable.home_user_center };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initView();

	}

	private void initView() {
		gv_home = (GridView) findViewById(R.id.gv_home);
		adapter = new MyGridViewAdapter();
		gv_home.setAdapter(adapter);

		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					Intent intent = new Intent(HomeActivity.this,
							PrivateCommActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	private class MyGridViewAdapter extends BaseAdapter {

		private ViewHolder holder;

		@Override
		public int getCount() {
			return pics.length;
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

			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.home_gridview_item, null);
				holder = new ViewHolder();
				holder.iv_pic = (ImageView) convertView
						.findViewById(R.id.gv_item_pic);
				holder.tv_text = (TextView) convertView
						.findViewById(R.id.gv_item_text);

				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();

			holder.iv_pic.setBackgroundResource(pics[position]);
			holder.tv_text.setText(menus[position]);

			return convertView;
		}

	}

	class ViewHolder {
		ImageView iv_pic;
		TextView tv_text;
	}
}
