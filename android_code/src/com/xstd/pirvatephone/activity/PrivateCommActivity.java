package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneDetailDaoUtils;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDao;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.dao.sms.SmsRecordDao;
import com.xstd.pirvatephone.dao.sms.SmsRecordDaoUtils;
import com.xstd.privatephone.adapter.ContactAdapter;
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.adapter.MyViewPagerAdapter;
import com.xstd.privatephone.adapter.PhoneRecordAdapter;
import com.xstd.privatephone.adapter.SmsRecordAdapter;
import com.xstd.privatephone.tools.Tools;

import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2, view3;// 各个页卡

	private Button ib_back;
	private Button edit;

	private int smsPageNum = 0;
	private int dialPageNum = 1;
	private int contactPageNum = 2;
	private ListView contact_lv_cont;
	private Button contact_add_contacts;

	private TextView contact_empty;
	private Cursor contactCursor;
	private ListView dial_lv_cont;
	private ListView sms_lv_cont;
	private TextView sms_tv_empty;
	private TextView dial_tv_empty;

	/*
	 * getContentResolver().delete(Uri.parse("content://sms/"+ sms.getId()),
	 * null, null);
	 */
	boolean showEdit = false;
	private RelativeLayout contact_content;
	private RelativeLayout contact_rl_content;

	private ArrayList selectContactsNumber;
	private Button sms_btn_recover;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_comm);

		initView();

		selectContactsNumber = new ArrayList();
		InitImageView();
		InitTextView();
		InitViewPager();
	}

	private void InitViewPager() {
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
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化头标
	 */

	private void InitTextView() {
		textView1 = (Button) findViewById(R.id.text1);
		textView2 = (Button) findViewById(R.id.text2);
		textView3 = (Button) findViewById(R.id.text3);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
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
		// 1、title栏
		edit = (Button) findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {
			private ListView contact_listview_up;

			@Override
			public void onClick(View v) {

				if (currIndex == 0) {

				} else if (currIndex == 1) {

				} else if (currIndex == 2) {

					Tools.logSh("currIndex==" + currIndex);
					if (contact_content == null) {
						contact_content = (RelativeLayout) findViewById(R.id.contact_edit_content);
						contact_rl_content.setVisibility(View.VISIBLE);
						showEdit = true;// 显示编辑栏。

						contact_listview_up = (ListView) findViewById(R.id.contact_edit_list_gone);
						// 展示数据
						//getContact(contact_listview_up, contact_empty);
						editContact(contact_listview_up, contact_empty);
						Tools.logSh("进入编辑模式");
					} else {
						contact_listview_up = (ListView) findViewById(R.id.contact_edit_list_gone);
						
						if (showEdit) {
							contact_content.setVisibility(View.GONE);
							contact_rl_content.setVisibility(View.VISIBLE);
							Tools.logSh("退出编辑模式");
						} else {
							contact_content.setVisibility(View.VISIBLE);
							contact_rl_content.setVisibility(View.GONE);
						//	getContact(contact_listview_up, contact_empty);
							Tools.logSh("再次进入编辑模式2");
							editContact(contact_listview_up, contact_empty);
						}
						showEdit = !showEdit;
					}

				}
			}
		});

		ib_back = (Button) findViewById(R.id.ib_back);

		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

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

			// 加载数据
			fillData();

			Tools.logSh("当前页面" + currIndex);
		}

	}

	/**
	 * 根据当前页面加载不同的控件
	 */
	private void fillData() {

		if (currIndex == smsPageNum) {
			
			sms_lv_cont = (ListView) findViewById(R.id.sms_lv_cont);
			sms_tv_empty = (TextView) findViewById(R.id.sms_tv_empty);
			
			sms_btn_recover = (Button) findViewById(R.id.bt_recover);
			sms_btn_recover.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), "hello", 0).show();
				}
			});
			
			Tools.logSh("接收到增加联系人点击" + currIndex);
			getSmsRecord();

			return;
		}
		if (currIndex == dialPageNum) {
			dial_lv_cont = (ListView) findViewById(R.id.dial_lv_cont);
			dial_tv_empty = (TextView) findViewById(R.id.dial_tv_empty);

			getPhoneRecord();

			return;
		}
		if (currIndex == contactPageNum) {
			contact_lv_cont = (ListView) findViewById(R.id.contact_lv_cont);
			contact_empty = (TextView) findViewById(R.id.contact_tv_empty);
			contact_add_contacts = (Button) findViewById(R.id.contact_add_contacts);
			contact_rl_content = (RelativeLayout) findViewById(R.id.contact_rl_content);

			contact_add_contacts.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// show dialog
					Tools.logSh("接收到增加联系人点击" + currIndex);
					showDialog();
				}
			});

			// 从我们的数据库读取隐私联系人，展示到页面上
			getContact(contact_lv_cont, contact_empty);
		}
	}

	private void getSmsRecord() {
		SmsRecordDao smsRecordDao = SmsRecordDaoUtils
				.getSmsRecordDao(getApplicationContext());
		SQLiteDatabase database = smsRecordDao.getDatabase();
		Tools.logSh("getSmsRecord()执行了");
		Cursor smsRecordCursor = database.query("SMS_RECORD", null, null, null,
				null, null, null);

		Tools.logSh("getSmsRecord()执行了+" + smsRecordCursor.getCount());
		if (smsRecordCursor.getCount() == 0) {
			Tools.logSh("没有数据");
			
			sms_lv_cont.setEmptyView(sms_tv_empty);

		} else {
			Tools.logSh("cursor的长度为：" + smsRecordCursor.getCount());
			sms_lv_cont.setAdapter(new SmsRecordAdapter(
					getApplicationContext(), smsRecordCursor));
		}
		
		sms_lv_cont.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// show detail
				Tools.logSh("进入sms详细界面");
				Intent intent = new Intent(PrivateCommActivity.this,SmsDetailActivity.class);
				//号码带过去
				intent.putExtra("Number", 1234+"");
				startActivity(intent);
			
			}
		});
	}

	private void getPhoneRecord() {
		PhoneRecordDao phoneRecordDao = PhoneRecordDaoUtils
				.getPhoneRecordDao(getApplicationContext());
		SQLiteDatabase database = phoneRecordDao.getDatabase();
		
		//获取通话记录表中所有的电话号码
		HashSet<String> set = new HashSet<String>();
		Cursor recordCursor = database.query("PHONE_RECORD", null, null, null,
				null, null, null);

		if (recordCursor.getCount() == 0) {
			dial_lv_cont.setEmptyView(dial_tv_empty);
		} else {
			Tools.logSh("cursor的长度为：" + recordCursor.getCount());
			dial_lv_cont.setAdapter(new PhoneRecordAdapter(
					getApplicationContext(), recordCursor));
		}
	}

	private void editContact(ListView lv, TextView tv){
		
		Tools.logSh("进入了editContact界面");
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());
		SQLiteDatabase database = contactInfoDao.getDatabase();

		 Cursor cursor = database.query("CONTACT_INFO", null, null, null, null,
				null, null);
		if (contactCursor.getCount() == 0) {
			lv.setEmptyView(tv);

			Tools.logSh("listview没有数据");
			
		} else {
			Tools.logSh("listview有数据"+contactCursor.getCount());
			
			lv.setAdapter(new EditContactAdapter(getApplicationContext(),
					cursor));
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "条目呗点击了"+position, 0).show();
					CheckBox btn_check = (CheckBox) view
							.findViewById(R.id.checkbox);
					
					TextView phoneNumber = (TextView) view.findViewById(R.id.tv_phone_num);
					String phone = phoneNumber.getText().toString().trim();
					
					// 记录选项
					if (!btn_check.isChecked()) {
						selectContactsNumber.add(position);
					} else {
						selectContactsNumber.remove(position);
					}

					btn_check.setChecked(!btn_check.isChecked());
					Tools.logSh("listview条目被点击了"+selectContactsNumber.size() + "::"+phone);
				}
			});
		}
	}
	
	/**
	 * 获取我们数据库联系人
	 */
	private void getContact(ListView lv, TextView tv) {

		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(getApplicationContext());
		SQLiteDatabase database = contactInfoDao.getDatabase();

		Cursor contactCursor = database.query("CONTACT_INFO", null, null, null, null,
				null, null);
		if (contactCursor.getCount() == 0) {
			lv.setEmptyView(tv);
		} else {
			// 通知数据获取完成
			Tools.logSh("cursor的长度为：" + contactCursor.getCount());
			lv.setAdapter(new ContactAdapter(getApplicationContext(),
					contactCursor));
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 讲电话号码传递给系统拨号服务，开启电话
					Toast.makeText(getApplicationContext(), "" +
							"开启电话" + position,
							Toast.LENGTH_SHORT).show();
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_phone_num);
					String phone_number = tv_phone_num.getText().toString()
							.trim();

					if (phone_number != null && !phone_number.equals("")) {

						// 封装一个拨打电话的intent，并且将电话号码包装成一个Uri对象传入
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phone_number));
						startActivity(intent);// 内部类
					}

				}
			});
		}
	}

	private void showDialog() {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("添加新的联系人");
		builder.setItems(new String[] { "从联系人添加", "手工输入号码" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						TextView textView = (TextView) findViewById(R.id.text);
						switch (which) {
						case 0:
							Intent intent = new Intent(
									PrivateCommActivity.this,
									ContactActivity.class);
							startActivity(intent);
							break;
						case 1:
							Intent intent2 = new Intent(
									PrivateCommActivity.this,
									HandInputActivity.class);
							startActivity(intent2);
							break;
						}
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

}
