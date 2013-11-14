package com.xstd.privatephone.adapter;

import java.util.zip.Inflater;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;

public class SmsDetailAdapter extends CursorAdapter {
	private Context mContext;
	 private LayoutInflater inflater;
	
	@SuppressWarnings("deprecation")
	public SmsDetailAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
		 inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
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
		
		if(type == 1){//收
			views.tv_content_in_left.setVisibility(View.VISIBLE);
			views.tv_left_time.setVisibility(View.VISIBLE);
			views.iv_left_icon.setVisibility(View.VISIBLE);
			views.iv_right_icon.setVisibility(View.GONE);
			views.tv_content_in_right.setVisibility(View.GONE);
			views.tv_right_time.setVisibility(View.GONE);
			views.iv_left_icon.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
			views.tv_content_in_left.setText(body);
			views.tv_left_time.setText(DateFormat.format("yyyy-MM-dd kk.mm.ss", date).toString());
			
		}else{
			views.iv_left_icon.setVisibility(View.GONE);
			views.tv_content_in_left.setVisibility(View.GONE);
			views.tv_left_time.setVisibility(View.GONE);
			views.iv_right_icon.setVisibility(View.VISIBLE);
			views.tv_content_in_right.setVisibility(View.VISIBLE);
			views.tv_right_time.setVisibility(View.VISIBLE);
			
			views.iv_right_icon.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
			views.tv_content_in_right.setText(body);
			views.tv_right_time.setText(DateFormat.format("yyyy-MM-dd kk.mm.ss", date).toString());
		}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        View view = inflater.inflate(R.layout.private_sms_detail_item,null);
		ViewHold views = new ViewHold();
		views.iv_left_icon = (ImageView) view.findViewById(R.id.iv_left_icon);
		views.iv_right_icon = (ImageView) view.findViewById(R.id.iv_right_icon);
		views.tv_content_in_left = (TextView) view.findViewById(R.id.tv_content_in_left);
		views.tv_content_in_right = (TextView) view.findViewById(R.id.tv_content_in_right);
		views.tv_left_time = (TextView) view.findViewById(R.id.tv_left_time);
		views.tv_right_time = (TextView) view.findViewById(R.id.tv_right_time);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		ImageView iv_left_icon;
		ImageView iv_right_icon;
		TextView tv_content_in_left;
		TextView tv_content_in_right;
		TextView tv_left_time;
		TextView tv_right_time;
	}

}
