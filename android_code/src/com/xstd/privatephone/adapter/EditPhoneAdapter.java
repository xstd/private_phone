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
import com.xstd.pirvatephone.utils.DateUtils;
import com.xstd.privatephone.tools.Tools;

public class EditPhoneAdapter extends CursorAdapter {
	private ArrayList<String> selectContacts = new ArrayList<String>();


	private Context mContext;
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
		String phone_number = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
		Long date = cursor.getLong(cursor.getColumnIndex(PhoneRecordDao.Properties.Date.columnName));
		String name = cursor.getString(cursor.getColumnIndex(PhoneRecordDao.Properties.Name.columnName));
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
			name = phone_number;
		}
		views.tv_name.setText(name);
		views.tv_duration.setText("23:10");
		views.tv_number.setText(" ( "+phone_number+" )");
		views.iv_pic.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
		views.tv_type.setBackgroundResource(picId);
		views.tv_phone_belong.setText("  北京");
		
		views.tv_date.setText(DateUtils.parseDate(date));
		if(selectContacts.contains(phone_number)){
			views.checkbox.setChecked(true);
		}else{
			views.checkbox.setChecked(false);
		}
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method 
		View view = LayoutInflater.from(mContext).inflate(R.layout.private_phone_edit_item, null);
		ViewHold views = new ViewHold();
		
		views.checkbox = (CheckBox) view.findViewById(R.id.iv_check);
		views.iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
		views.tv_type = (ImageView) view.findViewById(R.id.iv_type);
		views.tv_name = (TextView) view.findViewById(R.id.tv_name);
		views.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
		views.tv_number = (TextView) view.findViewById(R.id.tv_number);
		views.tv_date = (TextView) view.findViewById(R.id.tv_phone_date);
		views.tv_phone_belong = (TextView) view.findViewById(R.id.tv_phone_belong);
		
		view.setTag(views);
		
		return view;
	}
	
	public void notifyChange(ArrayList<String> numbers) {
		selectContacts = numbers;
		notifyDataSetChanged();
	}
	
	static class ViewHold{
		
		ImageView iv_pic;
		TextView tv_date;
		ImageView tv_type;
		TextView tv_name;
		TextView tv_number;
		TextView tv_duration;
		TextView tv_phone_belong;
		CheckBox checkbox;
	}

}