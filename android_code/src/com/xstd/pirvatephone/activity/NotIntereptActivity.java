package com.xstd.pirvatephone.activity;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.privatephone.adapter.ContactAdapter;
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.bean.ModelJson;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
	private Button btn_cancle;
	private Button btn_sure;
	private String modelName;
	private Cursor contactCursor;
	private EditContactAdapter mContactAdapter;
	private boolean delete = false;
	private int type = 1;

	private ArrayList<String> selectContactsNumbers = new ArrayList<String>();
	private ArrayList<String> selectContactsNames = new ArrayList<String>();
	private ModelDetailDao modelDetailDao;
	private RelativeLayout rl_remove_record;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_interept);

		modelName = getIntent().getStringExtra("ModelName");

		initData();

		initView();

		setData();
	}

	private void setData() {

		if (contactCursor != null && contactCursor.getCount() > 0) {
			mContactAdapter = new EditContactAdapter(getApplicationContext(),
					contactCursor);
			mListView.setAdapter(mContactAdapter);

			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					CheckBox checkbox = (CheckBox) view
							.findViewById(R.id.checkbox);
					TextView tv_phone_num = (TextView) view
							.findViewById(R.id.tv_phone_num);
					TextView tv_name = (TextView) view
							.findViewById(R.id.tv_name);

					String number = tv_phone_num.getText().toString();
					String name = tv_name.getText().toString();

					checkbox.setChecked(!checkbox.isChecked());

					if (checkbox.isChecked()) {
						selectContactsNumbers.add(number);
						selectContactsNames.add(name);
					} else {
						selectContactsNumbers.remove(number);
						selectContactsNames.remove(name);
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
		} else {
			Toast.makeText(NotIntereptActivity.this, "还没有隐私联系人，请先添加隐私联系人！",
					Toast.LENGTH_SHORT).show();
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

	private void initData() {

		modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(NotIntereptActivity.this);
		// 获取私密联系人
		getContact();

	}

	private void initView() {
		// title
		tv_title = (TextView) findViewById(R.id.tv_title);

		// content
		select_all = (RelativeLayout) findViewById(R.id.rl_add_all);
		btn_check_all = (CheckBox) findViewById(R.id.btn_check_all);

		mListView = (ListView) findViewById(R.id.listview);

		btn_remove_record = (CheckBox) findViewById(R.id.btn_remove_record);
		rl_remove_record = (RelativeLayout) findViewById(R.id.rl_remove_record);
		// bottom
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);

		tv_title.setText(modelName + ":新增不拦截联系人");

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
								.findViewById(R.id.checkbox);
						TextView tv_phone_num = (TextView) layout
								.findViewById(R.id.tv_phone_num);
						TextView tv_name = (TextView) layout
								.findViewById(R.id.tv_name);

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
					setResult(1, intent);
				}
				finish();
			}
		});
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

	private void updateUI() {
		initData();
		mContactAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_interept, menu);
		return true;
	}

}
