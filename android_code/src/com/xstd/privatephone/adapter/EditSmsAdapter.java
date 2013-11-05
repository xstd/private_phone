package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.xstd.pirvatephone.R;

public class EditSmsAdapter extends CursorAdapter {
	private final ArrayList<String> selectContacts = new ArrayList<String>();
	private static Context mContext;
	private String phoneType;
	private int picId;

	// 联系人字段
	private String[] CONTACT_PROJECTION = new String[] { PhoneLookup._ID,
			PhoneLookup.DISPLAY_NAME };

	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;

	public EditSmsAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();

		final String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		int msg_count = cursor.getInt(cursor.getColumnIndex("COUNT"));
		Long lastedContact = cursor.getLong(cursor
				.getColumnIndex("LASTED_CONTACT"));
		String lasted_data = cursor.getString(cursor
				.getColumnIndex("LASTED_DATA"));

		// 根据电话号码 查询联系人信息
		String name = null;
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phone_number));
		Cursor contactCursor = mContext.getContentResolver().query(uri,
				CONTACT_PROJECTION, null, null, null);
		if (contactCursor.moveToFirst()) {
			// 查询到了联系人
			name = contactCursor.getString(DISPLAY_NAME_COLUMN_INDEX);
		}
		contactCursor.close();

		// 适配数据到控件

		if (name != null) {
			// 查询到了联系人
			views.tv_phone_name.setText(name);
		} else {
			// 没有查询到联系人
			views.tv_phone_name.setText(phone_number);
		}
		views.sms_tv_count.setText("(" + msg_count + ")");
		views.tv_hidden_number.setText(phone_number);
		views.tv_hidden_number.setVisibility(View.GONE);
		views.isopen.setBackgroundResource(R.drawable.private_sms_read);

		views.sms_tv_date.setText(new Date(lastedContact).toLocaleString());
		views.sms_tv_content.setText(lasted_data);

		if (selectContacts.contains(phone_number)) {
			views.checkbox.setChecked(true);
		}

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
				R.layout.private_sms_edit_item, null);
		ViewHold hold = new ViewHold();

		hold.isopen = (ImageView) view.findViewById(R.id.sms_iv_inorout);
		hold.tv_phone_name = (TextView) view.findViewById(R.id.sms_tv_name);
		hold.sms_tv_content = (TextView) view.findViewById(R.id.sms_tv_content);
		hold.sms_tv_date = (TextView) view.findViewById(R.id.sms_tv_date);
		hold.sms_tv_count = (TextView) view.findViewById(R.id.sms_tv_count);
		hold.tv_hidden_number = (TextView) view
				.findViewById(R.id.tv_hidden_number);
		hold.checkbox = (CheckBox) view.findViewById(R.id.checkbox);

		view.setTag(hold);

		return view;
	}

	static class ViewHold {

		ImageView isopen;
		TextView sms_tv_date;
		TextView sms_tv_content;
		TextView tv_phone_name;
		TextView sms_tv_count;
		TextView tv_hidden_number;
		CheckBox checkbox;
	}

}