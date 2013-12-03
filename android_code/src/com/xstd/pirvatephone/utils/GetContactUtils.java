package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.util.Log;

import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

public class GetContactUtils {
	private Context mContext;


    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;
    
    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
   
    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    

    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };

	/** 联系人信息 **/
	private ArrayList<MyContactInfo> mContactsInfos = new ArrayList<MyContactInfo>();

	public GetContactUtils(Context context) {
		this.mContext = context;
	}

	public ArrayList<MyContactInfo> getContacts(String search) {
		mContactsInfos.clear();

		ContentResolver resolver = mContext.getContentResolver();
		// Cursor cursor =
		// db.rawQuery("SELECT name,telephone FROM jacnamelist where (finditems like ? and department>-1)",
		// new String[]{"%"+username+"%"});
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, Phone.DISPLAY_NAME + " like ? or "
						+ Phone.NUMBER + " like ?", new String[] {
						"%" + search + "%", "%" + search + "%" }, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				MyContactInfo contactInfo = new MyContactInfo(phoneNumber,
						contactName, false, photoid,contactid);
				mContactsInfos.add(contactInfo);
				// mContactsPhotos.add(contactPhoto);
			}

			phoneCursor.close();
		}

		return mContactsInfos;
	}

	public ArrayList<MyContactInfo> getContacts() {

		mContactsInfos.clear();
		// 未优化方式
		/** 得到手机SIM卡联系人人信息 **/
		getSIMContacts();

		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();
		Tools.logSh("查询到----------" + mContactsInfos.size() + "个联系人");
		return mContactsInfos;

	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {

		ContentResolver resolver = mContext.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
				    continue;
				// 得到联系人名称
				String contactName = phoneCursor
					.getString(PHONES_DISPLAY_NAME_INDEX);

				//Sim卡中没有联系人头像

				MyContactInfo contactInfo = new MyContactInfo(phoneNumber,
						contactName, false, 0L,null);
				mContactsInfos.add(contactInfo);
			}

			phoneCursor.close();
		}
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				//得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				//得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				Log.e("contactid+photoid", contactid+":"+photoid);
				
				MyContactInfo contactInfo = new MyContactInfo(phoneNumber,
						contactName, false, photoid,contactid);
				mContactsInfos.add(contactInfo);
			}

			phoneCursor.close();
		}
	}
}
