package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.xstd.privatephone.tools.Tools;

public class DelectSystemContactUtils {
	
	public static void deleteContacts(Context mContext,String[] mPhoneNumbers){
		
		if(mPhoneNumbers==null){
			Tools.logSh("kong--------------");
		}else{
			Tools.logSh("bukong--------------");
		}
		for (int i = 0; i < mPhoneNumbers.length; i++) {
			String number = mPhoneNumbers[i];
			Cursor cursor = mContext.getContentResolver().query(Data.CONTENT_URI,
					new String[] { Data.RAW_CONTACT_ID }, Phone.NUMBER + "=?",
					new String[] { number }, null);

			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			if (cursor.moveToFirst()) {

				do {

					long id = cursor.getLong(cursor
							.getColumnIndex(Data.RAW_CONTACT_ID));
					ops.add(ContentProviderOperation.newDelete(
							ContentUris.withAppendedId(RawContacts.CONTENT_URI,
									id)).build());
					if (id > 0) {
						Tools.logSh("成功删除了系统联系人数据库的一条数据");
					} else {
						Tools.logSh("删除系统联系人数据库的一条数据失败");
					}

					try {
						mContext.getContentResolver().applyBatch(
								ContactsContract.AUTHORITY, ops);
					} catch (Exception e) {
					}
				} while (cursor.moveToNext());

				cursor.close();
			}
		}
	}
}
