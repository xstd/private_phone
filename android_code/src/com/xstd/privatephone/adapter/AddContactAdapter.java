package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

public class AddContactAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MyContactInfo> mContactsInfos;
	/** 选取转换为隐私联系人的号码 **/

	public AddContactAdapter(Context context,
			ArrayList<MyContactInfo> contactInfos) {
		mContext = context;
		/*
		 * mContactsName = contactsName; mContactsNumber = contactsNumber;
		 */
		mContactsInfos = contactInfos;
	}

	public void updateUI() {
		notifyDataSetChanged();
	}

	public int getCount() {
		// 设置绘制数量
		return mContactsInfos.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return mContactsInfos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold;
		if (convertView == null ) {
			hold = new ViewHold();
			convertView = View.inflate(mContext,
					R.layout.private_comm_add_item, null);
			hold = new ViewHold();

			hold.name = (TextView) convertView.findViewById(R.id.tv_name);
			hold.phone = (TextView) convertView.findViewById(R.id.tv_phone_num);
			hold.check = (CheckBox) convertView.findViewById(R.id.btn_check);

			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}

		final MyContactInfo mContactInfo = mContactsInfos.get(position);
		// 绘制联系人名称
		hold.name.setText(mContactInfo.getName());
		// 绘制联系人号码
		hold.phone.setText(mContactInfo.getAddress());
		hold.check.setChecked(mContactInfo.isChecked);
		hold.check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContactInfo.isChecked) {
					mContactInfo.isChecked = false;
				} else {
					mContactInfo.isChecked = true;
				}
				Tools.logSh(mContactInfo.isChecked + "");
			}
		});
		return convertView;
	}

	static class ViewHold {
		TextView name;
		TextView phone;
		CheckBox check;
	}

}