package com.xstd.privatephone.adapter;

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
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.privatephone.tools.Tools;

public class PhoneDetailAdapter extends CursorAdapter {

	private static Context mContext;

	public PhoneDetailAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;

	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		/*
		 * private Long id; 
		 * private String phone_number; 
		 * private String name;
		 * private Integer type; 
		 * private Long date; 
		 * private Long duration;
		 */
		ViewHold views = (ViewHold) view.getTag();

		Integer type = cursor.getInt(cursor
				.getColumnIndex(PhoneDetailDao.Properties.Type.columnName));
		Long date = cursor.getLong(cursor
				.getColumnIndex(PhoneDetailDao.Properties.Date.columnName));
		
		Long duration = cursor.getLong(cursor
				.getColumnIndex(PhoneDetailDao.Properties.Duration.columnName));
		
		Tools.logSh("type="+type+":::"+"date"+date+"duration"+duration);
		
		String typeStr = "";
		if(type==1){
			typeStr = "呼入电话";
			views.type_pic.setBackgroundResource(R.drawable.privacy_incoming);
		}else{
			typeStr = "呼出电话";
			views.type_pic.setBackgroundResource(R.drawable.privacy_outgoing);
		}
		views.type.setText(typeStr);
		views.date.setText(DateFormat.format("yyyy-MM-dd kk.mm.ss", date).toString()+"("+duration+"秒)");

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_phone_detail_item, null);
		ViewHold views = new ViewHold();

		views.type_pic = (ImageView) view.findViewById(R.id.phone_iv_inorout);
		views.type = (TextView) view.findViewById(R.id.phone_tv_type);
		views.date = (TextView) view.findViewById(R.id.phone_tv_date);

		view.setTag(views);
		return view;
	}

	static class ViewHold {
		ImageView type_pic;
		TextView type;
		TextView date;
	}

}
