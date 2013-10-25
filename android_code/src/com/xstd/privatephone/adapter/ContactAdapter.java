package com.xstd.privatephone.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

public class ContactAdapter extends CursorAdapter {
	private Context mContext;

	public ContactAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();

		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		final String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		String name = cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));
		int type = cursor.getInt(cursor.getColumnIndex("TYPE"));

		if (TextUtils.isEmpty(name)) {
			name = phone_number;
		}

		String typeStr = "";
		if (type == 0) {
			typeStr = "[" + "正常接听" + "]";
		} else {
			typeStr = "[" + "立即挂断" + "]";
		}

		views.tv_name.setText(name);
		views.tv_type.setText(typeStr);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-",
				""));
		views.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);

		views.btn_dail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phone_number != null && !phone_number.equals("")) {

					// 封装一个拨打电话的intent，并且将电话号码包装成一个Uri对象传入
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + phone_number));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);// 内部类
				}
			}
		});

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_contact_item, null);
		ViewHold views = new ViewHold();
		views.tv_name = (TextView) view.findViewById(R.id.tv_name);
		views.tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		views.tv_type = (TextView) view.findViewById(R.id.tv_type);
		views.btn_dail = (Button) view.findViewById(R.id.contact_btn_dail);

		view.setTag(views);

		return view;
	}

	static class ViewHold {
		TextView tv_name;
		TextView tv_phone_num;
		TextView tv_type;
		Button btn_dail;
	}

}