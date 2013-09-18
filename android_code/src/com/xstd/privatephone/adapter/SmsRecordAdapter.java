package com.xstd.privatephone.adapter;

import java.util.Date;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.tools.Tools;

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

public class SmsRecordAdapter extends CursorAdapter {
	private static Context mContext;
	private String phoneType;
	private int picId;

	public SmsRecordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();
		
		String phone_number = cursor.getString(cursor.getColumnIndex("PHONE_NUMBER"));
		int count = cursor.getInt(cursor.getColumnIndex("COUNT"));
		Long lastedContact = cursor.getLong(cursor.getColumnIndex("LASTED_CONTACT"));
		String lasted_data = cursor.getString(cursor.getColumnIndex("LASTED_DATA"));
		
		Tools.logSh(phone_number+"::"+count+"::"+lastedContact+"::"+lasted_data);
		
		views.isopen.setBackgroundResource(R.drawable.private_sms_read);
		views.tv_phone_num.setText(phone_number);
		views.sms_tv_date.setText(new Date(lastedContact).toLocaleString());
		views.sms_tv_content.setText(lasted_data);
		views.sms_tv_count.setText(count+"");
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_smsrecord_item, null);
		ViewHold hold = new ViewHold();
		
		hold.isopen = (ImageView) view.findViewById(R.id.sms_iv_inorout);
		hold.tv_phone_num = (TextView) view.findViewById(R.id.sms_tv_num);
		hold.sms_tv_content = (TextView) view.findViewById(R.id.sms_tv_content);
		hold.sms_tv_count = (TextView) view.findViewById(R.id.sms_tv_count);
		hold.sms_tv_date = (TextView) view.findViewById(R.id.sms_tv_date);
		
		view.setTag(hold);
		
		return view;
	}
	
	static class ViewHold{
		
		ImageView isopen;
		TextView sms_tv_date;
		TextView sms_tv_content;
		TextView tv_phone_num;
		TextView sms_tv_count;
	}

}