package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.privatephone.adapter.AddIntereptAdapter;

import android.os.Bundle;
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
	private AddIntereptAdapter addIntereptAdapter;
	private RelativeLayout btn_cancle;
	private RelativeLayout btn_sure;
	private TextView tv_title;
	private RelativeLayout select_all;
	private CheckBox btn_check_all;
	private CheckBox btn_remove_record;
	private RelativeLayout rl_remove_record;
	private boolean delete = false;
	private int type = 1;

	private ArrayList<String> selectContactsNumbers = new ArrayList<String>();
	private ArrayList<String> selectContactsNames = new ArrayList<String>();
	private RelativeLayout btn_back;
	private Button btn_edit;
	private TextView tv_empty_view;

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
		tv_title.setText("新增拦截联系人");
		btn_back = (RelativeLayout) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_edit.setVisibility(View.GONE);
		
		tv_empty_view = (TextView) findViewById(R.id.tv_empty_view);
		
		select_all = (RelativeLayout) findViewById(R.id.rl_add_all);
		btn_check_all = (CheckBox) findViewById(R.id.btn_check_all);

		// bottm
		btn_cancle = (RelativeLayout) findViewById(R.id.btn_cancle);
		btn_sure = (RelativeLayout) findViewById(R.id.btn_sure);

		// content
		mListView = (ListView) findViewById(R.id.listview);
		btn_remove_record = (CheckBox) findViewById(R.id.btn_remove_record);
		rl_remove_record = (RelativeLayout) findViewById(R.id.rl_remove_record);
		
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
								.findViewById(R.id.interept_checkbox);
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
								.findViewById(R.id.interept_checkbox);
						TextView tv_phone_num = (TextView) layout
								.findViewById(R.id.interept_tv_phone_num);
						TextView tv_name = (TextView) layout
								.findViewById(R.id.interept_tv_name);

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
					setResult(1, intent);
				}
				finish();
			}
		});
	}

	private void setData() {

		if (contactCursor != null && contactCursor.getCount() > 0) {
			tv_empty_view.setVisibility(View.GONE);
		} else {
			tv_empty_view.setVisibility(View.VISIBLE);
		}
		
		addIntereptAdapter = new AddIntereptAdapter(getApplicationContext(),
				contactCursor);
		mListView.setAdapter(addIntereptAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CheckBox checkbox = (CheckBox) view
						.findViewById(R.id.interept_checkbox);
				TextView tv_phone_num = (TextView) view
						.findViewById(R.id.interept_tv_phone_num);
				TextView tv_name = (TextView) view
						.findViewById(R.id.interept_tv_name);

				String number = tv_phone_num.getText().toString();
				String name = tv_name.getText().toString();

				checkbox.setChecked(!checkbox.isChecked());

				if (checkbox.isChecked()) {
						selectContactsNumbers.add(number);
						selectContactsNames.add(name);
						if(contactCursor!=null && selectContactsNumbers.size()==contactCursor.getCount()){
							btn_check_all.setChecked(true);
						}
				} else {
					if(selectContactsNumbers.contains(number)){
						selectContactsNumbers.remove(number);
						selectContactsNames.remove(name);
						if(selectContactsNumbers.size()==0){
							btn_check_all.setChecked(false);
						}
					}
				}
			}
		});

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
		getContact();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_interept, menu);
		return true;
	}

}
