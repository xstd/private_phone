package com.xstd.pirvatephone.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity {

	private Button bt_back;
	private Button bt_sure;
	private Button bt_cancle;
	private ListView lv_contact;
	private TextView tv_empty;

	private ContactInfo contactInfo = new ContactInfo();
	private PhoneRecord phoneRecord = new PhoneRecord();
	private SmsRecord smsRecord = new SmsRecord();
	private SmsDetail smsDetail = new SmsDetail();
	
	private  Uri CONTENT_URI = Uri.parse("content://sms/");

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
	private ArrayList<String> selectContactsNumber = new ArrayList<String>();
	private ArrayList<Integer> thread_ids = new ArrayList<Integer>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

	ListView mListView = null;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				Tools.logSh("接受到消息");
				lv_contact.setEmptyView(tv_empty);
				lv_contact
						.setAdapter(new AddContactAdapter(
								getApplicationContext(), mContactsName,
								mContactsNumber));
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
			}

		};
	};
	private GetContactTast task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

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

	}

	private void initView() {
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);

		lv_contact = (ListView) findViewById(R.id.lv_contact);
		tv_empty = (TextView) findViewById(R.id.tv_empty);

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
		
		//数组转换
		String[] s = new String[selectContactsNumber.size()];
		Object[] obj = selectContactsNumber.toArray();
		for (int i = 0; i < obj.length; i++) {
			s[i] = (String) obj[i];
			Tools.logSh(s[i]);
		}
		
		//转换后号码记录
		ArrayList array = new ArrayList();
		

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());

		ContentResolver resolver = getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, new String[] {
				Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER },
				Phone.NUMBER + "=?", s, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到联系人名称
				String contactName = phoneCursor.getString(1);

				// 得到手机号码,可能含有特殊符号+，- “ ”
				String number = phoneCursor.getString(2);
				
				String num1  = number.replace(" ", "");
				String num2  = num1.replace("+", "");
				String num3  = num2.replace("-", "");
				array.add(num3);

				Tools.logSh(number + "-----"+contactName);
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

		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(getApplicationContext());
		
		String[] s2 = new String[selectContactsNumber.size()];
		Object[] obj2 = array.toArray();
		for (int i = 0; i < obj2.length; i++) {
			s2[i] = (String) obj2[i];
			Tools.logSh(s2[i]);
		}

		// 获取通话记录
		Cursor recordCursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
				CallLog.Calls.NUMBER+"=?", s2, null);

		if (recordCursor != null) {
			while (recordCursor.moveToNext()) {

				// 得到手机号码
				String number = recordCursor.getString(recordCursor.getColumnIndex("number"));
				// 得到联系人名称
				Long start_time = recordCursor.getLong(recordCursor.getColumnIndex("date"));
				// 通话持续时间
				
				Long duration = recordCursor.getLong(recordCursor.getColumnIndex("duration"));
				// 通话类型
				Integer type = recordCursor.getInt(recordCursor.getColumnIndex("type"));
				// 通化人姓名
				String name = recordCursor.getString(recordCursor.getColumnIndex("name"));

				Tools.logSh(number + start_time + duration + type + name);

				phoneRecord.setPhone_number(number);
				phoneRecord.setStart_time(start_time);
				phoneRecord.setDuration(duration);
				phoneRecord.setType(type);
				phoneRecord.setName(name);

				
				
				// 添加到我们数据库
				phoneRecordDao.insert(phoneRecord);
				Tools.logSh("向recordCursor插入了一条数据");

			}
			recordCursor.close();
		}
		
		
		
		// 获取短信纪录
		SmsDetailDao smsdetailDao = SmsDetailDaoUtils.getSmsDetailDao(getApplicationContext());
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		/**
	     * 查询短信的数据
	     * 1.先查询sms表出每条短信的发送时间和内容
	     * 2.在查询threads数据库获取其他信息
	     */
		Cursor detailCursor = resolver.query(Uri.parse("content://sms/"),  
                null, "address=?", s2, null);  
		if (detailCursor != null) {
			while (detailCursor.moveToNext()) {
				
				//thread_id
				Integer thread_id = detailCursor.getInt(detailCursor.getColumnIndex("thread_id"));
				//phone_number
				String phone_number = detailCursor.getString(detailCursor.getColumnIndex("address"));
				//lasted date
				Long date = detailCursor.getLong(detailCursor.getColumnIndex("date"));
				
				String body = detailCursor.getString(detailCursor.getColumnIndex("body"));
				
				
				map.put(thread_id, phone_number);
				smsDetail.setThread_id(thread_id);
				smsDetail.setPhone_number(phone_number);
				smsDetail.setDate(date);
				smsDetail.setData(body);
				
				thread_ids.add(thread_id);
				
				smsdetailDao.insert(smsDetail);
				Tools.logSh("向smsDetail插入了一条数据");
			}
			detailCursor.close();
		}
		
		
		String[] s3 = new String[thread_ids.size()];
		Object[] obj3 = thread_ids.toArray();
		for (int i = 0; i < obj3.length; i++) {
			s3[i] = ""+obj3[i];
			Tools.logSh("++++++"+s3[i]);
		}
		
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils.getSmsRecordDao(getApplicationContext());
		
		
		Tools.logSh("--------"+1);
		/*Cursor smsCursor = resolver.query(Uri.parse("content://sms/"),  
				new String[]{ "* from threads--" }, "_id=?", s3, null); */ 
		Cursor smsCursor = resolver.query(Uri.parse("content://sms/"),  
				new String[] { "* from threads--" }, null, null, null);  

		Tools.logSh(""+smsCursor.getCount());
		if (smsCursor != null) {
			while (smsCursor.moveToNext()) {
				Tools.logSh("--------"+3);
				//_id
				int _id = smsCursor.getInt(smsCursor.getColumnIndex("_id"));
				// 通信次数
				int count = smsCursor.getInt(smsCursor.getColumnIndex("message_count"));
				// 最近一次通信时间
				Long lasted_date = smsCursor.getLong(smsCursor.getColumnIndex("date"));
				// 最近一次短信内容
				String data = smsCursor.getString(smsCursor.getColumnIndex("snippet"));
				// 是否已读
				Integer type = smsCursor.getInt(smsCursor.getColumnIndex("read"));

				String number = map.get(_id);
				
				Tools.logSh(count +"::" +lasted_date +"::"+ data + "::"+type+"::"+number);
				
				smsRecord.setPhone_number(number);
				smsRecord.setCount(count);
				smsRecord.setLasted_contact(lasted_date);
				smsRecord.setLasted_data(data);

				// 添加到我们数据库
				smsRecordDao.insert(smsRecord);
				Tools.logSh("向smsRecord插入了一条数据");

			}
			smsCursor.close();
		}
		
		// 删除系统库中的联系人。
	//	delContact(s);
		
		// 跟新listview
	}

	private void delContact(String[] str) {
		Tools.logSh("删除了系统数据库的一条数据");
		Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID }, Phone.NUMBER + "=?", str,
				null);

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		if (cursor.moveToFirst()) {

			do {

				long Id = cursor.getLong(cursor
						.getColumnIndex(Data.RAW_CONTACT_ID));
				ops.add(ContentProviderOperation
						.newDelete(
								ContentUris.withAppendedId(
										RawContacts.CONTENT_URI, Id)).build());
				try {
					getContentResolver().applyBatch(ContactsContract.AUTHORITY,
							ops);
				} catch (Exception e) {
				}
			} while (cursor.moveToNext());

			cursor.close();
		}

		// 跟新界面UI
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			msg.what = 1;
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
