package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.GetContactUtils;
import com.xstd.privatephone.adapter.AddNotIntereptAdapter;
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NotIntereptActivity extends BaseActivity {

	private TextView tv_title;
	private RelativeLayout select_all;
	private CheckBox btn_check_all;
	private ListView mListView;
	private CheckBox btn_remove_record;
	private RelativeLayout btn_cancle;
	private RelativeLayout btn_sure;
	private String modelName;
	private Cursor contactCursor;
	private AddNotIntereptAdapter addNotIntereptAdapter;
	private boolean delete = false;
	private int type = 0;

	private ArrayList<String> selectContactsNumbers = new ArrayList<String>();
	private ArrayList<String> selectContactsNames = new ArrayList<String>();
	private RelativeLayout rl_remove_record;
	private RelativeLayout btn_back;
	private Button btn_edit;
	private RelativeLayout rl_empty;
	private RelativeLayout rl_content;

	private static final int UPDATE = 1;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE:
				setData();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_interept);

		modelName = getIntent().getStringExtra("ModelName");
		initView();
		GetPrivateContactTast contactTast = new GetPrivateContactTast(
				NotIntereptActivity.this);
		contactTast.execute();
	}

	private void initView() {
		// title
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("新增拦截联系人");
		btn_back = (RelativeLayout) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_edit.setVisibility(View.GONE);

		rl_empty = (RelativeLayout) findViewById(R.id.rl_empty);
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);

		// content
		select_all = (RelativeLayout) findViewById(R.id.rl_add_all);
		btn_check_all = (CheckBox) findViewById(R.id.btn_check_all);

		mListView = (ListView) findViewById(R.id.listview);

		btn_remove_record = (CheckBox) findViewById(R.id.btn_remove_record);
		rl_remove_record = (RelativeLayout) findViewById(R.id.rl_remove_record);
		// bottom
		btn_cancle = (RelativeLayout) findViewById(R.id.btn_cancle);
		btn_sure = (RelativeLayout) findViewById(R.id.btn_sure);

		tv_title.setText(":新增不拦截联系人");

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		rl_remove_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_remove_record.setChecked(!btn_remove_record.isChecked());
				if (btn_remove_record.isChecked()) {
					delete = true;
				} else {
					delete = false;
				}
			}
		});

		select_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btn_check_all.isChecked()) {
					// 反选所有的item
					for (int index = 0; index < mListView.getChildCount(); index++) {
						RelativeLayout layout = (RelativeLayout) mListView
								.getChildAt(index);
						CheckBox checkBox = (CheckBox) layout
								.findViewById(R.id.checkbox);
						checkBox.setChecked(false);

						// 清空选中号码
						selectContactsNumbers.clear();
						selectContactsNames.clear();
					}

				} else {
					// 全部选中所有的item
					for (int index = 0; index < mListView.getChildCount(); index++) {
						RelativeLayout layout = (RelativeLayout) mListView
								.getChildAt(index);
						CheckBox checkBox = (CheckBox) layout
								.findViewById(R.id.not_interept_checkbox);
						TextView tv_phone_num = (TextView) layout
								.findViewById(R.id.not_interept_tv_phone_num);
						TextView tv_name = (TextView) layout
								.findViewById(R.id.not_interept_tv_name);

						String number = tv_phone_num.getText().toString()
								.trim();
						String name = tv_name.getText().toString().trim();

						checkBox.setChecked(true);

						// 将所有号码添加到选中号码。
						selectContactsNumbers.add(number);
						selectContactsNames.add(name);

					}

				}
				btn_check_all.setChecked(!btn_check_all.isChecked());
			}
		});

		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将新添加的情景模式的详细信息存储到model_detail数据库
				finish();
			}
		});
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String clazzName = getCallingActivity().getShortClassName()
						.toString();
				Tools.logSh("className==" + clazzName);
				if (".activity.ModelEditActivity".equals(clazzName)) {
					ContextModelUtils.saveModelDetail(NotIntereptActivity.this,
							modelName, selectContactsNames,
							selectContactsNumbers, type, delete);
					// 发送指令：不拦截哪部分代码
					Tools.logSh("不拦截——————————————"
							+ selectContactsNames.size());

				} else {
					Toast.makeText(NotIntereptActivity.this,
							"callingActivity！NewContextModelActivity",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("Type", type);
					intent.putStringArrayListExtra("SelectContactsNumbers",
							selectContactsNumbers);
					intent.putStringArrayListExtra("SelectContactsNames",
							selectContactsNames);
					intent.putExtra("delete", delete);
					setResult(0, intent);
				}
				finish();
			}
		});
	}

	private void setData() {
		if (contactCursor != null && contactCursor.getCount() > 0) {
			rl_content.setVisibility(View.VISIBLE);
			rl_empty.setVisibility(View.GONE);
			addNotIntereptAdapter = new AddNotIntereptAdapter(
					getApplicationContext(), contactCursor);
			mListView.setAdapter(addNotIntereptAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.not_interept_checkbox);
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.not_interept_tv_phone_num);
					TextView tv_name = (TextView) view
							.findViewById(R.id.not_interept_tv_name);

					String number = tv_phone_num.getText().toString();
					String name = tv_name.getText().toString();

					checkbox.setChecked(!checkbox.isChecked());

					if (checkbox.isChecked()) {
						// 判断当前情景模式下是否已经不拦截该号码
						selectContactsNumbers.add(number);
						selectContactsNames.add(name);
					} else {
						if (selectContactsNumbers.contains(number)) {
							selectContactsNumbers.remove(number);
							selectContactsNames.remove(name);
						}
					}
				}

			});

			mListView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					TextView tv_phone_num = (TextView) v
							.findViewById(R.id.tv_phone_num);
					String address = tv_phone_num.getText().toString();
					showDeleteDialog(modelName, address);
					return false;
				}
			});
		}else{
			rl_content.setVisibility(View.GONE);
			rl_empty.setVisibility(View.VISIBLE);
		}
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

	protected void showDeleteDialog(final String modelName, final String address) {
		AlertDialog.Builder builder = new Builder(NotIntereptActivity.this);

		builder.setMessage("删除此情景模式？");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Tools.logSh("选择了确认按钮，删除了情景模式");
				// deleteModelNumber(modelName, address);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private class GetPrivateContactTast extends
			AsyncTask<Void, Integer, Integer> {

		private Context context;

		public GetPrivateContactTast(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_interept, menu);
		return true;
	}

}
