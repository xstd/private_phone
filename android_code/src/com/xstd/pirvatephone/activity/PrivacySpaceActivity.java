package com.xstd.pirvatephone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

/**
 * 文件隐藏模块主界面
 * 
 * @author Chrain
 * 
 */
public class PrivacySpaceActivity extends BaseActivity {

	protected static final String TAG = "PrivacySpaceActivity";
	/**
	 * 左上角返回按钮
	 */
	private ImageButton return_bt;
	/**
	 * 文件隐藏模块几大类的listview
	 */
	private ListView lv_privacy;
	private PrivacySpaceAdapter adapter;
	private TextView ll_title_text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_space);
		initViews();
		setListener();
	}

	/**
	 * 设置控件监听
	 */
	private void setListener() {
		return_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		lv_privacy.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				if (position == 0) {
					intent.setClass(getApplicationContext(),
							AddPrivacyPictureActivity.class);
				} else {
					intent.setClass(getApplicationContext(),
							PrivacyShowActivity.class);
				}
				intent.putExtra("privacy_type", position);// 把类型带过去
				startActivity(intent);
			}

		});
	}

	/**
	 * 初始化view
	 */
	private void initViews() {
		return_bt = (ImageButton) findViewById(R.id.ll_return_btn);
		ll_title_text = (TextView) findViewById(R.id.ll_title_text);
		ll_title_text.setText(R.string.ll_title_text);
		lv_privacy = (ListView) findViewById(R.id.lv_privacy_main);
		adapter = new PrivacySpaceAdapter();
		lv_privacy.setAdapter(adapter);
	}

	/**
	 * 文件隐藏分类的图片
	 */
	int[] home_privacy_pic = new int[] { R.drawable.privacy_mainview_ic_image,
			R.drawable.home_user_center, R.drawable.privacy_mainview_ic_video,
			R.drawable.privacy_mainview_ic_file,
			R.drawable.privacy_mainview_ic_pwd };

	static String[] home_privacy_title;

	private class PrivacySpaceAdapter extends BaseAdapter {

		public PrivacySpaceAdapter() {
			/**
			 * 加载分类
			 */
			home_privacy_title = getResources().getStringArray(
					R.array.privacy_category);
		}

		@Override
		public int getCount() {
			return home_privacy_pic.length;
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
