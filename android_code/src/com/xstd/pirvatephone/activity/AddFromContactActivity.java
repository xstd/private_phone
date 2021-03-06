package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.GetContactUtils;
import com.xstd.pirvatephone.utils.MakeCallUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.pirvatephone.utils.WriteContactUtils;
import com.xstd.privatephone.adapter.AddFromContactAdapter;
import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

public class AddFromContactActivity extends BaseActivity {

	private RelativeLayout btn_back;
	private RelativeLayout bt_sure;
	private RelativeLayout bt_cancle;
	private ListView mListView;
	private ProgressBar progressBar;
	private ImageView iv_empty;

	private Button btn_edit;
	private TextView tv_title;
	private EditText et_search;
	private String[] selectPhones;

	private static final int UPDATE = 1;
	private static final int MSG_KEY = 3;
	private static final int SHOW_TOAST = 5;
	private static final int REMOVE_FINISH = 6;

	private boolean flags_delete = false;
	private boolean firstTime = true;
	private String param;
	
	private TextView recover_tv_progress;
	private TextView recover_tv_progress_detail;
	private ProgressBar recover_progress;

	private AlertDialog progressDialog;
	private RelativeLayout btn_clear;
	private AddFromContactAdapter mAdapter;
	
	/** 选取转换为隐私联系人的号码 **/
	private ArrayList<MyContactInfo> mContactInfos = new ArrayList<MyContactInfo>();

	public static final Uri CONVERSATIONS_URI = Uri
			.parse("content://sms/conversations");

	static final String[] CALL_LOG_PROJECTION = new String[] {
			CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NUMBER,
			CallLog.Calls.TYPE, };

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE:
				Tools.logSh("接受到消息");
				
				showUpdateUI();
				
				break;

			case MSG_KEY:
				refreshListView(msg.getData().get("value").toString());
				break;
			case SHOW_TOAST:
				Toast.makeText(AddFromContactActivity.this, "已经存在该隐私联系人！！",
						Toast.LENGTH_SHORT).show();
				break;
			case REMOVE_FINISH:
				progressDialog.dismiss();
				finish();
				break;
			}
		}
	};
	
	private void showUpdateUI() {
		if(!firstTime){
			Log.e("mContactInfos size=", mContactInfos.size()+"");
			
			mAdapter = new AddFromContactAdapter(
					getApplicationContext(), mContactInfos);
			mListView.setAdapter(mAdapter);
			return;
		}
		
		mAdapter = new AddFromContactAdapter(
				getApplicationContext(), mContactInfos);
		if(mContactInfos.size()>0){
			iv_empty.setVisibility(View.VISIBLE);
		}
		mListView.setEmptyView(iv_empty);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 保存已选择联系人。
				Tools.logSh("条目被点击了");
				CheckBox btn_check = (CheckBox) view
						.findViewById(R.id.btn_check);
				btn_check.setChecked(!btn_check.isChecked());
				// 记录选项
					mContactInfos.get(position).setChecked(
							btn_check.isChecked());

				Tools.logSh("选中了" + mContactInfos.get(position));
			}
		});
		
		firstTime = false;
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_from_contact);

		initView();
		GetContactTast task = new GetContactTast(AddFromContactActivity.this);
		task.execute();

	}

	private void initView() {
		// title
		btn_back = (RelativeLayout) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("从联系人添加");

		// bottom
		bt_sure = (RelativeLayout) findViewById(R.id.bt_sure);
		bt_cancle = (RelativeLayout) findViewById(R.id.bt_cancle);
		// content
		et_search = (EditText) findViewById(R.id.et_search);
		btn_clear = (RelativeLayout) findViewById(R.id.btn_clear);
		mListView = (ListView) findViewById(R.id.lv_contact);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		iv_empty = (ImageView) findViewById(R.id.iv_empty);

		btn_edit.setVisibility(View.GONE);
		
		btn_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(et_search.getText().toString())){
					return ;
				}
				et_search.setText("");
			}
		});
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
				if(isOpen){
					imm.hideSoftInputFromWindow(view.getWindowToken(), 1000); //强制隐藏键盘
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});

		// search
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
				parseArray();
				if (selectPhones != null && selectPhones.length > 0) {
					//Tools.logSh("selectPhones中个数为：" + selectPhones.length);
					// 显示选择对话框
					showRemoveDialog(selectPhones);
				} else {
					Toast.makeText(AddFromContactActivity.this, "选择联系人不能为空！！",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void parseArray() {
		Tools.logSh("removeContact");

		ArrayList<MyContactInfo> mSelectContactInfos = new ArrayList<MyContactInfo>();

		for (int i = 0; i < mContactInfos.size(); i++) {
			if (mContactInfos.get(i).isChecked()) {
				mSelectContactInfos.add(mContactInfos.get(i));
			}
		}
		Tools.logSh(mContactInfos.size() + "");

		selectPhones = new String[mSelectContactInfos.size()];
		Object[] obj = mSelectContactInfos.toArray();
		for (int i = 0; i < obj.length; i++) {
			selectPhones[i] = ((MyContactInfo) (obj[i])).getAddress();
			Tools.logSh(selectPhones[i]);
		}

	}

	public void showRemoveDialog(final String[] selectPhones) {

		final RemoveRecordTast tast = new RemoveRecordTast(
				AddFromContactActivity.this);
		
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_private_remove_select, null);
		final LinearLayout ll_remove_and_delete = (LinearLayout) view
				.findViewById(R.id.ll_remove_and_delete);
		final LinearLayout ll_remove_only = (LinearLayout) view
				.findViewById(R.id.ll_remove_only);
		ll_remove_and_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				flags_delete = true;
				tast.execute();
				// 删除系统库中的联系人的相关信息,移动相关的通信信息
			}
		});

		ll_remove_only.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				flags_delete = false;
				tast.execute();
				// 不删除系统库中的联系人,移动相关的通信信息
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	private void refreshListView(String value) {
		param = value;
		// 根据search条件查询
		mContactInfos.clear();
		if(mContactInfos!=null){
			Log.e("mContactInfos size======", mContactInfos.size()+"");
		}
		GetContactTast task = new GetContactTast(AddFromContactActivity.this);
		task.execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}
	
	
	public void newInstance(Context ctx) {
		AlertDialog.Builder builder = new Builder(ctx);
		View dialogView = LayoutInflater.from(ctx).inflate(
				R.layout.private_comm_recover_progress_dialog, null, true);
		recover_tv_progress = (TextView) dialogView
				.findViewById(R.id.recover_tv_progress);
		recover_tv_progress_detail = (TextView) dialogView
				.findViewById(R.id.recover_tv_progress_detail);
		recover_progress = (ProgressBar) findViewById(R.id.recover_progress);
		builder.setView(dialogView);
		progressDialog = builder.create();
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	private class RemoveRecordTast extends AsyncTask<Void, Integer, Integer> {

		private Context context;

		public RemoveRecordTast(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			newInstance(AddFromContactActivity.this);
			Toast.makeText(context, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {

			parseArray();
				remove(selectPhones);

			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			Toast.makeText(context, "执行完毕", Toast.LENGTH_SHORT).show();
			Message msg = new Message();
			msg.what = REMOVE_FINISH;
			handler.sendMessage(msg);
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		public void onProgressUpdate(Integer... values) {

		}
	}

	private void remove(String[] numbers) {
		WriteContactUtils mWriteContactUtils = new WriteContactUtils(
				AddFromContactActivity.this);
		// 仅选择一个时：判断私密通信联系人是否已有该联系人
		if (numbers.length == 1) {
			Tools.logSh("仅仅选择一个联系人");
			boolean b = mWriteContactUtils.isPrivateContact(numbers);
			if (b) {
				Message msg = new Message();
				msg.what = SHOW_TOAST;
				handler.sendMessage(msg);

			} else {
				Tools.logSh("仅仅选择一个联系人，还不存在该隐私联系人");
				mWriteContactUtils.writeContact(selectPhones);

				if (flags_delete) {
					Tools.logSh("执行删除系统数据库信息");

					RecordToUsUtils recordToUsUtils = new RecordToUsUtils(
							AddFromContactActivity.this);
					recordToUsUtils.removeContactRecord(numbers,
							flags_delete);
				} else {
					Tools.logSh("仅仅移动联系人");
				
				}
			}
		} else {

			mWriteContactUtils.writeContact(numbers);
			// 选择多个时：剔除重复的联系人

			if (flags_delete) {
				Tools.logSh("执行删除系统数据库信息");

				RecordToUsUtils recordToUsUtils = new RecordToUsUtils(
						AddFromContactActivity.this);
				recordToUsUtils.removeContactRecord(numbers, flags_delete);
			} else {
				Tools.logSh("仅仅移动联系人");
			}
		}

	}

	private class GetContactTast extends AsyncTask<Void, Integer, Integer> {

		private Context context;
		private GetContactUtils getContactUtils;

		public GetContactTast(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(context, "开始执行", Toast.LENGTH_SHORT).show();
			progressBar.setVisibility(View.VISIBLE);
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			getContactUtils = new GetContactUtils(
					AddFromContactActivity.this);
			if(param==null){
				mContactInfos = getContactUtils.getContacts();
			}else{
				mContactInfos = getContactUtils.getContacts(param);
			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {
			progressBar.setVisibility(View.GONE);
			if (mContactInfos != null) {
				Toast.makeText(context,
						"获取系统联系人完毕" + mContactInfos.size() + "个",
						Toast.LENGTH_SHORT).show();
			}

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
