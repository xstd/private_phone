package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.List;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.pirvatephone.utils.ArrayUtils;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.DelectOurContactUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneRecordsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsRecordsUtils;
import com.xstd.pirvatephone.utils.MakeCallUtils;
import com.xstd.pirvatephone.utils.RecordToSysUtils;
import com.xstd.pirvatephone.utils.RestoreSystemSmsUtils;
import com.xstd.privatephone.adapter.ContactAdapter;
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.adapter.EditPhoneAdapter;
import com.xstd.privatephone.adapter.EditSmsAdapter;
import com.xstd.privatephone.adapter.MyViewPagerAdapter;
import com.xstd.privatephone.adapter.PhoneRecordAdapter;
import com.xstd.privatephone.adapter.SmsRecordAdapter;
import com.xstd.privatephone.tools.Tools;
import com.xstd.privatephone.view.SureDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrivateCommActivity extends BaseActivity {

	private com.xstd.privatephone.view.MyViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private Button textView1, textView2, textView3;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2, view3;// 各个页卡

	private Button ib_back;
	private Button edit;

	private int smsPageNum = 0;
	private int dialPageNum = 1;
	private int contactPageNum = 2;
	private Button contact_add_contacts;

	private TextView contact_empty;
	private Cursor contactCursor;
	boolean showEdit = false;

	private ArrayList<String> selectContactsNumber = new ArrayList<String>();;
	private ListView contact_listview;

	private static final int REMOVE_FINISH = 0;
	private static final String TAG = "PrivateCommActivity";

	private ContactAdapter mContactAdapter;

	private String[] selectPhones;
	private Cursor smsRecordCursor;
	private Cursor phoneRecordCursor;
	private ListView dial_listview;
	private TextView dial_empty;
	private ListView sms_listview;
	private TextView sms_empty;

	private RelativeLayout edit_ll_body;
	private RelativeLayout edit_rl_select;
	private Button edit_checkbox;
	private ListView edit_listview;
	private LinearLayout edit_ll_sms_bt;
	private LinearLayout edit_ll_contact_bt;
	private LinearLayout edit_ll_phone_bt;
	private Button btn_recover_sms;
	private Button btn_remove_sms;
	private Button btn_delete_contact;
	private Button btn_delete_phone;
	private LinearLayout body_layout;

	private EditContactAdapter editContactAdapter;
	private EditPhoneAdapter editPhoneAdapter;
	private EditSmsAdapter editSmsAdapter;

	private final ArrayList<String> selectContacts = new ArrayList<String>();

	public Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case REMOVE_FINISH:
				showEdit = false;

				textView1.setClickable(true);
				textView2.setClickable(true);
				textView3.setClickable(true);
				// 跟新listview
				Tools.logSh("currIndex=====" + currIndex);

				if (currIndex == 2) {
					if (mContactAdapter != null) {
						contactCursor.requery();
						Tools.logSh("contactCursor.size()====="
								+ contactCursor.getCount());
						setContactAdapter(contact_listview);
					} else {
						contactCursor.requery();
						setContactAdapter(contact_listview);
					}
				}

				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_comm);

		initData();
		InitImageView();
		initView();

		InitViewPager();
	}

	private void initData() {
		getSmsRecord();
		getPhoneRecord();
		getContact();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void InitViewPager() {
		// 初始化头标
		textView1 = (Button) findViewById(R.id.text1);
		textView2 = (Button) findViewById(R.id.text2);
		textView3 = (Button) findViewById(R.id.text3);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));

		viewPager = (com.xstd.privatephone.view.MyViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.spac_sms, null);
		view2 = inflater.inflate(R.layout.spac_dial, null);
		view3 = inflater.inflate(R.layout.spac_contact, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		currIndex = 0;
		viewPager.setCurrentItem(currIndex);

		fillData();
	}

	/**
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据 3
	 */

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void initView() {
		// 1、title
		edit = (Button) findViewById(R.id.btn_edit);

		// content-all
		body_layout = (LinearLayout) findViewById(R.id.body_layout);
		edit_ll_body = (RelativeLayout) findViewById(R.id.edit_ll_body);

		// edit-checkall
		edit_rl_select = (RelativeLayout) findViewById(R.id.edit_rl_select);
		edit_checkbox = (Button) findViewById(R.id.edit_checkbox_all);
		edit_listview = (ListView) findViewById(R.id.edit_listview);

		// edit-buttom
		edit_ll_sms_bt = (LinearLayout) findViewById(R.id.edit_ll_sms_bt);
		edit_ll_contact_bt = (LinearLayout) findViewById(R.id.edit_ll_contact_bt);
		edit_ll_phone_bt = (LinearLayout) findViewById(R.id.edit_ll_phone_bt);

		// edit-bottom-button
		btn_recover_sms = (Button) findViewById(R.id.edit_btn_recover_sms);
		btn_remove_sms = (Button) findViewById(R.id.edit_btn_remove_sms);
		btn_delete_contact = (Button) findViewById(R.id.edit_btn_delete_contact);
		btn_delete_phone = (Button) findViewById(R.id.edit_btn_delete_phone);

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.logSh("currIndex==" + currIndex);
				if (showEdit) {
					showNormalUI();
					textView1.setClickable(true);
					textView2.setClickable(true);
					textView3.setClickable(true);
				} else {
					showEditUI();
				}
				showEdit = !showEdit;
			}
		});

		ib_back = (Button) findViewById(R.id.btn_back);
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		showNormalUI();

	}

	private void showEditBottom() {
		if (currIndex == smsPageNum) {
			edit_ll_sms_bt.setVisibility(View.VISIBLE);
			edit_ll_contact_bt.setVisibility(View.GONE);
			edit_ll_phone_bt.setVisibility(View.GONE);
		} else if (currIndex == dialPageNum) {
			edit_ll_sms_bt.setVisibility(View.GONE);
			edit_ll_phone_bt.setVisibility(View.VISIBLE);
			edit_ll_contact_bt.setVisibility(View.GONE);
		} else if (currIndex == contactPageNum) {
			edit_ll_sms_bt.setVisibility(View.GONE);
			edit_ll_phone_bt.setVisibility(View.GONE);
			edit_ll_contact_bt.setVisibility(View.VISIBLE);
		}

	}

	private void showEditUI() {
		textView1.setClickable(false);
		textView2.setClickable(false);
		textView3.setClickable(false);
		edit_ll_body.setVisibility(View.VISIBLE);
		Tools.logSh("进入编辑模式");
		if (currIndex == smsPageNum) {

			editSmsAdapter = new EditSmsAdapter(PrivateCommActivity.this,
					smsRecordCursor);

			edit_listview.setAdapter(editSmsAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.checkbox);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_hidden_number);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
						}
					}
				}
			});
			btn_remove_sms.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						showEditDeleteSmsDialog();
					}

				}
			});
			btn_recover_sms.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要恢复的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						showEditRecoverSmsDialog();
					}

				}
			});

		} else if (currIndex == dialPageNum) {

			editPhoneAdapter = new EditPhoneAdapter(PrivateCommActivity.this,
					phoneRecordCursor);

			edit_listview.setAdapter(editPhoneAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.checkbox);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_hidden_number);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
						}
					}
				}
			});
			btn_delete_phone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						showEditPhoneDialog();
					}
				}
			});

		} else if (currIndex == contactPageNum) {

			editContactAdapter = new EditContactAdapter(
					PrivateCommActivity.this, contactCursor);
			edit_listview.setAdapter(editContactAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.checkbox);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_phone_num);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
						}
					}
				}
			});
			btn_delete_contact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						showEditContactDialog();
					}
				}
			});
		}
		showEditBottom();
	}

	private void showNormalUI() {
		edit_ll_body.setVisibility(View.GONE);
		Tools.logSh("退出编辑模式");

	}

	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			Log.w(TAG, arg0 + "");
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			/*
			 * Toast.makeText(PrivateCommActivity.this, "您选择了" +
			 * viewPager.getCurrentItem() + "页卡", Toast.LENGTH_SHORT).show();
			 */

			showEdit = false;
			// 加载数据
			fillData();
			selectContacts.clear();
			Tools.logSh("当前页面" + currIndex);
		}

	}

	/**
	 * 根据当前页面加载不同的控件
	 */
	private void fillData() {

		Log.w(TAG, "fillData");

		if (currIndex == smsPageNum) {

			sms_listview = (ListView) view1.findViewById(R.id.sms_lv_cont);
			sms_empty = (TextView) view1.findViewById(R.id.sms_tv_empty);

			Tools.logSh("接收到增加联系人点击" + currIndex);
			updateContact(sms_listview, sms_empty);

			return;
		}
		if (currIndex == dialPageNum) {
			dial_listview = (ListView) findViewById(R.id.dial_lv_cont);
			dial_empty = (TextView) findViewById(R.id.dial_tv_empty);

			updateContact(dial_listview, dial_empty);

			return;
		}
		if (currIndex == contactPageNum) {
			// 正常模式下视图
			contact_listview = (ListView) findViewById(R.id.contact_listview);
			contact_empty = (TextView) findViewById(R.id.contact_tv_empty);
			contact_add_contacts = (Button) findViewById(R.id.contact_add_contacts);

			contact_add_contacts.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// show dialog
					Tools.logSh("接收到增加联系人点击" + currIndex);
					showAddContactDialog();
				}
			});

			// 从我们的数据库读取隐私联系人，展示到页面上

			updateContact(contact_listview, contact_empty);
		}
	}

	/**
	 * 删除联系人同时恢复记录到系统数据库
	 * 
	 * @param selectNumbers
	 */
	public void deleteContacts(String[] selectNumbers) {
		DelectOurContactUtils.deleteContacts(this, selectNumbers);

		RecordToSysUtils recordToSysUtils = new RecordToSysUtils(this);
		recordToSysUtils.restoreContact(selectNumbers);

		// 删除隐私联系人号码同时移除情景模式内该号码相关信息
		ContextModelUtils.deleteModelDetail(this, selectNumbers);
	}

	public void updatePhone(ListView mListView, TextView tv) {

		if (contactCursor.getCount() == 0) {
			mListView.setEmptyView(tv);
		} else {
			// 通知数据获取完成
			setContactAdapter(mListView);
		}

	}

	private Cursor getSmsRecord() {
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(getApplicationContext());
		SQLiteDatabase database = smsRecordDao.getDatabase();
		Tools.logSh("getSmsRecord()执行了");
		smsRecordCursor = database.query("SMS_RECORD", null, null, null, null,
				null, null);

		Tools.logSh("getSmsRecord()执行了+" + smsRecordCursor.getCount());
		return smsRecordCursor;
	}

	/**
	 * 获取我们数据库联系人
	 */
	private Cursor getContact() {

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());
		SQLiteDatabase database = contactInfoDao.getDatabase();

		contactCursor = database.query(ContactInfoDao.TABLENAME, null, null,
				null, null, null, null);
		return contactCursor;
	}

	/**
	 * 获取我们数据库联系人
	 */
	private Cursor getPhoneRecord() {

		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(getApplicationContext());
		SQLiteDatabase database = phoneRecordDao.getDatabase();

		phoneRecordCursor = database.query("PHONE_RECORD", null, null, null,
				null, null, null);

		return phoneRecordCursor;
	}

	public void updateContact(ListView mListView, TextView tv) {

		switch (currIndex) {
		case 0:
			if (smsRecordCursor.getCount() == 0) {
				mListView.setEmptyView(tv);
			} else {
				// 通知数据获取完成
				setSmsRecordAdapter(mListView);
			}
			break;

		case 1:

			if (phoneRecordCursor.getCount() == 0) {
				mListView.setEmptyView(tv);
			} else {
				// 通知数据获取完成
				setPhoneRecordAdapter(mListView);
			}
			break;

		case 2:
			if (contactCursor.getCount() == 0) {
				mListView.setEmptyView(tv);
			} else {
				// 通知数据获取完成
				setContactAdapter(mListView);
			}
			break;
		}

	}

	private void setPhoneRecordAdapter(ListView mListView) {

		if (phoneRecordCursor.getCount() == 0) {
			mListView.setEmptyView(dial_empty);
		} else {
			Tools.logSh("cursor的长度为：" + phoneRecordCursor.getCount());
			dial_empty.setVisibility(View.GONE);
			mListView.setAdapter(new PhoneRecordAdapter(
					getApplicationContext(), phoneRecordCursor));
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tools.logSh("进入电话详细界面");
				Intent intent = new Intent(PrivateCommActivity.this,
						PhoneDetailActivity.class);
				// 联系人带过去
				TextView sms_tv_name = (TextView) view
						.findViewById(R.id.dial_tv_contact);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.dial_tv_contact_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString().trim();
				intent.putExtra("Name", name);
				intent.putExtra("Number", number);
				startActivity(intent);
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView sms_tv_name = (TextView) view
						.findViewById(R.id.dial_tv_contact);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.dial_tv_contact_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString().trim();
				showPhoneDialog(name, number);
				return false;
			}
		});
	}

	private void setSmsRecordAdapter(ListView mListView) {
		if (smsRecordCursor.getCount() == 0) {
			Tools.logSh("没有数据");

			mListView.setEmptyView(sms_empty);

		} else {
			sms_empty.setVisibility(View.GONE);
			Tools.logSh("cursor的长度为：" + smsRecordCursor.getCount());
			mListView.setAdapter(new SmsRecordAdapter(getApplicationContext(),
					smsRecordCursor));
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// show detail
				Tools.logSh("进入sms详细界面");
				Intent intent = new Intent(PrivateCommActivity.this,
						SmsDetailActivity.class);
				// 姓名带过去
				TextView sms_tv_name = (TextView) view
						.findViewById(R.id.sms_tv_name);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.sms_tv_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString().trim();
				Tools.logSh("Name==" + name);
				intent.putExtra("Name", name);
				intent.putExtra("Number", number);
				startActivity(intent);

			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView sms_tv_name = (TextView) view.findViewById(R.id.sms_tv_name);
				TextView sms_tv_number = (TextView) view.findViewById(R.id.sms_tv_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString().trim();
				
				showSmsDialog(name,number);
				return false;
			}
		});
	}

	private void setContactAdapter(ListView mListView) {
		if (contactCursor.getCount() == 0) {
			Tools.logSh("没有数据");

			mListView.setEmptyView(contact_empty);

		} else {
			contact_empty.setVisibility(View.GONE);
			Tools.logSh("cursor的长度为：" + contactCursor.getCount());
		}

		Tools.logSh("mCursor的长度为：" + contactCursor.getCount());
		mContactAdapter = new ContactAdapter(getApplicationContext(),
				contactCursor);
		mListView.setAdapter(mContactAdapter);
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv_phone_num = (TextView) view
						.findViewById(R.id.tv_phone_num);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_type = (TextView) view.findViewById(R.id.tv_type);
				String phone_number = tv_phone_num.getText().toString().trim();
				String name = tv_name.getText().toString().trim();
				String strType = tv_type.getText().toString().trim();
				int type = 0;
				if ("[立即挂断]".equals(strType)) {
					type = 1;
				} else {
					type = 0;
				}
				// 显示编辑
				showContactDialog(name, phone_number, type);
				return false;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv_phone_num = (TextView) view
						.findViewById(R.id.tv_phone_num);
				String phone_number = tv_phone_num.getText().toString().trim();
				MakeCallUtils.makeCall(PrivateCommActivity.this, phone_number);
			}
		});
	}

	public void showContactDialog(final String name, final String address,
			final int type) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "打电话", "发短信", "编辑", "退出" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							MakeCallUtils.makeCall(PrivateCommActivity.this,
									address);
							break;

						case 1:

							Intent smsDetailIntent = new Intent(
									PrivateCommActivity.this,
									SmsDetailActivity.class);
							// 姓名带过去
							Tools.logSh("Name==" + name);
							smsDetailIntent.putExtra("Name", name);
							startActivity(smsDetailIntent);
							break;
						case 2:
							Intent intent = new Intent(
									PrivateCommActivity.this,
									PrivateContactEditActivity.class);
							intent.putExtra("Display_Name", name);
							intent.putExtra("Address", address);
							intent.putExtra("Type", type);

							startActivity(intent);

							break;
						case 3:

							break;
						}
					}
				});
		builder.create().show();

	}

	public void showPhoneDialog(final String name, final String number) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("" + name);
		builder.setItems(new String[] { "打电话", "发短信", "删除" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							// 获取号码
							String phone = ContactUtils.queryContactNumber(
									PrivateCommActivity.this, name);
							MakeCallUtils.makeCall(PrivateCommActivity.this,
									phone);
							break;

						case 1:
							Intent smsDetailIntent = new Intent(
									PrivateCommActivity.this,
									SmsDetailActivity.class);
							// 姓名带过去
							Tools.logSh("Name==" + name);
							smsDetailIntent.putExtra("Name", name);
							startActivity(smsDetailIntent);

							break;
						case 2:

							showDeletePhoneDialog(number);

							Toast.makeText(PrivateCommActivity.this, "还没做",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
		builder.create().show();
	}

	public void showSmsDialog(final String name, final String number) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("" + name);
		builder.setItems(new String[] { "回复短信", "呼叫联系人", "恢复到手机收件箱", "删除此会话" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							Intent intent = new Intent(PrivateCommActivity.this,
									SmsDetailActivity.class);
							intent.putExtra("Name", name);
							intent.putExtra("Number", number);
							startActivity(intent);
							break;

						case 1:
							MakeCallUtils.makeCall(PrivateCommActivity.this,
									number);
							break;
						case 2:

							Toast.makeText(PrivateCommActivity.this, "还没做",
									Toast.LENGTH_SHORT).show();
							break;
						case 3:

							Toast.makeText(PrivateCommActivity.this, "还没做",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
		builder.create().show();
	}

	private void showDeletePhoneDialog(String number) {
		SureDialog dialog = new SureDialog(this, number);

	}

	private void showAddContactDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("添加新的联系人");
		builder.setItems(new String[] { "从联系人添加", "手工输入号码","从通话记录添加","从短信记录添加"},
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						TextView textView = (TextView) findViewById(R.id.text);
						switch (which) {
						case 0:
							Intent intent = new Intent(
									PrivateCommActivity.this,
									AddContactActivity.class);
							startActivity(intent);
							break;
						case 1:
							Intent intent2 = new Intent(
									PrivateCommActivity.this,
									HandInputActivity.class);
							startActivity(intent2);
							break;
							
						case 2:
							Toast.makeText(PrivateCommActivity.this, "别急，正在努力的完成。。。。", Toast.LENGTH_SHORT).show();
							break;
							
						case 3:
							Toast.makeText(PrivateCommActivity.this, "别急，正在努力的完成。。。。", Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
		builder.create().show();

	}

	private void showEditContactDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示:确定将联系人移出隐私空间?");

		builder.setPositiveButton("移出", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					RestoreContactTast tast = new RestoreContactTast(
							PrivateCommActivity.this);
					tast.execute();

				}
			}
		});
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();

	}

	private void showEditPhoneDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("提示:确定删除此条通话记录");
		builder.setPositiveButton("确定", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					DeletePhoneTast task = new DeletePhoneTast(
							PrivateCommActivity.this);
					task.execute();
				}
			}
		});
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();

	}

	private void showEditDeleteSmsDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("提示:确定删除整个短信会话");
		builder.setPositiveButton("确定", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					DeleteSmsTast task = new DeleteSmsTast(
							PrivateCommActivity.this);
					task.execute();
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();

	}

	private void showEditRecoverSmsDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("提示:是否将隐私短信恢复到系统");
		builder.setPositiveButton("确定", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Tools.logSh("selectContacts=======" + selectContacts);
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要恢复的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					RestoreSmsTast task = new RestoreSmsTast(
							PrivateCommActivity.this);
					task.execute();
				}
			}
		});
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.private_comm, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		Tools.logSh("currIndex=============" + currIndex);
		// 当从增加联系人页面返回时，跟新数据
		if (currIndex == 2) {
			if (mContactAdapter != null) {
				contactCursor.requery();
				Tools.logSh("contactCursor.getCount()============="
						+ contactCursor.getCount());
				mContactAdapter.notifyDataSetChanged();
			} else {
				contactCursor.requery();
				setContactAdapter(contact_listview);
			}
		}

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (showEdit) {
			showNormalUI();
			showEdit = !showEdit;
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private class DeletePhoneTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public DeletePhoneTast(Context context) {
			this.mContext = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "正在执行", Toast.LENGTH_SHORT).show();
			ArrayUtils arrayUtils = new ArrayUtils();
			DelectOurPhoneDetailsUtils.deletePhoneDetails(
					PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			DelectOurPhoneRecordsUtils.deletePhoneRecords(
					PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			selectContacts.clear();

			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
			Toast.makeText(mContext, "执行完毕", Toast.LENGTH_SHORT).show();

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private class RestoreContactTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public RestoreContactTast(Context context) {
			this.mContext = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "正在执行", Toast.LENGTH_SHORT).show();
			ArrayUtils arrayUtils = new ArrayUtils();
			deleteContacts(arrayUtils.listToArray(selectContacts));
			selectContacts.clear();

			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
			Toast.makeText(mContext, "执行完毕", Toast.LENGTH_SHORT).show();

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private class RestoreSmsTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public RestoreSmsTast(Context context) {
			this.mContext = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "正在执行", Toast.LENGTH_SHORT).show();
			ArrayUtils arrayUtils = new ArrayUtils();
			RestoreSystemSmsUtils.restoreSms(PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			DelectOurSmsDetailsUtils.deleteSmsDetails(PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			DelectOurSmsRecordsUtils.deleteSmsRecords(PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			selectContacts.clear();

			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
			Toast.makeText(mContext, "执行完毕", Toast.LENGTH_SHORT).show();

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private class DeleteSmsTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public DeleteSmsTast(Context context) {
			this.mContext = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "正在执行", Toast.LENGTH_SHORT).show();
			ArrayUtils arrayUtils = new ArrayUtils();
			DelectOurSmsDetailsUtils.deleteSmsDetails(PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			DelectOurSmsRecordsUtils.deleteSmsRecords(PrivateCommActivity.this,
					arrayUtils.listToArray(selectContacts));
			selectContacts.clear();

			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
			Toast.makeText(mContext, "执行完毕", Toast.LENGTH_SHORT).show();

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

}
