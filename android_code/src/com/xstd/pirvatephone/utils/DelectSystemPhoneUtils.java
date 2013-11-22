package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.provider.CallLog;

import com.xstd.privatephone.tools.Tools;

public class DelectSystemPhoneUtils {
	
	public static void deletePhone(Context mContext,String[] mPhoneNumbers){
		if(mPhoneNumbers==null || mPhoneNumbers.length==0){
			return;
		}
		for (int i = 0; i < mPhoneNumbers.length; i++) {
			String number = mPhoneNumbers[i];
			Tools.logSh(number);
			int deletePhone = mContext.getContentResolver().delete(
					CallLog.Calls.CONTENT_URI, "number=?",
					new String[] { number });
			if (deletePhone > 0) {
				Tools.logSh("成功删除了系统通话记录数据库的一条数据");
			} else {
				Tools.logSh("删除系统通话记录数据库的一条数据失败");
			}
		}
	}
}
