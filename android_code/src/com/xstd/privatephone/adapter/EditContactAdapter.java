package com.xstd.privatephone.adapter;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.image.ImageUtils;
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

		// checkBoxs.add(views.checkbox);
		views.tv_name.setText(name);
		views.tv_type.setText(typeStr);
		views.tv_phone_num.setText(phone_number.replace(" ", "").replace("-",
				""));

		if (selectContacts.contains(phone_number)) {
			views.checkbox.setChecked(true);
		} else {
			views.checkbox.setChecked(false);
		}

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		Log.e("PhotoId==", photo_id + "   ContactId==" + contact_id);
		if (photo_id > 0) {
			AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(mContext,
					contact_id, views.iv_pic);
			asyncLoader.execute();

		} else {
			views.iv_pic
					.setImageResource(R.drawable.private_comm_contact_icon_default);
		}

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_contact_edit_item, null);
		ViewHold views = new ViewHold();
		views.checkbox = (CheckBox) view
				.findViewById(R.id.edit_contact_checkbox);
		views.iv_pic = (ImageView) view.findViewById(R.id.edit_contact_iv_pic);

		views.tv_name = (TextView) view.findViewById(R.id.edit_contact_tv_name);
		views.tv_phone_num = (TextView) view
				.findViewById(R.id.edit_contact_phone_num);
		views.tv_type = (TextView) view.findViewById(R.id.edit_contact_tv_type);
		views.tv_phone_belong = (TextView) view
				.findViewById(R.id.edit_contact_phone_belong);

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
		ImageView iv_pic;
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