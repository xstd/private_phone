package com.xstd.privatephone.adapter;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.plugin.common.utils.image.ImageUtils;
import com.xstd.pirvatephone.R;
import com.xstd.privatephone.bean.MyContactInfo;

public class AddFromContactAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MyContactInfo> mContactsInfos = new ArrayList<MyContactInfo>();

	/** 选取转换为隐私联系人的号码 **/

	public AddFromContactAdapter(Context context,
			ArrayList<MyContactInfo> contactInfos) {
		mContext = context;
		mContactsInfos.clear();
		mContactsInfos = contactInfos;
	}

	public void updateUI() {
		notifyDataSetChanged();
	}

	public int getCount() {
		// 设置绘制数量
		return mContactsInfos.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return mContactsInfos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = View.inflate(mContext,
					R.layout.private_add_contact_item, null);

			hold.name = (TextView) convertView.findViewById(R.id.tv_name);
			hold.phone = (TextView) convertView.findViewById(R.id.tv_phone_num);
			hold.check = (CheckBox) convertView.findViewById(R.id.btn_check);
			hold.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);

			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		Log.e("position====", position+"");
		MyContactInfo mContactInfo = new MyContactInfo();
		mContactInfo = mContactsInfos.get(position);
		// 绘制联系人名称
		hold.name.setText(mContactInfo.getName());
		// 绘制联系人号码
		hold.phone.setText(mContactInfo.getAddress());
		hold.check.setChecked(mContactInfo.isChecked);
		// 得到联系人头像Bitamp

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		Log.e("PhotoId  else ==", mContactInfo.getPhotoId() + "  contactId=="
				+ mContactInfo.getContactId());
		if (mContactInfo.getPhotoId() > 0) {
			AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(mContext,
					mContactInfo.getContactId(), hold.iv_photo);
			asyncLoader.execute();

		} else {
			hold.iv_photo
					.setImageResource(R.drawable.private_comm_contact_icon_default);
		}

		return convertView;
	}

	static class ViewHold {
		TextView name;
		TextView phone;
		ImageView iv_photo;
		CheckBox check;
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

		Log.e("uri===", uri.toString() + "");

		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(mContext.getContentResolver(), uri);

		contactPhoto = BitmapFactory.decodeStream(input);

		contactPhoto = ImageUtils.createRoundedCornerBitmap(contactPhoto, 48,
				48, 0.6f, true, true, true, true);
		return contactPhoto;
	}

}