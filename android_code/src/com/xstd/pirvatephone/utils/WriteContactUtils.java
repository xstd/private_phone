package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteContactUtils {
	private Context mContext;
	private String[] mPhones;
	private ArrayList<String> mRepeatePhones = new ArrayList<String>();
	private ArrayList<String> mNoRepeatPhones = new ArrayList<String>();
	private ContactInfo contactInfo;

	public WriteContactUtils(Context context) {
		this.mContext = context;
	}

	
	private String[] parseArray(ArrayList<String> selectContactsNumbers) {
		Tools.logSh("parseArray");
		mPhones = null;

		if (selectContactsNumbers.size() > 0) {
			mPhones = new String[selectContactsNumbers.size()];
			for (int i = 0; i <selectContactsNumbers.size(); i++) {
				mPhones[i] = selectContactsNumbers.get(i);
				Tools.logSh("selectPhones[i]="+mPhones[i]);
			}
		}
		
		return  mPhones;
	}
	/**
	 * 获取未重复的号码
	 */
	public void removeRepeat(String[] mSelectPhones){
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			String num = mSelectPhones[i];
			Cursor cursor = contactDatabase.query(
					ContactInfoDao.TABLENAME, null,
					ContactInfoDao.Properties.Phone_number.columnName + "=?",
					new String[] { num }, null, null, null);
			//该号码已经存在于隐私联系人中
			if(cursor!=null && cursor.getCount()>0){
				mRepeatePhones.add(num);
			}
		}
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			if(mRepeatePhones.contains(mSelectPhones[i])){
			
			}else{
				mNoRepeatPhones.add(mSelectPhones[i]);
			}
		}
		
		String[] noRepeatPhones = parseArray(mNoRepeatPhones);
		if(noRepeatPhones!=null){
			writeContact(noRepeatPhones);
		}
		
	}

	/**
	 * 若仅仅移动一个，且存在于私密联系人
	 * 
	 * @return
	 */
	public boolean isPrivateContact(String[] mSelectPhones) {

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();
		
		String number = mSelectPhones[0];
		Cursor cursor = contactDatabase.query(
				ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Phone_number.columnName + "=?",
				new String[] { number }, null, null, null);

		if (cursor != null && cursor.getCount()>0) {
			return true;
		}else{
			return false;
		}
		
	}

	public void writeContact(String[] mSelectPhones) {

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

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
					// 判断该联系人（姓名）是否已存在
					Cursor query = contactDatabase.query(
							ContactInfoDao.TABLENAME, null,
							ContactInfoDao.Properties.Display_name.columnName
									+ "=?", new String[] { contactName }, null,
							null, null);
					if (query != null && query.getCount() > 0) {
						Tools.logSh("该联系人已经存在");
						break ;
					} else {
						String number = phoneCursor.getString(2);

						contactInfo.setPhone_number(number);
						contactInfo.setDisplay_name(contactName);

						// 添加到我们数据库
						contactInfoDao.insert(contactInfo);
						Tools.logSh("phoneCursor插入了一条数据");
					}
				}
				phoneCursor.close();
			}
		}
	}
}
