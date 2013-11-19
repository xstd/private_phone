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

public class EditModelAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> mNoIntereptNumbers = new ArrayList<String>();
	private ArrayList<String> mNoIntereptNames = new ArrayList<String>();
	
	private ArrayList<String> mSelectNumbers = new ArrayList<String>();

	public EditModelAdapter(Context context,
			ArrayList<String> noIntereptNumbers,ArrayList<String> noIntereptNames) {
		mContext = context;
		/*
		 * mContactsName = contactsName; mContactsNumber = contactsNumber;
		 */
		mNoIntereptNumbers = noIntereptNumbers;
		mNoIntereptNames= noIntereptNames;
	}

	public void updateUI() {
		notifyDataSetChanged();
	}

	public int getCount() {
		// 设置绘制数量
		return mNoIntereptNumbers.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return mNoIntereptNumbers.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold;
		if (convertView == null ) {
			hold = new ViewHold();
			convertView = View.inflate(mContext,
					R.layout.private_edit_model_item, null);
			hold = new ViewHold();

			hold.name = (TextView) convertView.findViewById(R.id.tv_name);
			hold.phone = (TextView) convertView.findViewById(R.id.tv_phone_num);
			hold.check = (CheckBox) convertView.findViewById(R.id.checkbox);

			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}

		// 绘制联系人名称
		hold.name.setText(mNoIntereptNumbers.get(position));
		// 绘制联系人号码
		hold.phone.setText(mNoIntereptNames.get(position));
		return convertView;
	}

	static class ViewHold {
		TextView name;
		TextView phone;
		CheckBox check;
	}

}