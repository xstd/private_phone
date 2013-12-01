package com.xstd.privatephone.adapter;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.image.ImageUtils;
import com.xstd.pirvatephone.R;

public class ContactAdapter extends CursorAdapter {
	private Context mContext;

	@SuppressWarnings("deprecation")
	public ContactAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold hold = (ViewHold) view.getTag();

		int id = cursor.getInt(cursor.getColumnIndex("_id"));
		final String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		String name = cursor.getString(cursor.getColumnIndex("DISPLAY_NAME"));
		int type = cursor.getInt(cursor.getColumnIndex("TYPE"));
		Long contact_id = cursor.getLong(cursor.getColumnIndex("CONTACT_ID"));
		Long photo_id = cursor.getLong(cursor.getColumnIndex("PHOTO_ID"));

		if (TextUtils.isEmpty(name)) {
			name = phone_number;
		}

		String typeStr = "";
		if (type == 0) {
			typeStr = "[" + "正常接听" + "]";
		} else {
			typeStr = "[" + "立即挂断" + "]";
		}

		hold.tv_name.setText(name);
		hold.tv_type.setText(typeStr);
		hold.tv_phone_num.setText(phone_number.replace(" ", "")
				.replace("-", ""));
		hold.tv_phone_belong.setText("   北京");
		hold.btn_dail.setBackgroundResource(R.drawable.private_dial_normal);

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		Log.e("PhotoId==", photo_id + "   ContactId=="+contact_id);
		if (photo_id > 0) {
			AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(mContext,
					contact_id, hold.iv_pic);
			asyncLoader.execute();

		} else {
			hold.iv_pic
					.setImageResource(R.drawable.private_comm_contact_icon_default);
		}

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

	private class AsyncBitmapLoader extends AsyncTask<Void, Void, Bitmap> {

		private Context mContext;
		private Long mContactId;
		private ImageView bm;

		public AsyncBitmapLoader(Context context, Long contactId, ImageView bm) {
			this.mContext = context;
			this.mContactId = contactId;
			this.bm = bm;
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			return loadPicture(mContactId);
		}

		@Override
		public void onPostExecute(Bitmap integer) {
			this.bm.setImageBitmap(integer);
		}

	}

	private Bitmap loadPicture(Long contactId) {
		Bitmap contactPhoto = null;
		if (contactId == null) {
			return null;
		}
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);

		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(mContext.getContentResolver(), uri);

		contactPhoto = BitmapFactory.decodeStream(input);
		
		contactPhoto = ImageUtils.createRoundedCornerBitmap(contactPhoto, 48,
				48, 0.6f, true, true, true, true);
		
		return contactPhoto;
	}

}