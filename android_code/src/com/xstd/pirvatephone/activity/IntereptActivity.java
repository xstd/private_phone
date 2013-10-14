package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.privatephone.adapter.EditContactAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IntereptActivity extends Activity {

	private String modelName;
	private ListView mListView;
	
	private ArrayList<String> selectContactsNumbers = new ArrayList<String>();
	private ArrayList<String> selectContactsNames = new ArrayList<String>();
	private Cursor contactCursor;
	private ModelDetailDao modelDetailDao;
	private EditContactAdapter mContactAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interept);
		
		modelName = getIntent().getStringExtra("ModelName");
		
		initData();
		
		initView();
		
		setData();
	}

	private void initView() {
		//title
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(modelName+":新增加拦截联系人");
		
		mListView = (ListView) findViewById(R.id.listview);
		
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
