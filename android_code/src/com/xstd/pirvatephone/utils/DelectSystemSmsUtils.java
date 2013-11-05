package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.net.Uri;

import com.xstd.privatephone.tools.Tools;

public class DelectSystemSmsUtils {
	
	public static void deleteSms(Context mContext,String[] mPhoneNumbers){
		
		for (int i = 0; i < mPhoneNumbers.length; i++) {
			String number = mPhoneNumbers[i];
			int deleteSms = mContext.getContentResolver().delete(
					Uri.parse("content://sms/"), "address=? or address = ?",
					new String[] { number, "+86" + number });

			if (deleteSms > 0) {
				Tools.logSh("成功删除了系统sms数据库的一条数据");
			} else {
				Tools.logSh("删除系统sms数据库的一条数据失败");
			}
			int deleteSms2 = mContext.getContentResolver().delete(
					Uri.parse("content://sms/"), "address in (?, ?)",
					new String[] { number, "+86" + number });
			if (deleteSms2 > 0) {
				Tools.logSh("成功删除了系统deleteSms2数据库的一条数据");
			} else {
				Tools.logSh("删除系统deleteSms2数据库的一条数据失败");
			}
		}
	}
}
