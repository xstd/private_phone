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
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;

public class AddNotIntereptAdapter extends CursorAdapter {
	private Context mContext;
	private final ArrayList<String> selectContacts = new ArrayList<String>();
	private ArrayList<CheckBox> checkBoxs = new ArrayList<CheckBox>();

	@SuppressWarnings("deprecation")
	public AddNotIntereptAdapter(Context context, Cursor cursor) {
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
		
		
		//views.iv_icon.setBackgroundResource(R.drawable.private_comm_contact_icon_default);
		views.tv_name.setText(name);
		views.tv_type.setText(typeStr);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-",
				""));

		if (selectContacts.contains(phone_number)) {
			views.checkbox.setChecked(true);
		}

		checkBoxs.add(views.checkbox);

		views.checkbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							selectContacts.add(phone_number);
						} else {
							if (selectContacts.contains(phone_number)) {
								selectContacts.remove(phone_number);
							}
						}
					}
				});

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_add_not_interept_item, null);
		ViewHold views = new ViewHold();
		views.checkbox = (CheckBox) view.findViewById(R.id.not_interept_checkbox);
		//views.iv_icon = (ImageView) view.findViewById(R.id.not_interept_iv_pic);
		
		views.tv_name = (TextView) view.findViewById(R.id.not_interept_tv_name);
		views.tv_phone_num = (TextView) view.findViewById(R.id.not_interept_tv_phone_num);
		views.tv_type = (TextView) view.findViewById(R.id.not_interept_tv_type);
		views.tv_phone_belong = (TextView) view.findViewById(R.id.not_interept_tv_phone_belong);

		view.setTag(views);

		return view;
	}

	public void notifyChange(boolean flag) {
		for (int i = 0; i < checkBoxs.size(); i++) {
			checkBoxs.get(i).setChecked(flag);
		}
	}

	static class ViewHold {
		TextView tv_name;
		TextView tv_phone_num;
		TextView tv_type;
		TextView tv_phone_belong;
		CheckBox checkbox;
		//ImageView iv_icon;
	}

}