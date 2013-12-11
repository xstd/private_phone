package com.xstd.pirvatephone.receiver;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.privatephone.tools.Tools;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

public class ObservePhone extends ContentObserver {
	private static final String TAG = "ObservePhone";
	private Context mContext;
	private SharedPreferences sp;

	public ObservePhone(Handler handler, Context context) {
		super(handler);
		this.mContext = context;
	}


	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Log.e(TAG, "通话数据库变化了");

		sp = mContext.getSharedPreferences("IntereptNumberFlag", Context.MODE_PRIVATE);
		int flag = sp.getInt("flag", 0);
		switch (flag) {
		case 0:

			break;
		case 1:
			removeReceive();
			break;
		case 2:
			removeReceive();
			break;

		default:
			break;
		}

	}

	private void removeReceive() {
		Log.e(TAG, "removeReceive()被调用");
		Cursor phoneDetailCursor = null;
		try {
			// 第2步:查询系统通话记录(该号码的最近一次记录)
			ContentResolver resolver = mContext.getContentResolver();
			phoneDetailCursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
					null, null, "date desc limit 1");

			if (phoneDetailCursor != null && phoneDetailCursor.getCount() > 0) {
				Tools.logSh("phoneDetailCursor.count()==="
						+ phoneDetailCursor.getCount());
				while (phoneDetailCursor.moveToNext()) {
					// 得到手机号码
					String number = phoneDetailCursor
							.getString(phoneDetailCursor
									.getColumnIndex("number"));
					// 得到开始时间
					Long date = phoneDetailCursor.getLong(phoneDetailCursor
							.getColumnIndex("date"));
					// 通话持续时间

					Long duration = phoneDetailCursor.getLong(phoneDetailCursor
							.getColumnIndex("duration"));
					// 通话类型
					int type = phoneDetailCursor.getInt(phoneDetailCursor
							.getColumnIndex("type"));
					// 通化人姓名

					String name = ContactUtils.queryContactName(mContext,
							number);

					Tools.logSh(number + "::" + date + "::" + duration + "::"
							+ type + "::" + name);

					// 第三步：将此通话记录插入到我们的数据库中，先判断记录中是否有这个号码的记录
					PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
							.getPhoneRecordDao(mContext);
					SQLiteDatabase phoneRecordDatabase = phoneRecordDao
							.getDatabase();
					Cursor phoneRecordCursor = phoneRecordDatabase.query(
							PhoneRecordDao.TABLENAME, null,
							PhoneRecordDao.Properties.Phone_number.columnName
									+ "=?", new String[] { number }, null,
							null, null);
					if (phoneRecordCursor != null
							&& phoneRecordCursor.getCount() > 0) {// 通话记录中已经有此号码的记录，跟新

						while (phoneRecordCursor.moveToNext()) {
							// 跟新Phonerecord
							PhoneRecord phoneRecord = new PhoneRecord();
							phoneRecord
									.setId(phoneRecordCursor.getLong(phoneRecordCursor
											.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
							phoneRecord
									.setPhone_number(phoneRecordCursor.getString(phoneRecordCursor
											.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName)));
							phoneRecord
									.setName(phoneRecordCursor.getString(phoneRecordCursor
											.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
							phoneRecord.setDate(date);
							phoneRecord.setType(type);
							phoneRecord
									.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
											.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);

							phoneRecordDao.update(phoneRecord);

							// 将此通话记录插入到我们的数据库中
							PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
									.getPhoneDetailDao(mContext);
							PhoneDetail phoneDetail = new PhoneDetail();
							phoneDetail.setPhone_number(number);
							phoneDetail.setDate(date);
							phoneDetail.setDuration(duration);
							phoneDetail.setType(type);
							phoneDetail.setName(name);
							phoneDetailDao.insert(phoneDetail);

							// 查询该联系人名称
							Tools.logSh("向我们数据库插入了一条数据");
							break;
						}

					} else {
						// 通话记录中还没有此号码的记录，直接插入一条新纪录
						PhoneRecord phoneRecord = new PhoneRecord();
						phoneRecord.setPhone_number(number);
						phoneRecord.setName(name);
						phoneRecord.setDate(System.currentTimeMillis());
						phoneRecord.setType(1);
						phoneRecord.setContact_times(1);
						phoneRecordDao.insert(phoneRecord);

						// 将此通话记录插入到我们的数据库中
						PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
								.getPhoneDetailDao(mContext);
						PhoneDetail phoneDetail = new PhoneDetail();
						phoneDetail.setPhone_number(number);
						phoneDetail.setDate(date);
						phoneDetail.setDuration(duration);
						phoneDetail.setType(1);
						phoneDetail.setName(name);
						phoneDetailDao.insert(phoneDetail);
					}
					phoneRecordCursor.close();
					// 清除该通话记录

					mContext.getContentResolver().delete(
							Uri.parse("content://call_log/calls"), "date=?",
							new String[] { date.toString() });

				}
				phoneDetailCursor.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (phoneDetailCursor != null) {
				phoneDetailCursor.close();
			}
		}
		Editor editor = sp.edit();
		editor.putInt("flag", 0);
		editor.commit();
	}

	private void removeDail() {
		Tools.logSh("removeDail()被调用");
		// 第2步:查询系统通话记录(该号码的最近一次记录)
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(
				Uri.parse("content://call_log/calls"), null, null, null,
				"date desc limit 1");
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String address = cursor.getString(cursor
						.getColumnIndex("number"));

				int type = cursor.getInt(cursor.getColumnIndex("type"));
				Long date = cursor.getLong(cursor.getColumnIndex("date"));
				Long duration = cursor.getLong(cursor
						.getColumnIndex("duration"));

				Tools.logSh("address=" + address + "::" + "type=" + type
						+ ":::" + "date=" + date + ":::" + "duration="
						+ duration);

				PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
						.getPhoneRecordDao(mContext);
				SQLiteDatabase phoneRecordDatabase = phoneRecordDao
						.getDatabase();
				Cursor phoneRecordCursor = phoneRecordDatabase.query(
						PhoneRecordDao.TABLENAME, null,
						PhoneRecordDao.Properties.Phone_number.columnName
								+ "=?", new String[] { address }, null, null,
						null);
				// 3.如果我们数据库中已有该号码的通话记录phoneRecord
				if (phoneRecordCursor != null
						&& phoneRecordCursor.getCount() > 0) {
					while (phoneRecordCursor.moveToNext()) {
						Tools.logSh("以后该号码的通话记录");
						PhoneRecord phoneRecord = new PhoneRecord();
						phoneRecord
								.setId(phoneRecordCursor.getLong(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Id.columnName)));
						phoneRecord.setPhone_number(address);
						phoneRecord
								.setName(phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
						phoneRecord.setDate(date);
						phoneRecord.setType(2);
						phoneRecord
								.setContact_times(phoneRecordCursor.getInt(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Contact_times.columnName)) + 1);

						phoneRecordDao.update(phoneRecord);

						PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
								.getPhoneDetailDao(mContext);
						PhoneDetail phoneDetail = new PhoneDetail();
						phoneDetail.setPhone_number(address);
						phoneDetail
								.setName(phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Name.columnName)));
						phoneDetail.setDate(date);
						phoneDetail.setDuration(duration);
						phoneDetail.setType(2);

						phoneDetailDao.insert(phoneDetail);
						break;
					}
				} else {// 如果还没有该号码的通话记录
					Tools.logSh("暂无该号码的通话记录");
					PhoneRecord phoneRecord = new PhoneRecord();
					phoneRecord.setPhone_number(address);

					phoneRecord.setDate(System.currentTimeMillis());
					phoneRecord.setType(2);
					phoneRecord.setContact_times(1);

					PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
							.getPhoneDetailDao(mContext);
					PhoneDetail phoneDetail = new PhoneDetail();
					phoneDetail.setPhone_number(address);
					phoneDetail.setDate(date);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(2);

					ContactInfoDao contactInfoDao2 = ContactInfoDaoUtils
							.getContactInfoDao(mContext);
					SQLiteDatabase database = contactInfoDao2.getDatabase();
					Cursor query = database.query(ContactInfoDao.TABLENAME,
							null,
							ContactInfoDao.Properties.Phone_number.columnName
									+ "=?", new String[] { address }, null,
							null, null);
					if (query != null && query.getCount() > 0) {
						while (query.moveToNext()) {
							String name = query
									.getString(query
											.getColumnIndex(ContactInfoDao.Properties.Display_name.columnName));
							phoneDetail.setName(name);
							phoneRecord.setName(name);
						}
						phoneRecordDao.insert(phoneRecord);
						phoneDetailDao.insert(phoneDetail);
					}
				}
				phoneRecordCursor.close();
				mContext.getContentResolver().delete(
						Uri.parse("content://call_log/calls"), "date=?",
						new String[] { date.toString() });

				cursor.close();
				break;
			}
		}
		Editor editor = sp.edit();
		editor.putInt("flag", 0);
		editor.commit();
	}
}
