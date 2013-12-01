package com.xstd.privatephone.adapter;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.image.ImageUtils;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.pirvatephone.utils.DateUtils;

public class SmsRecordAdapter extends CursorAdapter {
	private Context mContext;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 0;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 1;

	/** 获取库Phone表字段 **/
	private static final String[] PHONES_ID = new String[] {
			ContactInfoDao.Properties.Contact_id.columnName,
			ContactInfoDao.Properties.Photo_id.columnName };

	@SuppressWarnings("deprecation")
	public SmsRecordAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHold views = (ViewHold) view.getTag();

		String phone_number = cursor.getString(cursor
				.getColumnIndex("PHONE_NUMBER"));
		int msg_count = cursor.getInt(cursor.getColumnIndex("COUNT"));
		Long lastedContact = cursor.getLong(cursor
				.getColumnIndex("LASTED_CONTACT"));
		String lasted_data = cursor.getString(cursor
				.getColumnIndex("LASTED_DATA"));

		// 根据电话号码 查询联系人信息

		// 适配数据到控件
		String name = ContactUtils.queryContactName(mContext, phone_number);
		
		
		if (name == null || "".equals(name)) {
			name = phone_number;
		} 
		
		views.tv_phone_name.setText(name);
		views.tv_phone_number.setText(phone_number);
		views.sms_tv_count.setText("(" + msg_count + ")");
		views.sms_tv_date.setText(DateUtils.parseDate(lastedContact));
		views.sms_tv_content.setText(lasted_data);
		
		AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(mContext,
				phone_number, views.sms_iv_pic);
		asyncLoader.execute();

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.private_sms_record_item, null);
		ViewHold hold = new ViewHold();

		hold.sms_iv_pic = (ImageView) view.findViewById(R.id.sms_iv_pic);
		hold.tv_phone_name = (TextView) view.findViewById(R.id.sms_tv_name);
		hold.tv_phone_number = (TextView) view.findViewById(R.id.sms_tv_number);
		hold.sms_tv_content = (TextView) view.findViewById(R.id.sms_tv_content);
		hold.sms_tv_date = (TextView) view.findViewById(R.id.sms_tv_date);
		hold.sms_tv_count = (TextView) view.findViewById(R.id.sms_tv_count);

		view.setTag(hold);

		return view;
	}

	static class ViewHold {

		ImageView sms_iv_pic;
		TextView sms_tv_date;
		TextView sms_tv_content;
		TextView tv_phone_name;
		TextView tv_phone_number;
		TextView sms_tv_count;
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