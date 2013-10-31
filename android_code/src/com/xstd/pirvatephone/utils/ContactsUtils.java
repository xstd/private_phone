package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.module.SimpleContact;

import java.util.List;

public class ContactsUtils {

	private static final String TAG = "ContactsUtils";

	/**
	 * 通过uri查询联系人的信息
	 * 
	 * @param ctx
	 * @param uri
	 * @return
	 */
	public static SimpleContact readContact(Context ctx, Uri uri) {
		SimpleContact contact = null;
		Cursor cursor = ctx.getContentResolver().query(uri, null, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String id = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = ctx.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
					new String[] { id }, null);
			if (phone != null && phone.moveToFirst()) {
				String phonenumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				contact = new SimpleContact(id, name, phonenumber);
			}
		}
		return contact;
	}

	/**
	 * 通过电话号码查询联系人的姓名
	 * 
	 * @param number
	 * @return 如果有则返回联系人姓名，没有则返回null
	 */
	public static String queryContactName(Context context, long number) {
		String name = null;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
				new String[] { String.valueOf(number) }, null);
		if (cursor != null && cursor.getCount() > 0) {
			name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		}
		cursor.close();
		return name;
	}

    public static List<SimulateComm> getSmsByPeople(Context ctx) {
        return null;
    }

    public static List<SimulateComm> getCallLogByPeople(Context ctx) {
        Cursor cursor = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,CallLog.Calls.DEFAULT_SORT_ORDER);
        return null;
    }
}
