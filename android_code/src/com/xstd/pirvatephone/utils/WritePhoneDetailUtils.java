package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WritePhoneDetailUtils {
	
	public static void writePhoneDetail(Context mContext,String[] mSelectPhones){
		
		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
				.getPhoneDetailDao(mContext);
		ContentResolver resolver = mContext.getContentResolver();
		
		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];

			Tools.logSh("phone=====" + phone);
			// 获取详细通话记录
			Cursor phoneDetailCursor = resolver.query(
					CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER
							+ "=?", new String[] { phone }, null);
			if (phoneDetailCursor != null) {
				while (phoneDetailCursor.moveToNext()) {
					PhoneDetail mPhoneDetail = new PhoneDetail();
					// 得到手机号码
					String number = phoneDetailCursor
							.getString(phoneDetailCursor
									.getColumnIndex("number"));
					// 得到联系人名称
					Long start_time = phoneDetailCursor
							.getLong(phoneDetailCursor.getColumnIndex("date"));
					// 通话持续时间

					Long duration = phoneDetailCursor.getLong(phoneDetailCursor
							.getColumnIndex("duration"));
					// 通话类型
					int type = phoneDetailCursor.getInt(phoneDetailCursor
							.getColumnIndex("type"));
					// 通化人姓名
					/*String name = phoneDetailCursor.getString(phoneDetailCursor
							.getColumnIndex("name"));*/
					String name = ContactUtils.queryContactName(mContext, phone);

					Tools.logSh(number + "::" + start_time + "::" + duration
							+ "::" + type + "::" + name);

					if (name == null) {
						name = number;
					}

					mPhoneDetail.setPhone_number(number);
					mPhoneDetail.setDate(start_time);
					mPhoneDetail.setDuration(duration);
					mPhoneDetail.setType(type);
					mPhoneDetail.setName(name);

					// 添加到我们数据库
					phoneDetailDao.insert(mPhoneDetail);
					mPhoneDetail = null;
					Tools.logSh("向phoneDetail插入了一条数据");

				}
				phoneDetailCursor.close();
			}
		}

	}
}
