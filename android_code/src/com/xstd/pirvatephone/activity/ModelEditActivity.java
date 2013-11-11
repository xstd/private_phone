package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.R.layout;
import com.xstd.pirvatephone.R.menu;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.utils.ArrayUtils;
import com.xstd.pirvatephone.utils.ContextModelUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneRecordsUtils;
import com.xstd.privatephone.adapter.EditModelAdapter;
import com.xstd.privatephone.bean.ModelJson;
import com.xstd.privatephone.tools.Tools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ModelEditActivity extends BaseActivity {

	private int tag = 0;
	private TextView tv_uninterept;
	private TextView tv_interept;
	private LinearLayout ll_interept;
	private ListView lv_interept;
	private LinearLayout ll_uninterept;
	private ListView lv_uninterept;
	private String modelName;

	private ArrayList<String> intereptNumbers =new ArrayList<String>();
	private ArrayList<String> intereptNames =new ArrayList<String>();

	private ArrayList<String> noIntereptNumbers =new ArrayList<String>();
	private ArrayList<String> noIntereptNames =new ArrayList<String>();
	private Button add_btn;
	private int curIndex = 0;
	private EditModelAdapter noIntereptAdapter;
	private EditModelAdapter intereptAdapter;

	private static final int UPDATE = 0;
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE:

				if(tag==1){
					setData();
				}else{
					switch (curIndex) {
					case 0:
						showNoInterept();
						break;

					case 1:
						showInterept();
						break;

					default:
						break;
					}
				}
				Tools.logSh("intereptNumbers===" + intereptNumbers);
				Tools.logSh("intereptNames===" + intereptNames);
				Tools.logSh("noIntereptNumbers===" + noIntereptNumbers);
				Tools.logSh("noIntereptNames===" + noIntereptNames);

				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_model_edit);

		modelName = getIntent().getStringExtra("ModelName");
		initView();

	}

	/**
	 * 获取拦截与不拦截的信息
	 */
	private void setData() {
		if (tag == 1) {
			showNoInterept();
		}

		tv_interept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_uninterept
						.setBackgroundResource(R.drawable.tab_left_default);
				tv_interept.setBackgroundResource(R.drawable.tab_right_pressed);

				showInterept();
			}
		});

		tv_uninterept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_uninterept
						.setBackgroundResource(R.drawable.tab_left_pressed);
				tv_interept.setBackgroundResource(R.drawable.tab_right_default);

				showNoInterept();

			}
		});

		add_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (curIndex == 0) {
					Intent intent = new Intent(ModelEditActivity.this,
							NotIntereptActivity.class);
					intent.putExtra("ModelName", modelName);
					intent.putExtra("type", 0);
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(ModelEditActivity.this,
							IntereptActivity.class);
					intent.putExtra("ModelName", modelName);
					intent.putExtra("type", 1);
					startActivityForResult(intent, 1);
				}

			}
		});
	}

	private void initView() {
		tv_uninterept = (TextView) findViewById(R.id.tv_uninterept);
		tv_interept = (TextView) findViewById(R.id.tv_interept);

		// 拦截
		ll_interept = (LinearLayout) findViewById(R.id.ll_interept);
		lv_interept = (ListView) findViewById(R.id.lv_interept);

		// 不拦截
		ll_uninterept = (LinearLayout) findViewById(R.id.ll_uninterept);
		lv_uninterept = (ListView) findViewById(R.id.lv_uninterept);

		add_btn = (Button) findViewById(R.id.add_btn);

	}

	/**
	 * 显示拦截
	 */
	private void showInterept() {
		curIndex = 1;
		ll_interept.setVisibility(View.VISIBLE);
		ll_uninterept.setVisibility(View.GONE);
		Tools.logSh("intereptNumbers=" + intereptNumbers + ":::"
				+ "intereptNames=" + intereptNames);
		intereptAdapter = new EditModelAdapter(ModelEditActivity.this,
				intereptNumbers, intereptNames);
		lv_interept.setAdapter(intereptAdapter);
		lv_interept.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
				checkbox.setChecked(!checkbox.isChecked());
			}
		});
	}

	/**
	 * 显示不拦截
	 */
	private void showNoInterept() {

		curIndex = 0;
		ll_interept.setVisibility(View.GONE);
		ll_uninterept.setVisibility(View.VISIBLE);
		noIntereptAdapter = new EditModelAdapter(ModelEditActivity.this,
				noIntereptNumbers, noIntereptNames);
		lv_uninterept.setAdapter(noIntereptAdapter);
		lv_uninterept.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tools.logSh("条目被点击了");
				CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
				checkbox.setChecked(!checkbox.isChecked());
			}
		});

		lv_uninterept.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				Tools.logSh("不拦截item长按了");
				// 获取该号码
				TextView tv_phone_num = (TextView) v
						.findViewById(R.id.tv_phone_num);
				String number = tv_phone_num.getText().toString();

				showDeleteDialog(modelName, number);
				return false;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.model_edit, menu);
		return true;
	}

	protected void showDeleteDialog(final String modelName, final String address) {
		AlertDialog.Builder builder = new Builder(ModelEditActivity.this);

		builder.setMessage("删除此情景模式？");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Tools.logSh("选择了确认按钮，删除了情景模式");
				deleteModelNumber(modelName, address);
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

	private void deleteModelNumber(String model, String number) {
		ModelDetailDao modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(ModelEditActivity.this);
		SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
		Cursor modelDetailQuery = modelDetailDatabase.query(
				ModelDetailDao.TABLENAME, null,
				ModelDetailDao.Properties.Address.columnName + "=?",
				new String[] { number }, null, null, null);

		if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {
			while (modelDetailQuery.moveToNext()) {
				Long _id = modelDetailQuery
						.getLong(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Id.columnName));
				String address = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
				String name = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Name.columnName));

				String message = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));

				ModelDetail modelDetail = new ModelDetail();
				modelDetail.setId(_id);
				modelDetail.setAddress(address);
				modelDetail.setName(name);

				try {
					JSONObject json = new JSONObject(message);
					json.remove(model);
					message = json.toString();
					modelDetail.setMassage(message);
					Tools.logSh("message====" + message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				modelDetailDao.update(modelDetail);

			}
		}
	}

	private void clearAll() {
		noIntereptNumbers.clear();
		noIntereptNames.clear();
		intereptNumbers.clear();
		intereptNames.clear();
	}

	@Override
	protected void onResume() {
		tag++;
		GetInfoTask task = new GetInfoTask(ModelEditActivity.this);
		task.execute();

		super.onResume();
	}

	private class GetInfoTask extends AsyncTask<Void, Integer, Integer> {

		private Context mContext;

		public GetInfoTask(Context context) {
			this.mContext = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		public void onPreExecute() {
			Toast.makeText(mContext, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		public void onPostExecute(Integer integer) {

			ContextModelUtils contextModelUtils = new ContextModelUtils();

			intereptNumbers = contextModelUtils.getIntereptNumbers(
					ModelEditActivity.this, modelName);
			intereptNames = contextModelUtils.getIntereptNames(
					ModelEditActivity.this, modelName);

			noIntereptNumbers = contextModelUtils.getNotIntereptNumbers(
					ModelEditActivity.this, modelName);
			noIntereptNames = contextModelUtils.getNotIntereptNames(
					ModelEditActivity.this, modelName);

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
