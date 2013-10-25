package com.xstd.privatephone.adapter;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.privatephone.tools.Tools;

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
		
		int count = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName));
		int type = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Type.columnName));
		String phone_number = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
		Long date = cursor.getLong(cursor.getColumnIndex(PhoneRecordDao.Properties.Date.columnName));
		String name = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Name.columnName));
		Tools.logSh("name======================="+name);
		phoneType = "";
		picId = 0; 
		if(type == 1){//
			phoneType = "拨入电话";
			picId = R.drawable.privacy_incoming;
		}else if(type == 2){
			phoneType = "呼出电话";
			picId = R.drawable.privacy_outgoing;
			
		}else if(type == 3){
			phoneType = "来电未接通";
			picId = R.drawable.privacy_incoming;
		}else{
			phoneType = "其它";
			picId = R.drawable.privacy_incoming;
		}
		
		
		if(name==null || name==""){
			views.tv_phone_num.setText(phone_number);
		}else{
			views.tv_phone_num.setText(name);
		}
		
		views.inorout.setBackgroundResource(picId);
		views.tv_type.setText(phoneType);
		if(count!=0){
			views.tv_count.setText("("+count+")");
		}else{
			views.tv_count.setText("(0)");
		}
		
		views.tv_date.setText(new Date(date).toLocaleString());
		views.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method 
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_phone_record_item, null);
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