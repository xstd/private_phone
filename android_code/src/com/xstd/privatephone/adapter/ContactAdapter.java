package com.xstd.privatephone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

public class ContactAdapter extends CursorAdapter {
	private static Context mContext;

	public ContactAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();
		
		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		String phone_number = cursor.getString(cursor.getColumnIndex("PHONE_NUMBER"));
		String name = cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));
		
		if(TextUtils.isEmpty(name)){
			name = phone_number;
		}
		
		views.tv_name.setText(name);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-", ""));
		views.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_contact_item, null);
		ViewHold views = new ViewHold();
		views.tv_name = (TextView) view.findViewById(R.id.tv_name);
		views.tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		views.btn_dail = (Button) view.findViewById(R.id.contact_btn_dail);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		TextView tv_name;
		TextView tv_phone_num;
		Button btn_dail;
	}

}