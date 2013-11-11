package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IntereptActivity extends BaseActivity {

	private String modelName;
	private ListView mListView;

	private Cursor contactCursor;
	private ModelDetailDao modelDetailDao;
	private EditContactAdapter mContactAdapter;
	private Button btn_cancle;
	private Button btn_sure;
	private TextView tv_title;
	private RelativeLayout select_all;
	private CheckBox btn_check_all;
	private CheckBox btn_remove_record;
	private RelativeLayout rl_remove_record;
	private boolean delete = false;
	private int type = 1;

	private ArrayList<String> selectContactsNumbers = new ArrayList<String>();
	private ArrayList<String> selectContactsNames = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interept);

		modelName = getIntent().getStringExtra("ModelName");

		initData();

		initView();

		setData();
	}

	private void initView() {
		//title
		tv_title = (TextView) findViewById(R.id.tv_title);
		select_all = (RelativeLayout) findViewById(R.id.rl_add_all);
		btn_check_all = (CheckBox) findViewById(R.id.btn_check_all);
		tv_title.setText(modelName + ":新增加拦截联系人");

		// bottm
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);

		// content
		mListView = (ListView) findViewById(R.id.listview);
		btn_remove_record = (CheckBox) findViewById(R.id.btn_remove_record);
		rl_remove_record = (RelativeLayout) findViewById(R.id.rl_remove_record);
		
		rl_remove_record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btn_remove_record.setChecked(!btn_remove_record.isChecked());
				if(btn_remove_record.isChecked()){
					delete = true; 
				}else{
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
				finish();
			}
		});

		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String clazzName = getCallingActivity().getShortClassName()
						.toString();
				//from edit page
				if (".activity.ModelEditActivity".equals(clazzName)) {
					ContextModelUtils.saveModelDetail(IntereptActivity.this,
							modelName, selectContactsNames,
							selectContactsNumbers, type, delete);
					
					
				} else {//from create context model page
					
					Toast.makeText(IntereptActivity.this,
							"callingActivity！NewContextModelActivity",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("Type", type);
					intent.putStringArrayListExtra("SelectContactsNumbers",
							selectContactsNumbers);
					intent.putStringArrayListExtra("SelectContactsNames",
							selectContactsNames);
					intent.putExtra("delete", delete);
					setResult(2, intent);
				}
				finish();
			}
		});
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

		} else {
			Toast.makeText(IntereptActivity.this, "还没有隐私联系人，请先添加隐私联系人！",
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
				.getModelDetailDao(IntereptActivity.this);
		// 获取私密联系人
		getContact();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_interept, menu);
		return true;
	}

}
