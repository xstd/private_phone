package com.xstd.privatephone.adapter;

import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.MakeCallUtils;

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
		Long icon_id = cursor.getLong(cursor.getColumnIndex("ICON_ID"));

		if (TextUtils.isEmpty(name)) {
			name = phone_number;
		}

		String typeStr = "";
		if (type == 0) {
			typeStr = "[" + "正常接听" + "]";
		} else {
			typeStr = "[" + "立即挂断" + "]";
		}

		// 得到联系人头像Bitamp

		Bitmap contactPhoto = null;

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		if (icon_id > 0) {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, icon_id);

			ContentResolver resolver = mContext.getContentResolver();
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(resolver, uri);

			contactPhoto = BitmapFactory.decodeStream(input);

		} else {

			contactPhoto = BitmapFactory.decodeResource(
					mContext.getResources(),
					R.drawable.private_comm_contact_icon_default);

		}

		views.iv_pic.setImageBitmap(contactPhoto);

		views.tv_name.setText(name);
		views.tv_type.setText(typeStr);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-",
				""));
		views.tv_phone_belong.setText("   北京");
		views.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_contact_item, null);
		ViewHold views = new ViewHold();
		views.iv_pic = (ImageView) view.findViewById(R.id.contact_iv_pic);
		views.tv_name = (TextView) view.findViewById(R.id.tv_name);
		views.tv_phone_num = (TextView) view.findViewById(R.id.tv_phone_num);
		views.tv_phone_belong = (TextView) view
				.findViewById(R.id.tv_phone_belong);
		views.tv_type = (TextView) view.findViewById(R.id.tv_type);
		views.btn_dail = (Button) view.findViewById(R.id.contact_btn_dail);

		view.setTag(views);

		return view;
	}

	static class ViewHold {
		ImageView iv_pic;
		TextView tv_name;
		TextView tv_phone_num;
		TextView tv_phone_belong;
		TextView tv_type;
		Button btn_dail;
	}

}