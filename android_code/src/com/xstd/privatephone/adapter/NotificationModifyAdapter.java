package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.tools.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationModifyAdapter extends BaseAdapter {

	private Context mContext;
	private int checkedId;

	private String[] mTitleStrs;
	private String[] mContentStrs;

	private int[] iconIds = { R.drawable.ic_statusbar_0,
			R.drawable.ic_statusbar_1, R.drawable.ic_statusbar_2,
			R.drawable.ic_statusbar_3, R.drawable.ic_statusbar_4,
			R.drawable.ic_statusbar_5, R.drawable.ic_statusbar_6,
			R.drawable.ic_statusbar_7 };
	private SharedPreferences sp;

	public NotificationModifyAdapter(Context context) {
		this.mContext = context;
		mTitleStrs = context.getResources().getStringArray(
				R.array.s_setting_statusbar_titles);
		mContentStrs = context.getResources().getStringArray(
				R.array.s_setting_statusbar_contents);
		initData();

	}

	@Override
	public int getCount() {
		return iconIds.length;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = View.inflate(mContext,
				R.layout.private_user_notification_item, null);
		ImageView iv_icon = (ImageView) view.findViewById(R.id.icon);
		TextView tv_name = (TextView) view.findViewById(R.id.title);
		TextView tv_content = (TextView) view.findViewById(R.id.desc);
		CheckBox btn_check = (CheckBox) view.findViewById(R.id.check);

		String cont = sp.getString("" + position, "");
		final String title;
		final String desc;

		if (cont != "") {
			String[] strs = cont.split(":");
			title =strs[0];
			desc =strs[1];
		} else {
			title= mTitleStrs[position];
			desc= mContentStrs[position];
		}
		tv_name.setText(title);
		tv_content.setText(desc);
		iv_icon.setBackgroundResource(iconIds[position]);

		if (position == checkedId) {
			btn_check.setChecked(true);
		} else {
			btn_check.setChecked(false);
		}

		btn_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView.isChecked()) {
					checkedId = position;
					sp.edit().putInt("CheckedId", checkedId).commit();
					sp.edit().putInt("Icon", iconIds[position]).commit();
					sp.edit().putString("Title", title).commit();
					sp.edit().putString("Desc", desc)
							.commit();
					notifyDataSetChanged();
				}
			}
		});

		return view;
	}

	private void initData() {
		sp = mContext.getSharedPreferences("Setting_Info", 0);
		checkedId = sp.getInt("CheckedId", 0);
	}

}
