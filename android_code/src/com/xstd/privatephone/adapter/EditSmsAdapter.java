package com.xstd.privatephone.adapter;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.image.ImageUtils;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.DateUtils;

public class EditSmsAdapter extends CursorAdapter {
	private ArrayList<String> selectContacts = new ArrayList<String>();

	private static Context mContext;
	private String phoneType;

	// 联系人字段
	private String[] CONTACT_PROJECTION = new String[] { PhoneLookup._ID,
			PhoneLookup.DISPLAY_NAME };

	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;
	
	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 0;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 1;

	/** 获取库Phone表字段 **/
	private static final String[] PHONES_ID = new String[] {
			ContactInfoDao.Properties.Contact_id.columnName,
			ContactInfoDao.Properties.Photo_id.columnName };

	@SuppressWarnings("deprecation")
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

		views.sms_tv_date.setText(DateUtils.parseDate(lastedContact));
		views.sms_tv_content.setText(lasted_data);

		if (selectContacts.contains(phone_number)) {
			views.checkbox.setChecked(true);
		}else{
			views.checkbox.setChecked(false);
		}
		
		AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(mContext,
				phone_number, views.sms_iv_pic);
		asyncLoader.execute();

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_sms_edit_item, null);
		ViewHold hold = new ViewHold();

		hold.checkbox = (CheckBox) view.findViewById(R.id.edit_sms_checkbox);
		hold.sms_iv_pic = (ImageView) view.findViewById(R.id.edit_sms_iv_pic);
		hold.tv_phone_name = (TextView) view.findViewById(R.id.edit_sms_tv_name);
		hold.sms_tv_content = (TextView) view.findViewById(R.id.edit_sms_tv_content);
		hold.sms_tv_date = (TextView) view.findViewById(R.id.edit_sms_tv_date);
		hold.sms_tv_count = (TextView) view.findViewById(R.id.edit_sms_tv_count);
		hold.tv_hidden_number = (TextView) view
				.findViewById(R.id.edit_sms_tv_number);

		view.setTag(hold);

		return view;
	}
	
	public void notifyChange(ArrayList<String> numbers) {
		selectContacts = numbers;
		notifyDataSetChanged();
	}

	static class ViewHold {

		ImageView sms_iv_pic;
		TextView sms_tv_date;
		TextView sms_tv_content;
		TextView tv_phone_name;
		TextView sms_tv_count;
		TextView tv_hidden_number;
		CheckBox checkbox;
	}
	
	private class AsyncBitmapLoader extends AsyncTask<Void, Void, Bitmap> {

		private Context mContext;
		private String mPhoneNumber;
		private ImageView bm;

		public AsyncBitmapLoader(Context context, String phoneNumber,
				ImageView bm) {
			this.mContext = context;
			this.mPhoneNumber = phoneNumber;
			this.bm = bm;
		}

		@Override
		public void onPreExecute() {
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			return loadPicture(mPhoneNumber);
		}

		@Override
		public void onPostExecute(Bitmap bitmap) {
			this.bm.setImageBitmap(bitmap);
		}

	}

	private Bitmap loadPicture(String phoneNumber) {
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);

		SQLiteDatabase database = contactInfoDao.getDatabase();

		Cursor cursor = database.query(ContactInfoDao.TABLENAME, PHONES_ID,
				ContactInfoDao.Properties.Phone_number.columnName + "=?",
				new String[] { phoneNumber }, null, null, null);
		
		if(cursor!=null && cursor.getCount()>0){
			while(cursor.moveToNext()){
				Long contactId = cursor.getLong(PHONES_CONTACT_ID_INDEX);
				Long photoId = cursor.getLong(PHONES_PHOTO_ID_INDEX);
				Bitmap contactPhoto = null;
				
				if(photoId==0){
					contactPhoto = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.private_comm_contact_icon_default);
					
				}else{
					if (contactId == null) {
						return null;
					}else{
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactId);

					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(mContext.getContentResolver(), uri);

					contactPhoto = BitmapFactory.decodeStream(input);

					contactPhoto = ImageUtils.createRoundedCornerBitmap(contactPhoto, 48,
							48, 0.6f, true, true, true, true);
					}
				}

				return contactPhoto;
			}
			cursor.close();
		}
		return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.private_comm_contact_icon_default);
	}

}