package com.xstd.privatephone.adapter;

import com.xstd.pirvatephone.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {

	private Context mContext;
	private String[] contentItems;
	private boolean show_Notification =true;
	private boolean show_Voice=true;
	private boolean show_shake=true;

	public SettingAdapter(Context context) {
		mContext = context;
		contentItems = context.getResources().getStringArray(
				R.array.user_setting_item);
		initData();
	}

	@Override
	public int getCount() {
		if (contentItems != null && contentItems.length > 0) {
			return contentItems.length;
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(mContext, R.layout.private_user_setting_item,
				null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		CheckBox btn_check = (CheckBox) view.findViewById(R.id.btn_check);

		tv_name.setText(contentItems[position]);
		
		switch (position) {
		case 0:
				btn_check.setChecked(show_Notification);
			break;
		case 1:
			btn_check.setChecked(show_Voice);
			break;
		case 2:
			btn_check.setChecked(show_shake);
			break;
		case 3:
			btn_check.setVisibility(View.GONE);
			break;
		}

		return view;
	}
	
	private void initData(){
		SharedPreferences sp = mContext.getSharedPreferences("Setting_Info", 0);

		show_Notification = sp.getBoolean("Show_Notification", true);
		show_Voice = sp.getBoolean("Show_Voice", true);
		show_shake = sp.getBoolean("Show_Shake", true);
	}

}
