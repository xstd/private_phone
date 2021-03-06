package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.contact.ContactInfo;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.GetModelUtils;
import com.xstd.privatephone.tools.Tools;

public class PrivateContactEditActivity extends BaseActivity {
	private Button bt_cancle;
	private Button bt_sure;
	private RelativeLayout btn_back;
	private Button btn_edit;
	private EditText et_name;
	private EditText et_phone;

	private ContactInfo contactInfo = new ContactInfo();
	private TextView tv_title;
	private RadioGroup myRadioGroup;
	private RadioButton myRadioButton1;
	private RadioButton myRadioButton2;
	private String display_Name;
	private String address;
	private int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_contact_edit);

		display_Name = getIntent().getStringExtra("Display_Name");
		address = getIntent().getStringExtra("Address");
		type = getIntent().getIntExtra("Type", 0);

		initView();

	}

	private void initView() {
		// title
		btn_back = (RelativeLayout) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("编辑隐私联系人");
		btn_edit.setVisibility(View.GONE);

		// content
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		// radioGroup
		myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
		myRadioButton1 = (RadioButton) findViewById(R.id.myRadioButton1);
		myRadioButton2 = (RadioButton) findViewById(R.id.myRadioButton2);
		if (type == 0) {
			myRadioGroup.check(R.id.myRadioButton1);
		} else {
			myRadioGroup.check(R.id.myRadioButton2);
		}

		et_name.setText(display_Name);
		et_phone.setText(address);

		// bottom
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_cancle = (Button) findViewById(R.id.bt_cancle);

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 若为空
				String name = et_name.getText().toString();
				String phone = et_phone.getText().toString();
				int intereptType;

				if (TextUtils.isEmpty(phone)) {
					Toast.makeText(getApplicationContext(), "请填写正确的电话",
							Toast.LENGTH_SHORT).show();
				} else {// not null,add to our db.
					ContactInfoDao contactInfoDao = ContactInfoDaoUtils
							.getContactInfoDao(getApplicationContext());
					SQLiteDatabase contactDatabase = contactInfoDao
							.getDatabase();
					// get address---id

					if (name == null) {
						name = phone;
					}
					contactInfo.setPhone_number(phone);
					contactInfo.setDisplay_name(name);

					contactInfo.setType(type);
					// type
					if (myRadioButton1.isChecked()) {
						Tools.logSh("type===" + 0);
						intereptType = 0;
						contactInfo.setType(intereptType);
					} else {
						Tools.logSh("type===" + 1);
						intereptType = 1;
						contactInfo.setType(intereptType);
					}
					// 判断改动--1.如果号码没变，直接跟新，2.号码改变，判断号码是否存在
					if (address.equals(phone)) {

						contactDatabase
								.delete(ContactInfoDao.TABLENAME,
										ContactInfoDao.Properties.Phone_number.columnName
												+ "=?", new String[] { phone });
						contactInfoDao.insert(contactInfo);
						Toast.makeText(getApplicationContext(),
								"想联系人数据库中添加了一条新数据", Toast.LENGTH_SHORT).show();

						// update current model phonenumber interept model.
						if (intereptType != type) {
							updateModel(phone, intereptType);
						}

						finish();
					} else {
						Cursor query = contactDatabase
								.query(ContactInfoDao.TABLENAME,
										null,
										ContactInfoDao.Properties.Phone_number.columnName
												+ "=?", new String[] { phone },
										null, null, null);
						if (query != null && query.getCount() > 0) {
							// 已存在,
							Toast.makeText(PrivateContactEditActivity.this,
									"该号码已经存在", Toast.LENGTH_SHORT).show();
						} else {
							// 删除以前的---需要删除当前情景模式中的该号码
							contactDatabase
									.delete(ContactInfoDao.TABLENAME,
											ContactInfoDao.Properties.Phone_number.columnName
													+ "=?",
											new String[] { address });
							ContextModelUtils.deleteModelDetail(
									PrivateContactEditActivity.this,
									new String[] { address });

							// 插入新的
							contactInfoDao.insert(contactInfo);
							Toast.makeText(getApplicationContext(),
									"想联系人数据库中添加了一条新数据", Toast.LENGTH_SHORT)
									.show();
							// 返回到联系人界面
							if (intereptType != type) {
								updateModel(phone, intereptType);
							}

							// update current model phonenumber interept model.
							finish();
						}
					}
				}
			}
		});

		bt_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void updateModel(String number, int type) {
		// 1.
		ContextModelUtils contextModelUtils = new ContextModelUtils();
		ArrayList<String> intereptNumbers = contextModelUtils
				.getIntereptNumbers(PrivateContactEditActivity.this, null);
		ArrayList<String> notIntereptNumbers = contextModelUtils
				.getNotIntereptNumbers(PrivateContactEditActivity.this, null);
		// update
		// 0 --- normal ,1 ---- interept
		GetModelUtils getModelUtils = new GetModelUtils(
				PrivateContactEditActivity.this);
		String currentModel = getModelUtils.getCurrentModel();

		if (intereptNumbers.contains(number) && type == 0) {
			getModelUtils.updateModel(number, currentModel, type);

		}

		if (notIntereptNumbers.contains(number) && type == 1) {
			getModelUtils.updateModel(number, currentModel, type);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hand_input, menu);
		return true;
	}
}
