package com.xstd.privatephone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;

public class SmsDetailAdapter extends CursorAdapter {
	private static Context mContext;
	
	public SmsDetailAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		ViewHold views = (ViewHold) view.getTag();
		/*private Long id;
	    private String phone_number;
	    private String name;
	    private Long date;
	    private Long duration;
	    private Integer type;*/
		String phone_number = cursor.getString(cursor.getColumnIndex(SmsDetailDao.Properties.Phone_number.columnName));
		String body = cursor.getString(cursor.getColumnIndex(SmsDetailDao.Properties.Data.columnName));
		Long date = cursor.getLong(cursor.getColumnIndex(SmsDetailDao.Properties.Date.columnName));
		Integer type = cursor.getInt(cursor.getColumnIndex(SmsDetailDao.Properties.Thread_id.columnName));
		if(date!=null){
			views.date.setText( DateFormat.format("yyyy-MM-dd kk.mm.ss", date).toString());
		}else{
			views.date.setText("");
		}
	
		
		if(type == 1){//收
			views.body_left.setText(body);
			views.body_left.setVisibility(View.VISIBLE);
			views.body_right.setVisibility(View.GONE);
		}else{
			views.body_right.setText(body);
			views.body_right.setVisibility(View.VISIBLE);
			views.body_left.setVisibility(View.GONE);
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_sms_detail_item, null);
		ViewHold views = new ViewHold();
		
		views.date = (TextView) view.findViewById(R.id.tv_time);
		views.body_left = (TextView) view.findViewById(R.id.tv_content_in_left);
		views.body_right = (TextView) view.findViewById(R.id.tv_content_out_right);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		TextView date;
		TextView body_left;
		TextView body_right;
	}

}
