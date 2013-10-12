package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.xstd.pirvatephone.utils.DelectSystemContactUtils;
import com.xstd.pirvatephone.utils.DelectSystemPhoneUtils;
import com.xstd.pirvatephone.utils.DelectSystemSmsUtils;
import com.xstd.pirvatephone.utils.GetContactUtils;
import com.xstd.pirvatephone.utils.WriteContactUtils;
import com.xstd.pirvatephone.utils.WritePhoneDetailUtils;
import com.xstd.pirvatephone.utils.WritePhoneRecordUtils;
import com.xstd.pirvatephone.utils.WriteSmsDetailUtils;
import com.xstd.pirvatephone.utils.WriteSmsRecordUtils;
import com.xstd.privatephone.adapter.AddContactAdapter;
import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

public class AddContactActivity extends BaseActivity {

	private Button btn_back;
	private Button bt_sure;
	private Button bt_cancle;
	private ListView lv_contact;
	private TextView tv_empty;

	private GetContactTast task;

	private Uri smsUri = Uri.parse("content://sms/");
	private static final int UPDATE = 1;
	private static final int FINISH_GET_CONTACT = 2;
	private static final int MSG_KEY = 3;

	/** 选取转换为隐私联系人的号码 **/
	private static ArrayList<String> mSelectContactsNumber = new ArrayList<String>();

	private static ArrayList<MyContactInfo> mContactInfos;

	ListView mListView = null;

	private static final int DISPLAY_NAME_COLUMN_INDEX = 1;
	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");

	static final String[] CALL_LOG_PROJECTION = new String[] {
			CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NUMBER,
			CallLog.Calls.TYPE, };

	private ProgressBar pb_empty;
	private ImageView iv_empty_bg;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE:
				Tools.logSh("接受到消息");
				lv_contact.setEmptyView(iv_empty_bg);
				pb_empty.setVisibility(View.GONE);
				AddContactAdapter mAdapter= new AddContactAdapter(getApplicationContext(),
						mContactInfos);
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
							mContactInfos.get(position).setChecked(true);
							Tools.logSh("选中了" + mContactInfos.get(position));
						} else {
							mContactInfos.get(position).setChecked(false);
							Tools.logSh("取消了" + mContactInfos.get(position));
						}
						btn_check.setChecked(!btn_check.isChecked());

					}
				});
				break;

			case FINISH_GET_CONTACT:
				Tools.logSh("获取数据库联系人完成");
				pb_empty.setVisibility(View.GONE);
				break;
			
		case MSG_KEY:
			refreshListView(msg.getData().get("value").toString());
			}
		};
	};
	private Button btn_edit;
	private TextView tv_title;
	private TextView et_search;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		initView();

		task = new GetContactTast(getApplicationContext());
		task.execute();
	}

	private void initView() {
		//title
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("从联系人添加");
		et_search = (TextView) findViewById(R.id.et_search);
		
		//bottom
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);
		//content
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		pb_empty = (ProgressBar) findViewById(R.id.pb_empty);
		iv_empty_bg = (ImageView) findViewById(R.id.iv_empty_bg);

		btn_edit.setVisibility(View.GONE);
		
		//search
		et_search.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable editer) {
			}

			public void beforeTextChanged(CharSequence value, int arg0,
					int arg1, int arg2) {
			}

			public void onTextChanged(CharSequence value, int arg0, int arg1,
					int arg2) {
				Message msg = new Message();
				msg.what = MSG_KEY;
				Bundle data = new Bundle();
				data.putString("value", value.toString());
				msg.setData(data);
				handler.sendMessage(msg);
			}
		});
		
		btn_back.setOnClickListener(new OnClickListener() {

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
		Tools.logSh(mContactInfos.size() + "");
		
		ArrayList<MyContactInfo> mSelectContactInfos = new ArrayList<MyContactInfo>();

		
		for (int i = 0; i < mContactInfos.size(); i++) {
			if(mContactInfos.get(i).isChecked()){
				mSelectContactInfos.add(mContactInfos.get(i));
			}
		}
		Tools.logSh("----------------------------------------");
		
		// 数组转换
		String[] selectPhones = new String[mSelectContactInfos.size()];
		Object[] obj =  mSelectContactInfos.toArray();
		for (int i = 0; i < obj.length; i++) {
			selectPhones[i] =  ((MyContactInfo)(obj[i])).getAddress();
			Tools.logSh(selectPhones[i]);
		}

		Tools.logSh("2222222222----------------------------------------");
		//将系统联系人复制到我们数据库
		WriteContactUtils mWriteContactUtils = new WriteContactUtils(getApplicationContext(),selectPhones);
		mWriteContactUtils.writeContact();

		Tools.logSh("11111111111111111111111111111111111111");
		//将系统通话记录detail复制到我们数据库
		WritePhoneDetailUtils mWritePhoneDetailUtils = new WritePhoneDetailUtils(getApplicationContext(),selectPhones);
		mWritePhoneDetailUtils.writePhoneDetail();

		Tools.logSh("222222222222222222222222222222222222222");
		//将系统通话记录record复制到我们数据库
		WritePhoneRecordUtils mWritePhoneRecordUtils = new WritePhoneRecordUtils(getApplicationContext(),selectPhones);
		mWritePhoneRecordUtils.writePhoneRecord();
		
		
		Tools.logSh("333333333333333333333333333333333333333333");
		// 将系统sms detail复制到我们数据库
		WriteSmsDetailUtils mWriteSmsDetailUtils = new WriteSmsDetailUtils(getApplicationContext(),selectPhones);
		mWriteSmsDetailUtils.writeSmsDetail();
		
		// 将系统sms record复制到我们数据库
		WriteSmsRecordUtils mWriteSmsRecordUtils = new WriteSmsRecordUtils(getApplicationContext(),selectPhones);
		mWriteSmsRecordUtils.writeSmsRecord();
		// 显示选择对话框
		showDeleteDialog(selectPhones);

	}

	public void showDeleteDialog(final String[] selectPhones) {
		final Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] { "移动联系人同时删除手机数据库", "仅移动联系人" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							// 删除系统库中的联系人。
							DelectSystemContactUtils mDelectSystemContactUtils = new DelectSystemContactUtils(getApplicationContext(),selectPhones);
							mDelectSystemContactUtils.deleteContacts();
							
							DelectSystemPhoneUtils mDelectSystemPhoneUtils = new DelectSystemPhoneUtils(getApplicationContext(),selectPhones);
							mDelectSystemPhoneUtils.deletePhone();
							
							DelectSystemSmsUtils mDelectSystemSmsUtils = new DelectSystemSmsUtils(getApplicationContext(),selectPhones);
							mDelectSystemSmsUtils.deleteSms();
							
							finish();
							break;
						case 1:
							// 不删除系统库中的联系人
							
							finish();
							break;
						}
					}
				});
		builder.create().show();

	}

	private void refreshListView(String value) {
		//根据search条件查询
		
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
			GetContactUtils mGetContactUtils = new GetContactUtils(getApplicationContext());
			mContactInfos = mGetContactUtils.getContacts();

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
}
