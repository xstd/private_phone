package com.xstd.pirvatephone.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class WriteSmsDetailUtils {
	
	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");
	
	/**
	 * token 查询结果的唯一标示 ID cookie 传递对象 uri 查询的地址 projection 查询的字段 selection
	 * 查询的条件 where id = ? selectionArgs查询条件参数 ? orderBy 排序
	 */
	public static void writeSmsDetail(Context mContext,String[] mSelectPhones){
		
		ContentResolver resolver = mContext.getContentResolver();
		
		SmsDetailDao smsdetailDao = SmsDetailDaoUtils
				.getSmsDetailDao(mContext);

		for (int i = 0; i < mSelectPhones.length; i++) {
			String phone = mSelectPhones[i];

			Cursor detailCursor = resolver.query(Uri.parse("content://sms/"),
					null, "address=?", new String[] { phone }, null);
			if (detailCursor != null) {
				while (detailCursor.moveToNext()) {
					SmsDetail mSmsDetail = new SmsDetail();
					// thread_id
					Integer type = detailCursor.getInt(detailCursor
							.getColumnIndex("type"));
					// phone_number
					String phone_number = detailCursor.getString(detailCursor
							.getColumnIndex("address"));
					// lasted date
					Long date = detailCursor.getLong(detailCursor
							.getColumnIndex("date"));

					String body = detailCursor.getString(detailCursor
							.getColumnIndex("body"));

					mSmsDetail.setThread_id(type);
					mSmsDetail.setPhone_number(phone_number);
					mSmsDetail.setDate(date);
					mSmsDetail.setData(body);

					Tools.logSh("短信详细" + "thread_id" + type + "::"
							+ "phone_number" + phone_number + "::" + "date"
							+ date + "::" + "body" + body);
					smsdetailDao.insert(mSmsDetail);
					mSmsDetail = null;
					Tools.logSh("向smsDetail插入了一条数据");
				}
				detailCursor.close();
			}
		}


	}
}
