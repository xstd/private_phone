package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.Date;

import com.xstd.pirvatephone.R;

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

public class AddFromPhoneRecordAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<Integer> _ids = new ArrayList<Integer>();

	@SuppressWarnings("deprecation")
	public AddFromPhoneRecordAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHold holder = (ViewHold) view.getTag();
		final Integer _id = cursor.getInt(cursor.getColumnIndex("_id"));
		// 得到手机号码
		final String number = cursor.getString(cursor.getColumnIndex("number"));
		// 得到联系人名称
		final Long start_time = cursor.getLong(cursor.getColumnIndex("date"));
		// 通话持续时间
		Long duration = cursor.getLong(cursor.getColumnIndex("duration"));
		// 通话类型
		int type = cursor.getInt(cursor.getColumnIndex("type"));
		// 通化人姓名
		String name = cursor.getString(cursor.getColumnIndex("name"));
		if(name==null || "".equals(name)){
			name = number;
		}
		
		String strType = "";
		switch (type) {
		case 1:
			strType = "来电";
			break;

		case 2:
			strType = "去电";
			break;
		case 3:
			strType = "未接";
			break;
		}
		
		holder.tv_hidden.setText(number);
		holder.date.setText(strType);
		holder.tv_name.setText(name);
		holder.tv_id.setText(_id+"");
		holder.tv_type.setText("["+new Date(start_time).toLocaleString()+"]");
		
		if(_ids.contains(_id)){
			holder.checkbox.setChecked(true);
		}else{
			holder.checkbox.setChecked(false);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = inflater.inflate(
				R.layout.private_add_from_phone_item, null);
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

	public static class ViewHold {
		public TextView tv_name;
		public TextView tv_hidden;
		public TextView tv_id;
		public TextView date;
		public TextView tv_type;
		public CheckBox checkbox;
	}
	
	public void notifyChange(ArrayList<Integer> ids){
		_ids = ids;
		notifyDataSetChanged();
	}

}
