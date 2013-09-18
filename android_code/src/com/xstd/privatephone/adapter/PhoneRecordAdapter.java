package com.xstd.privatephone.adapter;

import java.util.Date;

import com.xstd.pirvatephone.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneRecordAdapter extends CursorAdapter {
	private static Context mContext;
	private String phoneType;
	private int picId;

	public PhoneRecordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();
		
		Integer id = cursor.getInt(cursor.getColumnIndex("_id"));
		String phone_number = cursor.getString(cursor.getColumnIndex("PHONE_NUMBER"));
		Long date = cursor.getLong(cursor.getColumnIndex("START_TIME"));
		Long duration = cursor.getLong(cursor.getColumnIndex("DURATION"));
		int type = cursor.getInt(cursor.getColumnIndex("TYPE"));
		String name = cursor.getString(cursor.getColumnIndex("NAME"));
		
		phoneType = "";
		picId = 0; 
		if(type == 1){//
			phoneType = "拨入电话";
			picId = R.drawable.privacy_incoming;
		}else{
			phoneType = "拨入电话";
			picId = R.drawable.privacy_outgoing;
		}
		if(name==null){
			views.tv_phone_num.setText(phone_number);
		}else{
			views.tv_phone_num.setText(name);
		}
		
		views.inorout.setBackgroundResource(picId);
		views.tv_type.setText(phoneType);
		views.tv_count.setText("(10)");
		views.tv_date.setText(new Date(date).toLocaleString());
		views.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_record_item, null);
		ViewHold views = new ViewHold();
		
		views.inorout = (ImageView) view.findViewById(R.id.dial_iv_inorout);
		views.tv_type = (TextView) view.findViewById(R.id.dial_tv_phone_type);
		views.tv_phone_num = (TextView) view.findViewById(R.id.dial_tv_phone_num);
		views.tv_date = (TextView) view.findViewById(R.id.dial_tv_date);
		views.tv_count = (TextView) view.findViewById(R.id.dial_tv_count);
		views.btn_dail = (Button) view.findViewById(R.id.dial_btn_dail);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		ImageView inorout;
		TextView tv_date;
		TextView tv_type;
		TextView tv_phone_num;
		TextView tv_count;
		Button btn_dail;
	}

}