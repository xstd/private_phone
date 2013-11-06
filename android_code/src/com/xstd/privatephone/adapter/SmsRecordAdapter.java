package com.xstd.privatephone.adapter;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.ContactUtils;

public class SmsRecordAdapter extends CursorAdapter {
	private static Context mContext;
	private String phoneType;
	private int picId;

	// 联系人字段
	private String[] CONTACT_PROJECTION = new String[] { PhoneLookup._ID,
			PhoneLookup.DISPLAY_NAME };

	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;

	public SmsRecordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();

		String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		int msg_count = cursor.getInt(cursor.getColumnIndex("COUNT"));
		Long lastedContact = cursor.getLong(cursor
				.getColumnIndex("LASTED_CONTACT"));
		String lasted_data = cursor.getString(cursor
				.getColumnIndex("LASTED_DATA"));

		// 根据电话号码 查询联系人信息

		// 适配数据到控件
		String name = ContactUtils.queryContactName(mContext, phone_number);
		
		
		if (name == null || "".equals(name)) {
			name = phone_number;
		} 
		
		views.tv_phone_name.setText(name);
		views.tv_phone_number.setText(phone_number);
		views.sms_tv_count.setText("(" + msg_count + ")");

		views.isopen.setBackgroundResource(R.drawable.private_sms_read);
		

		views.sms_tv_date.setText(new Date(lastedContact).toLocaleString());
		views.sms_tv_content.setText(lasted_data);

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_sms_record_item, null);
		ViewHold hold = new ViewHold();

		hold.isopen = (ImageView) view.findViewById(R.id.sms_iv_inorout);
		hold.tv_phone_name = (TextView) view.findViewById(R.id.sms_tv_name);
		hold.tv_phone_number = (TextView) view.findViewById(R.id.sms_tv_number);
		hold.sms_tv_content = (TextView) view.findViewById(R.id.sms_tv_content);
		hold.sms_tv_date = (TextView) view.findViewById(R.id.sms_tv_date);
		hold.sms_tv_count = (TextView) view.findViewById(R.id.sms_tv_count);

		view.setTag(hold);

		return view;
	}

	static class ViewHold {

		ImageView isopen;
		TextView sms_tv_date;
		TextView sms_tv_content;
		TextView tv_phone_name;
		TextView tv_phone_number;
		TextView sms_tv_count;
	}

}