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

	@SuppressWarnings("deprecation")
	public PhoneRecordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();
		
		int count = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName));
		int type = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Type.columnName));
		String phone_number = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
		Long date = cursor.getLong(cursor.getColumnIndex(PhoneRecordDao.Properties.Date.columnName));
		String name = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Name.columnName));
		Long duration = cursor.getLong(cursor.getColumnIndex(PhoneRecordDao.Properties.Duration.columnName));
		Tools.logSh("name======================="+name);
		phoneType = "";
		picId = 0; 
		if(type == 1){//
			phoneType = "拨入电话";
			picId = R.drawable.private_comm_pic_incomming;
		}else if(type == 2){
			phoneType = "呼出电话";
			picId = R.drawable.private_comm_pic_outgoing;
			
		}else if(type == 3){
			phoneType = "来电未接通";
			picId = R.drawable.private_comm_pic_missing;
		}else{
			phoneType = "其它";
			picId = R.drawable.private_comm_pic_missing;
		}
		
		
		if(name==null || "".equals(name)){
			views.tv_name.setText("未知联系人");
		}else{
			views.tv_name.setText(name);
		}
		
		views.iv_pic.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
		views.tv_number.setText("( "+phone_number+" )");
		views.tv_date.setText(new Date(date).toLocaleString());
		views.iv_type.setBackgroundResource(picId);
		views.tv_phone_belong.setText("    北京");
		views.tv_phone_duration.setText("( "+duration+" )");
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method 
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_phone_record_item, null);
		ViewHold views = new ViewHold();
		
		views.iv_pic = (ImageView) view.findViewById(R.id.phone_iv_pic);
		views.tv_name = (TextView) view.findViewById(R.id.tv_name);
		views.tv_number = (TextView) view.findViewById(R.id.tv_number);
		views.tv_date = (TextView) view.findViewById(R.id.tv_phone_date);
		views.iv_type = (ImageView) view.findViewById(R.id.iv_type);
		views.tv_phone_belong = (TextView) view.findViewById(R.id.tv_phone_belong);
		views.tv_phone_duration = (TextView) view.findViewById(R.id.tv_phone_duration);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		ImageView iv_pic;
		TextView tv_name;
		TextView tv_number;
		TextView tv_phone_belong;
		TextView tv_phone_duration;
		TextView tv_date;
		ImageView iv_type;
	}

}