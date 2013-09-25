package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AddContactAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mContactsName;
	private ArrayList<String> mContactsNumber;
	private ViewHold hold;

	public AddContactAdapter(Context context, ArrayList<String> contactsName,
			ArrayList<String> contactsNumber) {
		mContext = context;
		mContactsName = contactsName;
		mContactsNumber = contactsNumber;

	}
	
	public void updateUI(){
		notifyDataSetChanged();
	}

	public int getCount() {
		// 设置绘制数量
		return mContactsName.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null || position < mContactsNumber.size()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.private_comm_add_item, null);
			hold = new ViewHold();
			
			hold.name =  (TextView) convertView.findViewById(R.id.tv_name);
			hold.phone= (TextView) convertView.findViewById(R.id.tv_phone_num);
			hold.check= (Button) convertView.findViewById(R.id.btn_check);
			
			convertView.setTag(hold);
		}
		
		
		
		
		// 绘制联系人名称
		hold.name.setText(mContactsName.get(position));
		// 绘制联系人号码
		hold.phone.setText(mContactsNumber.get(position));
		hold.check.setBackgroundResource(R.drawable.private_comm_checkbox_uncheck);
		
		return convertView;
	}
	
	static class ViewHold{
		static TextView name;
		static TextView phone;
		static Button check;
	}

}