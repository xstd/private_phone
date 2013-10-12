package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteContactUtils {
	private Context mContext;
	private String[] mSelectPhones;
	private ContactInfo contactInfo;
	
	public WriteContactUtils(Context context, String[] selectPhones){
		this.mContext = context;
		this.mSelectPhones = selectPhones;
	}
	
	public void writeContact(){
		
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);

		ContentResolver resolver = mContext.getContentResolver();
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			String num = mSelectPhones[i];
			// 获取手机联系人
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
					new String[] { Phone.CONTACT_ID, Phone.DISPLAY_NAME,
							Phone.NUMBER }, Phone.NUMBER + "=?",
					new String[] { num }, null);

			if (phoneCursor != null) {
				while (phoneCursor.moveToNext()) {
					contactInfo = new ContactInfo();
					// 得到联系人名称
					String contactName = phoneCursor.getString(1);

					String number = phoneCursor.getString(2);

					// 得到联系人ID
					// Long contactid =
					// phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

					contactInfo.setPhone_number(number);
					contactInfo.setDisplay_name(contactName);

					// 添加到我们数据库
					contactInfoDao.insert(contactInfo);
					Tools.logSh("phoneCursor插入了一条数据");

				}
				phoneCursor.close();
			}
		}
	}
}
