package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.Date;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.ContactUtils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AddFromSmsRecordAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<Integer> _ids = new ArrayList<Integer>();

	@SuppressWarnings("deprecation")
	public AddFromSmsRecordAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHold holder = (ViewHold) view.getTag();
		Integer type = cursor.getInt(cursor
				.getColumnIndex("type"));
		final Integer _id = cursor.getInt(cursor.getColumnIndex("_id"));
		// phone_number
		final String phone_number = cursor.getString(cursor
				.getColumnIndex("address"));
		// lasted date
		final Long date = cursor.getLong(cursor
				.getColumnIndex("date"));

		String body = cursor.getString(cursor
				.getColumnIndex("body"));

		// 适配数据到控件
		String name = ContactUtils.queryContactName(mContext, phone_number);
		
		if (name == null || "".equals(name)) {
			name = phone_number;
		} 
		
		holder.tv_hidden.setText(phone_number);
		holder.date.setText(body);
		holder.tv_name.setText(name);
		holder.tv_id.setText(_id+"");
		holder.tv_type.setText("["+new Date(date).toLocaleString()+"]");

		if(_ids.contains(_id)){
			holder.checkbox.setChecked(true);
		}else{
			holder.checkbox.setChecked(false);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = inflater.inflate(
				R.layout.private_add_from_sms_item, null);
		ViewHold holder = new ViewHold();
		holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
		holder.date = (TextView) view.findViewById(R.id.tv_phone_num);
		holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
		holder.tv_hidden = (TextView) view.findViewById(R.id.tv_hidden);
		holder.tv_id = (TextView) view.findViewById(R.id.tv_id);
		holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);

		view.setTag(holder);
		return view;
	}

	private static class ViewHold {
		TextView tv_name;
		TextView tv_hidden;
		TextView tv_id;
		TextView date;
		TextView tv_type;
		CheckBox checkbox;
	}
	public void notifyChange(ArrayList<Integer> ids){
		_ids = ids;
		notifyDataSetChanged();
	}


}
