package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.DelectOurContactUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneRecordsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurSmsRecordsUtils;
import com.xstd.pirvatephone.utils.DelectSystemPhoneUtils;
import com.xstd.pirvatephone.utils.DelectSystemSmsUtils;
import com.xstd.pirvatephone.utils.RecordToSysUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.pirvatephone.utils.RestoreSystemPhoneUtils;
import com.xstd.pirvatephone.utils.RestoreSystemSmsUtils;
import com.xstd.pirvatephone.utils.WritePhoneDetailUtils;
import com.xstd.pirvatephone.utils.WritePhoneRecordUtils;
import com.xstd.pirvatephone.utils.WriteSmsDetailUtils;
import com.xstd.pirvatephone.utils.WriteSmsRecordUtils;
import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewContextModelActivity extends BaseActivity {

	private EditText model_name;
	private Button add_notinterept;
	private Button add_interept;
	private Button btn_cancle;
	private Button btn_sure;
	private ModelDao modelDao;
	private String modelName;
	private int type=1;
	private ArrayList<String> selectContactsNumbers;
	private ArrayList<String> selectContactsNames;
	private String[] selectPhones;
	private boolean delete = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_context_model);

		initView();

	}

	private void initView() {

		// add_name
		model_name = (EditText) findViewById(R.id.et_model_name);

		// content
		add_notinterept = (Button) findViewById(R.id.btn_add_notinterept);
		add_interept = (Button) findViewById(R.id.btn_add_interept);
		// bottom
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);

		add_notinterept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				modelName = model_name.getText().toString().trim();
				boolean b = hasModel(modelName);
				if (b) {

					// 设置该情景模式的不拦截联系人
					if (!TextUtils.isEmpty(modelName)) {
						Intent intent = new Intent(
								NewContextModelActivity.this,
								NotIntereptActivity.class);
						intent.putExtra("ModelName", modelName);
						// startActivity(intent);
						startActivityForResult(intent, 2);
					} else {
						Toast.makeText(NewContextModelActivity.this,
								"情景模式名称不能为空", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(NewContextModelActivity.this,
							"已有该情景模式，请从新定义！", Toast.LENGTH_SHORT).show();
				}

			}
		});

		add_interept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				modelName = model_name.getText().toString().trim();

				boolean b = hasModel(modelName);
				if (b) {

					// 设置该情景模式的不拦截联系人
					if (!TextUtils.isEmpty(modelName)) {
						Intent intent = new Intent(
								NewContextModelActivity.this,
								IntereptActivity.class);

						intent.putExtra("ModelName", modelName);
						startActivityForResult(intent, 2);
					} else {
						Toast.makeText(NewContextModelActivity.this,
								"情景模式名称不能为空", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(NewContextModelActivity.this,
							"已有该情景模式，请从新定义！", Toast.LENGTH_SHORT).show();
				}
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
				modelName = model_name.getText().toString().trim();
				if (!TextUtils.isEmpty(modelName)) {
					// 判断数据库中是否已有该情景模式
					boolean b = hasModel(modelName);

					if (b) {
						// 还没有该情景模式。增加一种情景模式
						createNewModel(modelName);
						finish();
						if(type==2){//拦截
							if (delete) {// 转移指定号码通信记录
								RecordToUsUtils recordToUsUtils = new RecordToUsUtils(NewContextModelActivity.this);
								recordToUsUtils.removeContactRecord(selectContactsNumbers, delete);
								
								Tools.logSh("selectContactsNumbers=="+selectContactsNumbers);
								Toast.makeText(NewContextModelActivity.this, "正在移动", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(NewContextModelActivity.this, "没有需要移动的1", Toast.LENGTH_SHORT).show();
							}
						}else{//不拦截
							if (delete){
								RecordToSysUtils recordToSysUtils = new RecordToSysUtils(NewContextModelActivity.this);
								recordToSysUtils.restoreContact(selectContactsNumbers);
							
							}else{
								Toast.makeText(NewContextModelActivity.this, "没有需要移动的2", Toast.LENGTH_SHORT).show();
							}
						}

					} else {
						Toast.makeText(NewContextModelActivity.this,
								"已有该情景模式，请从新定义！", Toast.LENGTH_SHORT).show();
					}

					// 更新UI
					Intent intent = new Intent();
					intent.setAction("ModelBroadcastReciver");
					sendBroadcast(intent);
				} else {
					Toast.makeText(NewContextModelActivity.this, "情景模式名称不能为空！",
							Toast.LENGTH_SHORT).show();

				}
			}
		});
	}

	private boolean hasModel(String modelName) {

		Tools.logSh("modelname=" + modelName);
		modelDao = ModelDaoUtils.getModelDao(NewContextModelActivity.this);
		SQLiteDatabase modelDatabase = modelDao.getDatabase();
		Cursor modelQuery = modelDatabase.query(ModelDao.TABLENAME, null,
				ModelDao.Properties.Model_name.columnName + "=?",
				new String[] { modelName }, null, null, null);
		if (modelQuery != null && modelQuery.getCount() > 0) {
			return false;
		} else {
			return true;
		}
	}

	private void createNewModel(String modelName) {
		Tools.logSh("createNewModel");
		// 创建情景模式
		Model model = new Model();
		model.setModel_name(modelName);
		model.setModel_type(0);
		modelDao.insert(model);

		if (selectContactsNumbers != null && selectContactsNumbers.size() > 0) {
			ContextModelUtils.saveModelDetail(this, modelName,
					selectContactsNames, selectContactsNumbers, type ,delete);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_context_model, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			type = data.getIntExtra("Type", 1);
			selectContactsNumbers = data
					.getStringArrayListExtra("SelectContactsNumbers");
			selectContactsNames = data
					.getStringArrayListExtra("SelectContactsNames");
			delete = data.getBooleanExtra("delete", false);

			Tools.logSh("type=" + type + ":::selectContactsNumbers="
					+ selectContactsNumbers);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
