package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.RecordToSysUtils;
import com.xstd.pirvatephone.utils.RecordToUsUtils;
import com.xstd.privatephone.adapter.NewContextModelAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewContextModelActivity extends BaseActivity {

	private EditText model_name;
	private Button btn_sure;
	private ModelDao modelDao;
	private String modelName;

	/**
	 * type==0,不拦截，type==1,拦截。
	 */
	private int type = 0;
	private ArrayList<String> intereptNumbers;
	private ArrayList<String> notIntereptNumbers;
	private ArrayList<String> intereptNames;
	private ArrayList<String> notIntereptNames;
	private boolean delete = false;
	private Button btn_back;
	private Button btn_edit;
	private TextView tv_title;
	private Button btn_cancel;

	private RelativeLayout rl_intercept;
	private RelativeLayout rl_not_intercept;
	private RelativeLayout add_notinterept;
	private RelativeLayout add_interept;

	public static final int UPDATE = 0;

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE:
				if (type == 0) {
					listview.setEmptyView(emptyview);
					listview.setAdapter(new NewContextModelAdapter(
							NewContextModelActivity.this, notIntereptNumbers,
							notIntereptNames));
				} 
				if(type == 1){
					listview.setEmptyView(emptyview);
					listview.setAdapter(new NewContextModelAdapter(
							NewContextModelActivity.this, intereptNumbers,
							intereptNames));
				}

				break;
			}
		};
	};
	private ListView listview;
	private TextView emptyview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_context_model);

		initView();

	}

	private void initView() {

		// title
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_sure = (Button) findViewById(R.id.btn_create);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_cancel.setVisibility(View.VISIBLE);
		btn_sure.setVisibility(View.VISIBLE);
		btn_edit.setVisibility(View.GONE);
		btn_back.setVisibility(View.GONE);
		tv_title.setText("新建情景模式");

		// add_name
		model_name = (EditText) findViewById(R.id.et_model_name);
		rl_intercept = (RelativeLayout) findViewById(R.id.rl_intercept);
		rl_not_intercept = (RelativeLayout) findViewById(R.id.rl_not_intercept);
		listview = (ListView) findViewById(R.id.listview);
		emptyview = (TextView) findViewById(R.id.tv_emptyview);

		// bottom
		add_interept = (RelativeLayout) findViewById(R.id.btn_add_interept);
		add_notinterept = (RelativeLayout) findViewById(R.id.btn_add_not_interept);

		rl_intercept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type = 1;
				add_interept.setVisibility(View.VISIBLE);
				add_notinterept.setVisibility(View.GONE);
				listview.setEmptyView(emptyview);
				listview.setAdapter(new NewContextModelAdapter(
						NewContextModelActivity.this, intereptNumbers,
						intereptNames));
			}
		});

		rl_not_intercept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				type = 0;
				add_interept.setVisibility(View.GONE);
				add_notinterept.setVisibility(View.VISIBLE);
				listview.setEmptyView(emptyview);
				listview.setAdapter(new NewContextModelAdapter(
						NewContextModelActivity.this, notIntereptNumbers,
						notIntereptNames));
			}
		});

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
						startActivityForResult(intent, 0);
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

					// 设置该情景模式的拦截联系人
					if (!TextUtils.isEmpty(modelName)) {
						Intent intent = new Intent(
								NewContextModelActivity.this,
								IntereptActivity.class);

						intent.putExtra("ModelName", modelName);
						startActivityForResult(intent, 1);
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

		btn_cancel.setOnClickListener(new OnClickListener() {

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
						if (type == 1) {// 拦截
							if (delete) {// 转移指定号码通信记录
								RecordToUsUtils recordToUsUtils = new RecordToUsUtils(
										NewContextModelActivity.this);
								recordToUsUtils.removeContactRecord(
										intereptNumbers, delete);

								Tools.logSh("selectContactsNumbers=="
										+ intereptNumbers);
								Toast.makeText(NewContextModelActivity.this,
										"正在移动", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(NewContextModelActivity.this,
										"没有需要移动的1", Toast.LENGTH_SHORT).show();
							}
						} else {// 不拦截
							if (delete) {
								RecordToSysUtils recordToSysUtils = new RecordToSysUtils(
										NewContextModelActivity.this);
								recordToSysUtils.restoreContact(
										notIntereptNumbers, true);

							} else {
								Toast.makeText(NewContextModelActivity.this,
										"没有需要移动的2", Toast.LENGTH_SHORT).show();
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

		if (intereptNumbers != null && intereptNumbers.size() > 0) {
			ContextModelUtils.saveModelDetail(this, modelName,
					intereptNames, intereptNumbers, 1, delete);
		}
		
		if (notIntereptNumbers != null && notIntereptNumbers.size() > 0) {
			ContextModelUtils.saveModelDetail(this, modelName,
					notIntereptNames, notIntereptNumbers, 0, delete);
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
		if (data != null) {
			type = data.getIntExtra("Type", 1);
			if (type == 0) {
				notIntereptNumbers = data
						.getStringArrayListExtra("SelectContactsNumbers");
				notIntereptNames = data
						.getStringArrayListExtra("SelectContactsNames");
				
				for (int i = 0; i < notIntereptNumbers.size(); i++) {
					if(intereptNumbers!=null && intereptNumbers.contains(notIntereptNumbers.get(i))){
						intereptNumbers.remove(notIntereptNumbers.get(i));
						intereptNames.remove(notIntereptNames.get(i));
					}
				}
			} else {
				intereptNumbers = data
						.getStringArrayListExtra("SelectContactsNumbers");
				intereptNames = data
						.getStringArrayListExtra("SelectContactsNames");
				
				for (int i = 0; i < intereptNumbers.size(); i++) {
					if(notIntereptNumbers!=null && notIntereptNumbers.contains(intereptNumbers.get(i))){
						notIntereptNumbers.remove(intereptNumbers.get(i));
						notIntereptNames.remove(intereptNames.get(i));
					}
				}
			}

			delete = data.getBooleanExtra("delete", false);

			Tools.logSh("type=" + type + ":::selectContactsNumbers="
					+ intereptNumbers);

			Message msg = new Message();
			msg.what = UPDATE;
			handler.sendMessage(msg);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
