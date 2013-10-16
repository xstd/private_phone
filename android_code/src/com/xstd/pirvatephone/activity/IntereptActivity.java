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
import com.xstd.privatephone.adapter.EditContactAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
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
	private Button btn_cancle;
	private Button btn_sure;

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
		// title
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(modelName + ":新增加拦截联系人");

		// bottm
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);

		// content
		mListView = (ListView) findViewById(R.id.listview);

		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//
				saveModel();
				finish();
			}
		});
	}

	private void saveModel() {

		for (int i = 0; i < selectContactsNumbers.size(); i++) {
			String number = selectContactsNumbers.get(i);
			// 查询该号码是否存在
			ModelDetailDao modelDetailDao = ModelDetailDaoUtils
					.getModelDetailDao(IntereptActivity.this);
			SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
			Cursor modelDetailQuery = modelDetailDatabase.query(
					ModelDetailDao.TABLENAME, null,
					ModelDetailDao.Properties.Address.columnName + "=?",
					new String[] { number }, null, null, null);
			
			if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {

				while (modelDetailQuery.moveToNext()) {
					String num = modelDetailQuery
							.getString(modelDetailQuery
									.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
					if (number.equals(num)) {// 已有该号码的相关信息，更新
						String jsonMassage = modelDetailQuery
								.getString(modelDetailQuery
										.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));

						Long _id = modelDetailQuery
								.getLong(modelDetailQuery
										.getColumnIndex(ModelDetailDao.Properties.Id.columnName));

						ModelDetail modelDetail = new ModelDetail();
						modelDetail.setId(_id);
						modelDetail.setAddress(number);
						modelDetail.setName(selectContactsNames.get(i));
						Tools.logSh("address" + number + "::" + "message=="
								+ jsonMassage);
						try {
							// {"home":1,"company":2}
							JSONObject json = new JSONObject(jsonMassage);
							json.put(modelName, 2);
							jsonMassage = json.toString();
						} catch (JSONException ex) {
							// 键为null或使用json不支持的数字格式(NaN, infinities)
							throw new RuntimeException(ex);
						}
						modelDetail.setMassage(jsonMassage);

						modelDetailDao.update(modelDetail);

					} else {// 未有该号码的相关信息，添加
						ModelDetail modelDetail = new ModelDetail();

						modelDetail.setAddress(number);
						String name = selectContactsNames.get(i);
						modelDetail.setName(name);

						// Json--
						String msg = "";
						try {
							// 首先最外层是{}，是创建一个对象
							JSONObject model = new JSONObject();

							// 1，不拦截；2，拦截
							model.put(modelName, 2);
							msg = model.toString();
							Tools.logSh("name=" + name + ":::" + "address"
									+ number + "::" + "message==" + msg);
							/*
							 * { "家里"："1","公司":"2" }
							 */

						} catch (JSONException ex) {
							// 键为null或使用json不支持的数字格式(NaN, infinities)
							throw new RuntimeException(ex);
						}

						modelDetail.setMassage(msg);
						Tools.logSh("向modelDetail添加了一条数据");
						modelDetailDao.insert(modelDetail);
					}

				}
				modelDetailQuery.close();

			} else {
				ModelDetail modelDetail = new ModelDetail();

				modelDetail.setAddress(number);
				String name = selectContactsNames.get(i);
				modelDetail.setName(name);

				// Json--
				String msg = "";
				try {
					// 首先最外层是{}，是创建一个对象
					JSONObject model = new JSONObject();

					// 1，不拦截；2，拦截
					model.put(modelName, 2);
					msg = model.toString();
					Tools.logSh("name=" + name + ":::" + "address" + number
							+ "::" + "message==" + msg);
					/*
					 * { "家里"："1","公司":"2" }
					 */

				} catch (JSONException ex) {
					// 键为null或使用json不支持的数字格式(NaN, infinities)
					throw new RuntimeException(ex);
				}

				modelDetail.setMassage(msg);
				Tools.logSh("向modelDetail添加了一条数据");
				modelDetailDao.insert(modelDetail);
			}
		}
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
