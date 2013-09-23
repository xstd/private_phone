package com.xstd.privatephone.adapter;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.adapter.PhoneRecordAdapter.ViewHold;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsDetailAdapter extends CursorAdapter {
	private static Context mContext;
	
	public SmsDetailAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		/*private Long id;
	    private String phone_number;
	    private String name;
	    private Long date;
	    private Long duration;
	    private Integer type;*/
		String phone_number = cursor.getString(cursor.getColumnIndex("phone_number"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		Long date = cursor.getLong(cursor.getColumnIndex("date"));
		Long duration = cursor.getLong(cursor.getColumnIndex("duration"));
		Integer type = cursor.getInt(cursor.getColumnIndex("type"));
		
		String phoneType = "";
		int picId = 0; 
		if(type == 1){//
			phoneType = "拨入电话";
			picId = R.drawable.privacy_incoming;
		}else{
			phoneType = "拨入电话";
			picId = R.drawable.privacy_outgoing;
		}
		
		
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_sms_left_item, null);
		ViewHold views = new ViewHold();
		
		views.date = (TextView) view.findViewById(R.id.tv_time_in);
		views.body = (TextView) view.findViewById(R.id.tv_content_in);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		TextView date;
		TextView body;
	}

}
