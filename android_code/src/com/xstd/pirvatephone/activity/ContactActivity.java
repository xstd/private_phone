package com.xstd.pirvatephone.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetail;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDao;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecord;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsDetail;
import com.xstd.pirvatephone.dao.sms.SmsDetailDao;
import com.xstd.pirvatephone.dao.sms.SmsDetailDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecord;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.adapter.AddContactAdapter;
import com.xstd.privatephone.tools.Tools;

public class ContactActivity extends BaseActivity {

	private Button bt_back;
	private Button bt_sure;
	private Button bt_cancle;
	private ListView lv_contact;
	private TextView tv_empty;

	private ContactInfo contactInfo;
	private GetContactTast task;
	private SmsDetail detail;
	private PhoneDetail phoneDetail;

	private Uri smsUri = Uri.parse("content://sms/");
	private static final int UPDATE_UI = 0;
	private static final int UPDATE = 1;
	private static final int FINISH_GET_CONTACT = 2;

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;
	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;
	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();
	/** 联系人号码 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 选取转换为隐私联系人的号码 **/
	private ArrayList<String> selectContactsNumber = new ArrayList<String>();

	private ArrayList<Integer> thread_ids = new ArrayList<Integer>();

	private HashMap<Integer, String> map;

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	ListView mListView = null;
	private AddContactAdapter mAdapter;

	// 查询的字段
	private String[] PROJECTION = new String[] { "sms.thread_id AS _id",
			"sms.body AS snippet", "groups.msg_count AS msg_count",
			" sms.address AS address", " sms.date AS date" };

	// PROJECTION[0,1,2,3,4]
	private static final int ID_COLUMN_INDEX = 0;
	private static final int SNIPPET_COLUMN_INDEX = 1;
	private static final int MSG_COUNT_COLUMN_INDEX = 2;
	private static final int ADDRESS_COLUMN_INDEX = 3;
	private static final int DATE_COLUMN_INDEX = 4;

	// 联系人字段
	private String[] CONTACT_PROJECTION = new String[] { PhoneLookup._ID,
			PhoneLookup.DISPLAY_NAME };
	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;
	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");

	static final String[] CALL_LOG_PROJECTION = new String[] {
			CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NUMBER,
			CallLog.Calls.TYPE, };

	private PhoneRecord phoneRecord;
	private ProgressBar pb_empty;
	private ImageView iv_empty_bg;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE:
				Tools.logSh("接受到消息");
				lv_contact.setEmptyView(iv_empty_bg);
				mAdapter = new AddContactAdapter(getApplicationContext(),
						mContactsName, mContactsNumber);
				lv_contact.setAdapter(mAdapter);
				lv_contact.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 保存已选择联系人。
						Tools.logSh("条目被点击了");
						CheckBox btn_check = (CheckBox) view
								.findViewById(R.id.btn_check);

						// 记录选项
						if (!btn_check.isChecked()) {
							selectContactsNumber.add(mContactsNumber
									.get(position));
						} else {
							selectContactsNumber.remove(mContactsNumber
									.get(position));
						}

						btn_check.setChecked(!btn_check.isChecked());
						Tools.logSh(selectContactsNumber.size() + "");
					}
				});
				break;

			case FINISH_GET_CONTACT:
				Tools.logSh("获取数据库联系人完成");
				pb_empty.setVisibility(View.GONE);
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		initView();

		task = new GetContactTast(getApplicationContext());
		task.execute();
	}

	/**
	 * 获取联系人
	 */
	private void getContact() {

		// 未优化方式
		/** 得到手机SIM卡联系人人信息 **/
		getSIMContacts();

		/** 得到手机通讯录联系人信息 **/
		getPhoneContacts();

		Message msg = new Message();
		msg.what = FINISH_GET_CONTACT;
		handler.sendMessage(msg);

	}

	private void initView() {
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);

		lv_contact = (ListView) findViewById(R.id.lv_contact);
		pb_empty = (ProgressBar) findViewById(R.id.pb_empty);
		iv_empty_bg = (ImageView) findViewById(R.id.iv_empty_bg);

		bt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 选择的隐私联系人信息导入我们的数据库。
				// 查询联系人信息添加到我们的数据库。
				removeContact();

			}

		});
	}

	/**
	 * 指定联系人的移动包含联系人、通话记录、短信
	 */
	private void removeContact() {

		Tools.logSh("removeContact");
		Tools.logSh(selectContactsNumber.size() + "");

		// 数组转换
		String[] selectPhones = new String[selectContactsNumber.size()];
		Object[] obj = selectContactsNumber.toArray();
		for (int i = 0; i < obj.length; i++) {
			selectPhones[i] = (String) obj[i];
			Tools.logSh(selectPhones[i]);
		}

		// 记录联系人号码的集合
		ArrayList<String> array = new ArrayList<String>();

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());

		ContentResolver resolver = getContentResolver();

		for (int i = 0; i < selectPhones.length; i++) {
			String num = selectPhones[i];
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

		PhoneDetailDao phoneDetailDao = PhoneDetailDaoUtils
				.getPhoneDetailDao(getApplicationContext());

		Tools.logSh("11111111111111111111111111111111111111");

		for (int i = 0; i < selectPhones.length; i++) {
			String phone = selectPhones[i];

			Tools.logSh("phone=====" + phone);
			// 获取详细通话记录
			Cursor phoneDetailCursor = resolver.query(
					CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER
							+ "=?", new String[] { phone }, null);
			if (phoneDetailCursor != null) {
				while (phoneDetailCursor.moveToNext()) {
					phoneDetail = new PhoneDetail();
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
					String name = phoneDetailCursor.getString(phoneDetailCursor
							.getColumnIndex("name"));

					Tools.logSh(number + "::" + start_time + "::" + duration
							+ "::" + type + "::" + name);

					if (name == null) {
						name = number;
					}

					phoneDetail.setPhone_number(number);
					phoneDetail.setDate(start_time);
					phoneDetail.setDuration(duration);
					phoneDetail.setType(type);
					phoneDetail.setName(name);

					// 添加到我们数据库
					phoneDetailDao.insert(phoneDetail);
					phoneDetail = null;
					Tools.logSh("向phoneDetail插入了一条数据");

				}
				phoneDetailCursor.close();
			}
		}

		Tools.logSh("222222222222222222222222222222222222222");
		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(getApplicationContext());

		// 获取简单通话记录
		/*
		 * Cursor phoneRecordCursor = resolver.query(CallLog.Calls.CONTENT_URI,
		 * null, CallLog.Calls.NUMBER + "=? ", selectPhones, null);
		 */

		for (int i = 0; i < selectPhones.length; i++) {
			String phone = selectPhones[i];

			Tools.logSh("phone=====" + phone);
			Cursor phoneRecordCursor = resolver.query(
					CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER
							+ "=?", new String[] { phone }, null);
			Tools.logSh("phoneRecordCursor=====" + phoneRecordCursor.getCount());
			if (phoneRecordCursor != null) {
				while (phoneRecordCursor.moveToNext()) {
					phoneRecord = new PhoneRecord();
					// 得到手机号码
					String number = phoneRecordCursor
							.getString(phoneRecordCursor
									.getColumnIndex(CallLog.Calls.NUMBER));
					// 得到时间
					Long date = phoneRecordCursor.getLong(phoneRecordCursor
							.getColumnIndex(CallLog.Calls.DATE));
					// 通话类型
					int type = phoneRecordCursor.getInt(phoneRecordCursor
							.getColumnIndex(CallLog.Calls.TYPE));
					// 通化人姓名
					String name = phoneRecordCursor.getString(phoneRecordCursor
							.getColumnIndex("name"));

					if (name == null) {
						name = number;
					}

					Tools.logSh(number + "::" + date + "::" + type + "::"
							+ name);

					phoneRecord.setName(name);
					phoneRecord.setDate(date);

					Cursor query = resolver.query(CallLog.Calls.CONTENT_URI,
							null, CallLog.Calls.NUMBER + "=?",
							new String[] { phone }, null);
					int count = query.getCount();

					Tools.logSh(number + "::" + date + "::" + type + "::"
							+ name + "count==" + count);

					phoneRecord.setContact_times(count);
					phoneRecord.setPhone_number(number);

					// 添加到我们数据库
					phoneRecordDao.insert(phoneRecord);
					phoneRecord = null;
					Tools.logSh("向phoneRecord插入了一条数据");
					// 取第一行数据
					phoneRecordCursor.close();
					break;
				}
			}
		}

		/**
		 * token 查询结果的唯一标示 ID cookie 传递对象 uri 查询的地址 projection 查询的字段 selection
		 * 查询的条件 where id = ? selectionArgs查询条件参数 ? orderBy 排序
		 */
		Tools.logSh("333333333333333333333333333333333333333333");
		// 获取短信详细纪录
		SmsDetailDao smsdetailDao = SmsDetailDaoUtils
				.getSmsDetailDao(getApplicationContext());

		for (int i = 0; i < selectPhones.length; i++) {
			String phone = selectPhones[i];

			Cursor detailCursor = resolver.query(Uri.parse("content://sms/"),
					null, "address=?", new String[] { phone }, null);
			if (detailCursor != null) {
				while (detailCursor.moveToNext()) {
					detail = new SmsDetail();
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

					detail.setThread_id(type);
					detail.setPhone_number(phone_number);
					detail.setDate(date);
					detail.setData(body);

					Tools.logSh("短信详细" + "thread_id" + type + "::"
							+ "phone_number" + phone_number + "::" + "date"
							+ date + "::" + "body" + body);
					smsdetailDao.insert(detail);
					detail = null;
					Tools.logSh("向smsDetail插入了一条数据");
				}
				detailCursor.close();
			}
		}

		// 获取每个对话的最后一次信息，以及时间，次数，内容
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(getApplicationContext());

		Tools.logSh("--------" + 1);

		for (int i = 0; i < selectPhones.length; i++) {
			String phone = selectPhones[i];
			Cursor smsCursor = resolver.query(CONVERSATIONS_URI, PROJECTION,
					"address=?", new String[] { phone }, null);

			Tools.logSh("" + smsCursor.getCount());
			if (smsCursor != null) {
				while (smsCursor.moveToNext()) {
					Tools.logSh("--------" + 3);

					// String idStr = smsCursor.getString(ID_COLUMN_INDEX);
					String body = smsCursor.getString(SNIPPET_COLUMN_INDEX);
					int count = smsCursor.getInt(MSG_COUNT_COLUMN_INDEX);
					String phone_number = smsCursor
							.getString(ADDRESS_COLUMN_INDEX);
					long date = smsCursor.getLong(DATE_COLUMN_INDEX);

					SmsRecord smsRecord = new SmsRecord();

					smsRecord.setPhone_number(phone_number);
					smsRecord.setCount(count);
					smsRecord.setLasted_contact(date);
					smsRecord.setLasted_data(body);

					// 添加到我们数据库
					smsRecordDao.insert(smsRecord);
					smsRecord = null;
					Tools.logSh("向smsRecord插入了一条数据");

				}
				smsCursor.close();
			}
		}

		// 显示选择对话框
		showDeleteDialog(selectPhones);

	}

	public void showDeleteDialog(final String[] str) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "移动联系人同时删除手机数据库", "仅移动联系人" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						TextView textView = (TextView) findViewById(R.id.text);
						switch (which) {
						case 0:
							// 删除系统库中的联系人。
							delContact(str);
							deletePhoneRecord(str);
							deleteSmsRecord(str);
							break;
						case 1:
							break;
						}
					}
				});
		builder.create().show();

	}

	private void delContact(String[] phoneNumbers) {

		for (int i = 0; i < phoneNumbers.length; i++) {
			String number = phoneNumbers[i];
			Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
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
						getContentResolver().applyBatch(
								ContactsContract.AUTHORITY, ops);
					} catch (Exception e) {
					}
				} while (cursor.moveToNext());

				cursor.close();
			}
		}

	}

	// 删除通话记录
	public void deletePhoneRecord(String[] phoneNumbers) {

		for (int i = 0; i < phoneNumbers.length; i++) {
			String number = phoneNumbers[i];

			int deletePhone = getContentResolver().delete(
					CallLog.Calls.CONTENT_URI, "number=?",
					new String[] { number });
			if (deletePhone > 0) {
				Tools.logSh("成功删除了系统通话记录数据库的一条数据");
			} else {
				Tools.logSh("删除系统通话记录数据库的一条数据失败");
			}
		}
	}

	// 删除短信记录
	public void deleteSmsRecord(String[] phoneNumbers) {

		for (int i = 0; i < phoneNumbers.length; i++) {
			String number = phoneNumbers[i];
			int deleteSms = getContentResolver().delete(
					Uri.parse("content://sms/"), "address=? or address = ?",
					new String[] { number, "+86" + number });

			if (deleteSms > 0) {
				Tools.logSh("成功删除了系统sms数据库的一条数据");
			} else {
				Tools.logSh("删除系统sms数据库的一条数据失败");
			}
			int deleteSms2 = getContentResolver().delete(
					Uri.parse("content://sms/"), "address in (?, ?)",
					new String[] { number, "+86" + number });
			if (deleteSms2 > 0) {
				Tools.logSh("成功删除了系统deleteSms2数据库的一条数据");
			} else {
				Tools.logSh("删除系统deleteSms2数据库的一条数据失败");
			}
		}
	}

	public void update(String[] str) {

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}

	public class GetContactTast extends AsyncTask<Void, Integer, Integer> {

		private Context context;

		public GetContactTast(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(context, "开始执行", Toast.LENGTH_SHORT).show();
			pb_empty.setVisibility(View.VISIBLE);
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			getContact();

			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(context, "执行完毕", Toast.LENGTH_SHORT).show();
			Message msg = new Message();
			msg.what = UPDATE;
			handler.sendMessage(msg);
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// Sim卡中没有联系人头像

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
			}

			phoneCursor.close();
		}
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;

				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.ic_launcher);
				}

				mContactsName.add(contactName);
				mContactsNumber.add(phoneNumber);
				mContactsPhonto.add(contactPhoto);
			}

			phoneCursor.close();
		}

	}
}
