package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.pirvatephone.utils.ArrayUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.DelectOurContactUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneRecordsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsRecordsUtils;
import com.xstd.pirvatephone.utils.MakeCallUtils;
import com.xstd.pirvatephone.utils.PxParseUtils;
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

public class PrivateCommActivity extends BaseActivity {

	private com.xstd.privatephone.view.MyViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private LinearLayout textView1, textView2, textView3;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex;// 当前页卡编号
	private int animationPicWidth;// 动画图片宽度
	private View view1, view2, view3;// 各个页卡
	private Button btn_sms, btn_phone, btn_contact;

	private RelativeLayout ib_back;
	private Button edit;

	private static final int SMS_PAGE = 0;
	private static final int DIAL_PAGE = 1;
	private static final int CONTACT_PAGE = 2;
	private RelativeLayout contact_add_contacts;

	private TextView contact_empty;

	boolean isEditing = false;

	private static final int EDIT_EXET = 0;
	private static final String TAG = "PrivateCommActivity";

	private CheckBox edit_checkbox;
	private LinearLayout contact_ll_count;
	private TextView contact_count;

	private ListView contact_listview;
	private ListView phone_listview;
	private ListView sms_listview;
	private ListView edit_listview;

	private TextView phone_empty;
	private TextView sms_empty;

	private LinearLayout edit_ll_body;
	private RelativeLayout edit_rl_select;

	private RelativeLayout edit_ll_sms_bt;

	private RelativeLayout btn_recover_sms;
	private RelativeLayout btn_remove_sms;
	private RelativeLayout btn_delete_contact;
	private RelativeLayout btn_delete_phone;

	private PhoneRecordDao phoneRecordDao;
	private ContactInfoDao contactInfoDao;
	private SmsRecordDao smsRecordDao;

	private Cursor contactCursor;
	private Cursor smsRecordCursor;
	private Cursor phoneRecordCursor;

	private EditContactAdapter editContactAdapter;
	private EditPhoneAdapter editPhoneAdapter;
	private EditSmsAdapter editSmsAdapter;

	private ContactAdapter mContactAdapter;
	private SmsRecordAdapter smsRecordAdapter;
	private PhoneRecordAdapter phoneRecordAdapter;

	private AlertDialog deleteContactDialog;
	private Dialog recoverContactProgressdialog;
	private TextView tv_dialog_show;
	private TextView recover_tv_progress;
	private TextView recover_tv_progress_detail;
	private ProgressBar recover_progress;

	private final ArrayList<String> selectContacts = new ArrayList<String>();

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case EDIT_EXET:
				Tools.logSh("currIndex=====" + currIndex);
				reinitData();
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_comm);

		Tools.logSh(PxParseUtils.px2dip(this, 30) + "");

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

	@SuppressWarnings("deprecation")
	private void reinitData() {

		isEditing = false;
		edit_ll_body.setVisibility(View.GONE);
		textView1.setClickable(true);
		textView2.setClickable(true);
		textView3.setClickable(true);

		if (recoverContactProgressdialog != null
				&& recoverContactProgressdialog.isShowing()) {
			recoverContactProgressdialog.dismiss();
			recoverContactProgressdialog = null;
		}
		if (deleteContactDialog != null && deleteContactDialog.isShowing()) {
			deleteContactDialog.dismiss();
			deleteContactDialog = null;
		}

		if (smsRecordCursor != null) {
			smsRecordCursor.requery();
		}

		if (phoneRecordCursor != null) {
			phoneRecordCursor.requery();
		}
		if (contactCursor != null) {
			contactCursor.requery();
		} else {
			contactCursor.requery();

		}

		switch (currIndex) {
		case SMS_PAGE:
			if (smsRecordCursor.getCount() == 0) {
				edit.setVisibility(View.GONE);
			} else {
				edit.setVisibility(View.VISIBLE);
			}
			if (smsRecordAdapter != null) {
				setSmsRecordAdapter(sms_listview);
			}
			break;

		case DIAL_PAGE:
			if (phoneRecordCursor.getCount() == 0) {
				edit.setVisibility(View.GONE);
			} else {
				edit.setVisibility(View.VISIBLE);
			}
			if (phoneRecordAdapter != null) {
				setPhoneRecordAdapter(phone_listview);
			}
			break;
		case CONTACT_PAGE:
			if (contactCursor.getCount() == 0) {
				edit.setVisibility(View.GONE);
			} else {
				edit.setVisibility(View.VISIBLE);
			}
			if (mContactAdapter != null) {
				setContactAdapter(contact_listview);
			}
			break;
		}
	}

	@Override
	public void onDestroy() {

		if (contactCursor != null) {
			contactCursor.close();
		}
		if (phoneRecordCursor != null) {
			phoneRecordCursor.close();
		}
		if (smsRecordCursor != null) {
			smsRecordCursor.close();
		}
		super.onDestroy();
	}

	private void InitViewPager() {
		// 初始化头标
		textView1 = (LinearLayout) findViewById(R.id.text1);
		textView2 = (LinearLayout) findViewById(R.id.text2);
		textView3 = (LinearLayout) findViewById(R.id.text3);

		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_phone = (Button) findViewById(R.id.btn_phone);
		btn_contact = (Button) findViewById(R.id.btn_contact);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));

		viewPager = (com.xstd.privatephone.view.MyViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.private_comm_spac_sms, null);
		view2 = inflater.inflate(R.layout.private_comm_spac_dial, null);
		view3 = inflater.inflate(R.layout.private_comm_spac_contact, null);
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
		imageView = (ImageView) findViewById(R.id.animation_pic);
		/*
		 * animationPicWidth = BitmapFactory.decodeResource(getResources(),
		 * R.id.animation_pic).getWidth();// 获取图片宽度
		 */
		animationPicWidth = imageView.getWidth();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - animationPicWidth) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void initView() {
		// 1、title
		edit = (Button) findViewById(R.id.btn_edit);

		// content-all
		edit_ll_body = (LinearLayout) findViewById(R.id.edit_ll_body);

		// edit-checkall
		edit_rl_select = (RelativeLayout) findViewById(R.id.edit_rl_select);
		edit_checkbox = (CheckBox) findViewById(R.id.edit_checkbox_all);
		edit_listview = (ListView) findViewById(R.id.edit_listview);

		// edit-buttom
		edit_ll_sms_bt = (RelativeLayout) findViewById(R.id.edit_ll_sms_bt);

		// edit-bottom-button
		btn_recover_sms = (RelativeLayout) findViewById(R.id.edit_btn_recover_sms);
		btn_remove_sms = (RelativeLayout) findViewById(R.id.edit_btn_remove_sms);
		btn_delete_contact = (RelativeLayout) findViewById(R.id.edit_btn_delete_contact);
		btn_delete_phone = (RelativeLayout) findViewById(R.id.edit_btn_delete_phone);

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isEditing = !isEditing;
				Tools.logSh("currIndex==" + currIndex);
				if (isEditing) {
					isEditingUI();
				} else {
					showNormalUI();
					textView1.setClickable(true);
					textView2.setClickable(true);
					textView3.setClickable(true);
				}
			}
		});

		ib_back = (RelativeLayout) findViewById(R.id.btn_back);
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		showNormalUI();
	}

	private void isEditingBottom() {
		if (currIndex == SMS_PAGE) {
			edit_ll_sms_bt.setVisibility(View.VISIBLE);
			btn_delete_contact.setVisibility(View.GONE);
			btn_delete_phone.setVisibility(View.GONE);
		} else if (currIndex == DIAL_PAGE) {
			edit_ll_sms_bt.setVisibility(View.GONE);
			btn_delete_phone.setVisibility(View.VISIBLE);
			btn_delete_contact.setVisibility(View.GONE);
		} else if (currIndex == CONTACT_PAGE) {
			edit_ll_sms_bt.setVisibility(View.GONE);
			btn_delete_phone.setVisibility(View.GONE);
			btn_delete_contact.setVisibility(View.VISIBLE);
		}

	}

	private void isEditingUI() {
		textView1.setClickable(false);
		textView2.setClickable(false);
		textView3.setClickable(false);

		edit_ll_body.setVisibility(View.VISIBLE);
		edit_checkbox.setChecked(false);
		selectContacts.clear();

		Tools.logSh("进入编辑模式");
		if (currIndex == SMS_PAGE) {

			editSmsAdapter = new EditSmsAdapter(PrivateCommActivity.this,
					smsRecordCursor);

			edit_listview.setAdapter(editSmsAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.edit_sms_checkbox);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.edit_sms_tv_number);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);
						if (selectContacts.size() == smsRecordCursor.getCount()) {
							edit_checkbox.setChecked(true);
						}
						Tools.logSh("selectContacts==" + selectContacts);
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
							Tools.logSh("selectContacts==" + selectContacts);
						}
						if (selectContacts.size() < smsRecordCursor.getCount()) {
							edit_checkbox.setChecked(false);
						}

						editSmsAdapter.notifyChange(selectContacts);
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
						isEditingDeleteSmsDialog();
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
						isEditingRecoverSmsDialog();
					}

				}
			});

			edit_rl_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//
					edit_checkbox.setChecked(!edit_checkbox.isChecked());
					if (edit_checkbox.isChecked()) {
						// get smsRecordCorsor
						getSmsRecord();
						if (smsRecordCursor != null
								&& smsRecordCursor.getCount() > 0) {
							Tools.logSh("smsRecordCursor不为空");
							while (smsRecordCursor.moveToNext()) {
								String number = smsRecordCursor.getString(smsRecordCursor
										.getColumnIndex(SmsRecordDao.Properties.Phone_number.columnName));
								selectContacts.add(number);
								Tools.logSh("selectContacts==="
										+ selectContacts);
							}
						}
					} else {
						selectContacts.clear();
					}
					editSmsAdapter.notifyChange(selectContacts);
				}
			});

		} else if (currIndex == DIAL_PAGE) {

			editPhoneAdapter = new EditPhoneAdapter(PrivateCommActivity.this,
					phoneRecordCursor);

			edit_listview.setAdapter(editPhoneAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.iv_check);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_number);
					String phone_number = tv_phone_num.getText().toString()
							.replace("( ", "").replace(" )", "").trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);

						if (selectContacts.size() == phoneRecordCursor
								.getCount()) {
							edit_checkbox.setChecked(true);
						}
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
						}
						if (selectContacts.size() < phoneRecordCursor
								.getCount()) {
							edit_checkbox.setChecked(false);
						}
					}
					editPhoneAdapter.notifyChange(selectContacts);
				}
			});
			btn_delete_phone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						isEditingPhoneDialog();
					}
				}
			});

			edit_rl_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					edit_checkbox.setChecked(!edit_checkbox.isChecked());
					if (edit_checkbox.isChecked()) {
						getPhoneRecord();
						if (phoneRecordCursor != null
								&& phoneRecordCursor.getCount() > 0) {
							while (phoneRecordCursor.moveToNext()) {
								String number = phoneRecordCursor.getString(phoneRecordCursor
										.getColumnIndex(PhoneRecordDao.Properties.Phone_number.columnName));
								selectContacts.add(number);
							}
						}
					} else {
						selectContacts.clear();
					}
					editPhoneAdapter.notifyChange(selectContacts);
				}
			});

		} else if (currIndex == CONTACT_PAGE) {

			editContactAdapter = new EditContactAdapter(
					PrivateCommActivity.this, contactCursor);
			edit_listview.setAdapter(editContactAdapter);

			edit_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.edit_contact_checkbox);
					checkbox.setChecked(!checkbox.isChecked());
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.edit_contact_phone_num);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (checkbox.isChecked()) {
						selectContacts.add(phone_number);
						if (selectContacts.size() == contactCursor.getCount()) {
							edit_checkbox.setChecked(true);
						}
					} else {
						if (selectContacts.contains(phone_number)) {
							selectContacts.remove(phone_number);
						}

						if (selectContacts.size() < contactCursor.getCount()) {
							edit_checkbox.setChecked(false);
						}

					}
					editContactAdapter.notifyChange(selectContacts);
				}
			});
			btn_delete_contact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (selectContacts == null || selectContacts.size() == 0) {
						Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
								Toast.LENGTH_SHORT).show();
					} else {
						showRecoverContactDialog();
					}
				}
			});

			edit_rl_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					edit_checkbox.setChecked(!edit_checkbox.isChecked());
					Tools.logSh("edit_checkbox===" + edit_checkbox.isChecked());
					if (edit_checkbox.isChecked()) {
						if (contactCursor != null
								&& contactCursor.getCount() > 0) {
							getContact();
							while (contactCursor.moveToNext()) {
								String number = contactCursor.getString(contactCursor
										.getColumnIndex(ContactInfoDao.Properties.Phone_number.columnName));
								selectContacts.add(number);
								Tools.logSh("add number===" + number
										+ "  selectContacts==="
										+ selectContacts);
							}
						}
					} else {
						selectContacts.clear();
					}
					editContactAdapter.notifyChange(selectContacts);
				}
			});
		}
		isEditingBottom();
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

		int one = offset * 2 + animationPicWidth;// 页卡1 -> 页卡2 偏移量
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

			isEditing = false;
			// 加载数据
			selectContacts.clear();
			fillData();
			Tools.logSh("当前页面" + currIndex);
		}

	}

	private void showTab(int currIndex) {
		switch (currIndex) {
		case SMS_PAGE:
			btn_sms.setBackgroundResource(R.drawable.private_comm_tab_sms_pressed);
			btn_phone
					.setBackgroundResource(R.drawable.private_comm_tab_phone_normal);
			btn_contact
					.setBackgroundResource(R.drawable.private_comm_tab_contact_normal);
			break;

		case DIAL_PAGE:
			btn_sms.setBackgroundResource(R.drawable.private_comm_tab_sms_normal);
			btn_phone
					.setBackgroundResource(R.drawable.private_comm_tab_phone_pressed);
			btn_contact
					.setBackgroundResource(R.drawable.private_comm_tab_contact_normal);
			break;
		case CONTACT_PAGE:
			btn_sms.setBackgroundResource(R.drawable.private_comm_tab_sms_normal);
			btn_phone
					.setBackgroundResource(R.drawable.private_comm_tab_phone_normal);
			btn_contact
					.setBackgroundResource(R.drawable.private_comm_tab_contact_pressed);
			break;
		}

	}

	/**
	 * 根据当前页面加载不同的控件
	 */
	private void fillData() {
		showTab(currIndex);

		Log.w(TAG, "fillData");

		if (currIndex == SMS_PAGE) {
			sms_listview = (ListView) view1.findViewById(R.id.sms_lv_cont);
			sms_empty = (TextView) view1.findViewById(R.id.sms_tv_empty);
			Tools.logSh("接收到增加联系人点击" + currIndex);
			updateNormalUI(sms_listview, sms_empty);

			return;
		}
		if (currIndex == DIAL_PAGE) {
			phone_listview = (ListView) findViewById(R.id.dial_lv_cont);
			phone_empty = (TextView) findViewById(R.id.dial_tv_empty);
			updateNormalUI(phone_listview, phone_empty);

			return;
		}
		if (currIndex == CONTACT_PAGE) {
			contact_ll_count = (LinearLayout) findViewById(R.id.contact_ll_count);
			contact_count = (TextView) findViewById(R.id.contact_tv_count);
			contact_listview = (ListView) findViewById(R.id.contact_listview);
			contact_empty = (TextView) findViewById(R.id.contact_tv_empty);
			contact_add_contacts = (RelativeLayout) findViewById(R.id.contact_ll_add);
			contact_add_contacts.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// show dialog
					Tools.logSh("接收到增加联系人点击" + currIndex);
					showAddContactDialog();
				}
			});

			// 从我们的数据库读取隐私联系人，展示到页面上
			updateNormalUI(contact_listview, contact_empty);
		}
	}

	/**
	 * 删除联系人同时恢复记录到系统数据库
	 * 
	 * @param selectNumbers
	 */
	public void deleteContacts(String[] selectNumbers, boolean flag) {
		DelectOurContactUtils.deleteContacts(this, selectNumbers);
		RecordToSysUtils recordToSysUtils = new RecordToSysUtils(this);
		recordToSysUtils.restoreContact(selectNumbers, flag);

		// 删除隐私联系人号码同时移除情景模式内该号码相关信息
		ContextModelUtils.deleteModelDetail(this, selectNumbers);
	}

	/**
	 * 获取我们数据库短信会话
	 * 
	 * @return our sms record cursor
	 * @author Jimmy
	 */
	private Cursor getSmsRecord() {
		smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(getApplicationContext());
		SQLiteDatabase database = smsRecordDao.getDatabase();
		smsRecordCursor = database.query("SMS_RECORD", null, null, null, null,
				null, null);
		Tools.logSh("getSmsRecord()执行了+" + smsRecordCursor.getCount());
		return smsRecordCursor;
	}

	/**
	 * 获取我们数据库联系人
	 * 
	 * @return our contact record cursor
	 * @author Jimmy
	 */
	private Cursor getContact() {
		contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());
		SQLiteDatabase database = contactInfoDao.getDatabase();
		contactCursor = database.query(ContactInfoDao.TABLENAME, null, null,
				null, null, null, null);
		return contactCursor;
	}

	/**
	 * 获取我们数据库通话记录
	 * 
	 * @return our phone record cursor
	 * @author Jimmy
	 */
	private Cursor getPhoneRecord() {
		phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(getApplicationContext());
		SQLiteDatabase database = phoneRecordDao.getDatabase();
		phoneRecordCursor = database.query("PHONE_RECORD", null, null, null,
				null, null, null);
		return phoneRecordCursor;
	}

	public void updateNormalUI(ListView mListView, TextView tv) {

		switch (currIndex) {
		case 0:
			setSmsRecordAdapter(mListView);
			break;

		case 1:
			setPhoneRecordAdapter(mListView);
			break;

		case 2:
			setContactAdapter(mListView);
			break;
		}
	}

	private void setPhoneRecordAdapter(ListView mListView) {

		if (phoneRecordCursor == null || phoneRecordCursor.getCount() == 0) {
			phone_empty.setVisibility(View.VISIBLE);
			edit.setVisibility(View.GONE);

		} else {
			phone_empty.setVisibility(View.GONE);
			edit.setVisibility(View.VISIBLE);
			Tools.logSh("cursor的长度为：" + phoneRecordCursor.getCount());
		}
		phoneRecordAdapter = new PhoneRecordAdapter(getApplicationContext(),
				phoneRecordCursor);
		mListView.setAdapter(phoneRecordAdapter);
		mListView.setEmptyView(phone_empty);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tools.logSh("进入电话详细界面");
				Intent intent = new Intent(PrivateCommActivity.this,
						PhoneDetailActivity.class);
				// 联系人带过去
				TextView sms_tv_name = (TextView) view
						.findViewById(R.id.tv_name);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.tv_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString()
						.replace("( ", "").replace(" )", "").trim();
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
						.findViewById(R.id.tv_name);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.tv_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString()
						.replace("( ", "").replace(" )", "").trim();
				Log.e(TAG, "name="+name+"   number="+number);
				showPhoneDialog(name, number);
				return false;
			}
		});
	}

	private void setSmsRecordAdapter(ListView mListView) {
		if (smsRecordCursor == null || smsRecordCursor.getCount() == 0) {
			sms_empty.setVisibility(View.VISIBLE);
			edit.setVisibility(View.GONE);

		} else {
			edit.setVisibility(View.VISIBLE);
			sms_empty.setVisibility(View.GONE);
			Tools.logSh("cursor的长度为：" + smsRecordCursor.getCount());
		}

		smsRecordAdapter = new SmsRecordAdapter(PrivateCommActivity.this,
				smsRecordCursor);
		mListView.setAdapter(smsRecordAdapter);
		mListView.setEmptyView(sms_empty);
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
				TextView sms_tv_name = (TextView) view
						.findViewById(R.id.sms_tv_name);
				TextView sms_tv_number = (TextView) view
						.findViewById(R.id.sms_tv_number);
				String name = sms_tv_name.getText().toString().trim();
				String number = sms_tv_number.getText().toString().trim();

				showSmsDialog(name, number);
				return false;
			}
		});
	}

	private void setContactAdapter(ListView mListView) {
		if (contactCursor == null || contactCursor.getCount() == 0) {
			Tools.logSh("没有数据");
			contact_count.setVisibility(View.GONE);
			contact_ll_count.setVisibility(View.GONE);
			contact_empty.setVisibility(View.VISIBLE);
			edit.setVisibility(View.GONE);
		} else {
			contact_ll_count.setVisibility(View.VISIBLE);
			contact_count.setVisibility(View.VISIBLE);
			contact_empty.setVisibility(View.GONE);
			edit.setVisibility(View.VISIBLE);
			contact_count.setText("全部 ( " + contactCursor.getCount() + " )");
			Tools.logSh("cursor的长度为：" + contactCursor.getCount());
		}

		Tools.logSh("mCursor的长度为：" + contactCursor.getCount());
		mContactAdapter = new ContactAdapter(getApplicationContext(),
				contactCursor);
		mListView.setAdapter(mContactAdapter);
		mListView.setEmptyView(contact_empty);
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

				showMakeDailDialog(phone_number);

			}
		});
	}

	private void showMakeDailDialog(final String number) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_phone_title_dail));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				MakeCallUtils.makeCall(PrivateCommActivity.this, number);
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	public void showContactDialog(final String name, final String address,
			final int type) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_contact_edit,
				null);
		final LinearLayout ll_make_call = (LinearLayout) view
				.findViewById(R.id.ll_make_call);
		final LinearLayout ll_send_sms = (LinearLayout) view
				.findViewById(R.id.ll_send_sms);
		final LinearLayout ll_edit = (LinearLayout) view
				.findViewById(R.id.ll_edit);
		final LinearLayout ll_out = (LinearLayout) view
				.findViewById(R.id.ll_out);
		ll_make_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				MakeCallUtils.makeCall(PrivateCommActivity.this, address);
			}
		});

		ll_send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent smsDetailIntent = new Intent(PrivateCommActivity.this,
						SmsDetailActivity.class);
				// 姓名带过去
				Tools.logSh("Number==" + address);
				smsDetailIntent.putExtra("Number", address);
				startActivity(smsDetailIntent);
			}
		});
		ll_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(PrivateCommActivity.this,
						PrivateContactEditActivity.class);
				intent.putExtra("Display_Name", name);
				intent.putExtra("Address", address);
				intent.putExtra("Type", type);

				startActivity(intent);
			}
		});
		ll_out.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	public void showPhoneDialog(final String name, final String number) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View
				.inflate(this, R.layout.dialog_private_phone_edit, null);
		TextView phone_edit_title = (TextView) view
				.findViewById(R.id.phone_edit_title);
		phone_edit_title.setText(name);
		final LinearLayout ll_make_call = (LinearLayout) view
				.findViewById(R.id.ll_make_call);
		final LinearLayout ll_send_sms = (LinearLayout) view
				.findViewById(R.id.ll_send_sms);
		final LinearLayout ll_delete = (LinearLayout) view
				.findViewById(R.id.ll_delete);
		ll_make_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				// 获取号码
				MakeCallUtils.makeCall(PrivateCommActivity.this, number);
			}
		});

		ll_send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent smsDetailIntent = new Intent(PrivateCommActivity.this,
						SmsDetailActivity.class);
				// 姓名带过去
				Tools.logSh("Number==" + number);
				smsDetailIntent.putExtra("Number", number);
				startActivity(smsDetailIntent);
			}
		});
		ll_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

				showDeletePhoneDialog(number);
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	public void showSmsDialog(final String name, final String number) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_sms_edit, null);
		TextView sms_edit_title = (TextView) view
				.findViewById(R.id.sms_edit_title);
		sms_edit_title.setText(name);
		final LinearLayout ll_send_sms = (LinearLayout) view
				.findViewById(R.id.ll_send_sms);
		final LinearLayout ll_dail_contact = (LinearLayout) view
				.findViewById(R.id.ll_dail_contact);
		final LinearLayout ll_recover = (LinearLayout) view
				.findViewById(R.id.ll_recover);
		final LinearLayout ll_delete = (LinearLayout) view
				.findViewById(R.id.ll_delete);
		ll_send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(PrivateCommActivity.this,
						SmsDetailActivity.class);
				intent.putExtra("Name", name);
				intent.putExtra("Number", number);
				startActivity(intent);
			}
		});

		ll_dail_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				MakeCallUtils.makeCall(PrivateCommActivity.this, number);
			}
		});
		ll_recover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				showRecoverSmsDialog(number);

			}
		});
		ll_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				showDeleteSmsDialog(number);

			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	protected void showDeleteSmsDialog(final String number) {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_sms_confirm_title2));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				SmsTast task2 = new SmsTast(PrivateCommActivity.this, false,
						number);
				task2.execute();
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	protected void showRecoverSmsDialog(final String number) {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_sms_confirm_title1));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SmsTast task1 = new SmsTast(PrivateCommActivity.this, true,
						number);
				task1.execute();
				dialog.dismiss();
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void showDeletePhoneDialog(final String number) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_sms_confirm_title3));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				DeletePhoneTast task = new DeletePhoneTast(
						PrivateCommActivity.this, number);
				task.execute();
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void showAddContactDialog() {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_add_contact,
				null);
		final LinearLayout ll_from_contact = (LinearLayout) view
				.findViewById(R.id.ll_from_contact);
		final LinearLayout ll_from_hand = (LinearLayout) view
				.findViewById(R.id.ll_from_hand);
		final LinearLayout ll_from_phone_record = (LinearLayout) view
				.findViewById(R.id.ll_from_phone_record);
		final LinearLayout ll_from_sms_record = (LinearLayout) view
				.findViewById(R.id.ll_from_sms_record);
		ll_from_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent1 = new Intent(PrivateCommActivity.this,
						AddFromContactActivity.class);
				startActivity(intent1);
			}
		});

		ll_from_hand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent2 = new Intent(PrivateCommActivity.this,
						HandInputActivity.class);
				startActivity(intent2);
			}
		});
		ll_from_phone_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent3 = new Intent(PrivateCommActivity.this,
						AddFromPhoneRecordActivity.class);
				startActivity(intent3);
				Toast.makeText(PrivateCommActivity.this, "别急，正在努力的完成。。。。",
						Toast.LENGTH_SHORT).show();
			}
		});
		ll_from_sms_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent4 = new Intent(PrivateCommActivity.this,
						AddFromSmsRecordActivity.class);
				startActivity(intent4);
				Toast.makeText(PrivateCommActivity.this, "别急，正在努力的完成。。。。",
						Toast.LENGTH_SHORT).show();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void showRecoverContactDialog() {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_recover_contact,
				null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(R.string.private_comm_contact_recover_title));
		
		LinearLayout ll_delete = (LinearLayout) view
				.findViewById(R.id.ll_delete);
		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		ll_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkbox.setChecked(!checkbox.isChecked());
			}
		});

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				boolean flag = false;
				if (checkbox.isChecked()) {
					// recover to system
					flag = true;
				} else {
					flag = false;
				}
				RestoreContactTast tast = new RestoreContactTast(
						PrivateCommActivity.this, flag);
				tast.execute();
				return;
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void isEditingPhoneDialog() {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_phone_title_delete));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					DeletePhoneTast task = new DeletePhoneTast(
							PrivateCommActivity.this, null);
					task.execute();
				}
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void isEditingDeleteSmsDialog() {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_sms_title_delete));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要删除的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					SmsTast task = new SmsTast(PrivateCommActivity.this, null,
							null);
					task.execute();
				}
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void isEditingRecoverSmsDialog() {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		tv_content.setText(getResources().getString(
				R.string.private_comm_sms_title_recover));
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				if (selectContacts == null || selectContacts.size() == 0) {

					Toast.makeText(PrivateCommActivity.this, "请选择要恢复的条目",
							Toast.LENGTH_SHORT).show();
				} else {
					SmsTast task = new SmsTast(PrivateCommActivity.this, true,
							null);
					task.execute();
				}
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	public void newRecoverDialogInstance(Context ctx) {
		recoverContactProgressdialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this,
				R.layout.private_comm_recover_progress_dialog, null);

		recoverContactProgressdialog.setContentView(view);
		recoverContactProgressdialog.setCanceledOnTouchOutside(false);
		recoverContactProgressdialog.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.private_comm, menu);
		return true;
	}

	@Override
	protected void onResume() {
		Tools.logSh("currIndex=============" + currIndex);
		// 当从增加联系人页面返回时，跟新数据

		Message msg = new Message();
		msg.what = EDIT_EXET;
		handler.sendMessage(msg);

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isEditing) {
			showNormalUI();
			isEditing = !isEditing;
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private class DeletePhoneTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;
		private String number;

		public DeletePhoneTast(Context context, String number) {
			this.mContext = context;
			this.number = number;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			newRecoverDialogInstance(mContext);
			Toast.makeText(mContext, "开始执行+number===" + number,
					Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			if (isEditing) {
				DelectOurPhoneDetailsUtils.deletePhoneDetails(
						PrivateCommActivity.this,
						ArrayUtils.listToArray(selectContacts));
				DelectOurPhoneRecordsUtils.deletePhoneRecords(
						PrivateCommActivity.this,
						ArrayUtils.listToArray(selectContacts));
				selectContacts.clear();

			} else {
				DelectOurPhoneDetailsUtils.deletePhoneDetails(
						PrivateCommActivity.this, new String[] { number });
				DelectOurPhoneRecordsUtils.deletePhoneRecords(
						PrivateCommActivity.this, new String[] { number });

			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {

			Message msg = new Message();
			msg.what = EDIT_EXET;
			handler.sendMessage(msg);

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
		private boolean mFlag;

		public RestoreContactTast(Context context, boolean flag) {
			this.mContext = context;
			this.mFlag = flag;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			newRecoverDialogInstance(mContext);
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			deleteContacts(ArrayUtils.listToArray(selectContacts), mFlag);
			selectContacts.clear();
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {

			Message msg = new Message();
			msg.what = EDIT_EXET;
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

	private class SmsTast extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;
		private Boolean flag;
		private String number;

		public SmsTast(Context context, Boolean flag, String number) {
			this.mContext = context;
			this.flag = flag;
			this.number = number;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			newRecoverDialogInstance(mContext);
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			if (isEditing) {
				if (flag) {// isRestore===true,restore to system
					RestoreSystemSmsUtils.restoreSms(PrivateCommActivity.this,
							ArrayUtils.listToArray(selectContacts));
				}
				DelectOurSmsDetailsUtils.deleteSmsDetails(
						PrivateCommActivity.this,
						ArrayUtils.listToArray(selectContacts));
				DelectOurSmsRecordsUtils.deleteSmsRecords(
						PrivateCommActivity.this,
						ArrayUtils.listToArray(selectContacts));
				selectContacts.clear();

			} else {
				if (flag) {// 恢复到系统数据据库
					RestoreSystemSmsUtils.restoreSms(PrivateCommActivity.this,
							new String[] { number });
				}

				DelectOurSmsDetailsUtils.deleteSmsDetails(
						PrivateCommActivity.this, new String[] { number });
				DelectOurSmsRecordsUtils.deleteSmsRecords(
						PrivateCommActivity.this, new String[] { number });
			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(mContext, "正在执行", Toast.LENGTH_SHORT).show();

			Message msg = new Message();
			msg.what = EDIT_EXET;
			handler.sendMessage(msg);

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}
}
