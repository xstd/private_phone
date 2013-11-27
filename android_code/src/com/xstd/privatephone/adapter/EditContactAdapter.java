package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;

public class EditContactAdapter extends CursorAdapter {
	private Context mContext;
	private ArrayList<String> selectContacts = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	public EditContactAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {

		ViewHold views = (ViewHold) view.getTag();

		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		final String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		String name = cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));
		int type = cursor.getInt(cursor
				.getColumnIndex(ContactInfoDao.Properties.Type.columnName));
		if (TextUtils.isEmpty(name)) {
			name = phone_number;
		}

		String typeStr = "";
		if (type == 0) {
			typeStr = "[" + "正常接听" + "]";
		} else {
			typeStr = "[" + "立即挂断" + "]";
		}
		
		//checkBoxs.add(views.checkbox);
		views.iv_icon.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
		views.tv_name.setText(name);
		views.tv_type.setText(typeStr);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-",
				""));

		if (selectContacts.contains(phone_number)) {
			views.checkbox.setChecked(true);
		}else{
			views.checkbox.setChecked(false);
		}

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_contact_edit_item, null);
		ViewHold views = new ViewHold();
		views.checkbox = (CheckBox) view.findViewById(R.id.edit_contact_checkbox);
		views.iv_icon = (ImageView) view.findViewById(R.id.edit_contact_iv_pic);
		
		views.tv_name = (TextView) view.findViewById(R.id.edit_contact_tv_name);
		views.tv_phone_num = (TextView) view.findViewById(R.id.edit_contact_phone_num);
		views.tv_type = (TextView) view.findViewById(R.id.edit_contact_tv_type);
		views.tv_phone_belong = (TextView) view.findViewById(R.id.edit_contact_phone_belong);

		view.setTag(views);

		return view;
	}

	public void notifyChange(ArrayList<String> numbers) {
		selectContacts = numbers;
		notifyDataSetChanged();
	}
	

	static class ViewHold {
		TextView tv_name;
		TextView tv_phone_num;
		TextView tv_type;
		TextView tv_phone_belong;
		CheckBox checkbox;
		ImageView iv_icon;
	}

}