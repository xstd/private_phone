package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.privatephone.tools.Tools;

public class EditPhoneAdapter extends CursorAdapter {
	private final ArrayList<String> selectContacts = new ArrayList<String>();
	private static Context mContext;
	private String phoneType;
	private int picId;

	public EditPhoneAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();
		
		int count = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName));
		int type = cursor.getInt(cursor.getColumnIndex(PhoneRecordDao.Properties.Type.columnName));
		final String phone_number = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
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
			name = phone_number;
			views.tv_phone_num.setText(name);
		}
		
		views.tv_hidden_number.setText(phone_number);
		views.tv_hidden_number.setVisibility(View.GONE);
		views.inorout.setBackgroundResource(picId);
		views.tv_type.setText(phoneType);
		if(count!=0){
			views.tv_count.setText("("+count+")");
		}else{
			views.tv_count.setText("(0)");
		}
		
		views.tv_date.setText(new Date(date).toLocaleString());
		if(selectContacts.contains(phone_number)){
			views.checkbox.setChecked(true);
		}
		
		views.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					selectContacts.add(phone_number);
				}else{
					if(selectContacts.contains(phone_number)){
						selectContacts.remove(phone_number);
					}
				}
			}
		});
		
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method 
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_phone_edit_item, null);
		ViewHold views = new ViewHold();
		
		views.inorout = (ImageView) view.findViewById(R.id.dial_iv_inorout);
		views.tv_type = (TextView) view.findViewById(R.id.dial_tv_phone_type);
		views.tv_phone_num = (TextView) view.findViewById(R.id.dial_tv_phone_num);
		views.tv_hidden_number = (TextView) view.findViewById(R.id.tv_hidden_number);
		views.tv_date = (TextView) view.findViewById(R.id.dial_tv_date);
		views.tv_count = (TextView) view.findViewById(R.id.dial_tv_count);
		views.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
		
		view.setTag(views);
		
		return view;
	}
	
	static class ViewHold{
		
		ImageView inorout;
		TextView tv_date;
		TextView tv_type;
		TextView tv_phone_num;
		TextView tv_hidden_number;
		TextView tv_count;
		CheckBox checkbox;
	}

}