package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsUtils {

	public static void readContact(Context ctx, Uri uri) {
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
				int phonetype = phone
						.getInt(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				int restype = ContactsContract.CommonDataKinds.Phone
						.getTypeLabelResource(phonetype);
			}
		}
	}
}
