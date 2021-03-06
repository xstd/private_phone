package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.pirvatephone.utils.GetModelUtils;
import com.xstd.privatephone.adapter.MyModelAdapter;
import com.xstd.privatephone.tools.Tools;


public class ContextModelActivity extends BaseActivity {

	private ListView model_lv;
	private RelativeLayout add_btn;
	private RelativeLayout btn_callforwarding;
	private MyModelAdapter modelAdapter;
	private ModelBroadcastReciver recevier;
	private IntentFilter intentFilter;
	private ArrayList<Model> models;
	private GetModelUtils modeUtils;
	private TextView tv_title;
	private RelativeLayout btn_back;
	private Button btn_edit;
	private TextView tv_empty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_context_model);

		initView();

		showModel();

		recevier = new ModelBroadcastReciver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("ModelBroadcastReciver");
		this.registerReceiver(recevier, intentFilter);

	}

	private void showModel() {
		// 查询情景模式表。若不存在表，创建该表，存在则查询数据，展示数据

		modeUtils = new GetModelUtils(this);
		models = modeUtils.getModels();
		Tools.logSh(models.size() + ":::");
		if (models != null && models.size() > 0) {
			tv_empty.setVisibility(View.GONE);
		} else {
			tv_empty.setVisibility(View.VISIBLE);
		}

		modelAdapter = new MyModelAdapter(ContextModelActivity.this, models);
		model_lv.setAdapter(modelAdapter);
		model_lv.setEmptyView(tv_empty);
		model_lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tv_modelname = (TextView) view
						.findViewById(R.id.tv_modelname);
				String modelName = tv_modelname.getText().toString();
				showEditDialog(modelName);
				return false;
			}
		});
		model_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tv_modelname = (TextView) view
						.findViewById(R.id.tv_modelname);
				String modelName = tv_modelname.getText().toString();
				Intent intent = new Intent(ContextModelActivity.this,
						ModelEditActivity.class);
				intent.putExtra("ModelName", modelName);
				startActivity(intent);

			}

		});
	}

	private void initView() {

		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_back = (RelativeLayout) findViewById(R.id.btn_back);
		btn_edit = (Button) findViewById(R.id.btn_edit);
		tv_empty = (TextView) findViewById(R.id.tv_empty);
		btn_edit.setVisibility(View.GONE);

		model_lv = (ListView) findViewById(R.id.model_lv);
		add_btn = (RelativeLayout) findViewById(R.id.btn_add);
		btn_callforwarding = (RelativeLayout) findViewById(R.id.btn_callforwarding);

		tv_title.setText("情景模式");

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		add_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ContextModelActivity.this,
						NewContextModelActivity.class);
				startActivity(intent);
			}
		});

		btn_callforwarding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ContextModelActivity.this,
						CallForwardingActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.context_model, menu);
		return true;
	}

	@Override
	protected void onResume() {
		models.clear();
		models = modeUtils.getModels();
		modelAdapter.notifyDataSetChanged();
		super.onResume();
	}

	private void showEditDialog(final String modelName) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_edit, null);
		final LinearLayout ll_rename = (LinearLayout) view
				.findViewById(R.id.ll_rename);
		final LinearLayout ll_delete = (LinearLayout) view
				.findViewById(R.id.ll_delete);
		ll_rename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				showRenameDialog(modelName);
			}
		});

		ll_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				showDeleteDialog(modelName);
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void showRenameDialog(final String modelName) {

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_rename, null);
		final EditText et_model_name = (EditText) view
				.findViewById(R.id.et_model_name);
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);

		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String change_model = et_model_name.getText().toString().trim();
				if (TextUtils.isEmpty(modelName)) {
					Toast.makeText(ContextModelActivity.this, "情景模式名称不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					updateModel(modelName, change_model);
					updateModelDetail(modelName, change_model);
					dialog.dismiss();
				}

			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	protected void showDeleteDialog(final String modelName) {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.dialog_model_sure_delete, null);
		RelativeLayout rl_sure = (RelativeLayout) view
				.findViewById(R.id.rl_sure);
		RelativeLayout rl_cancel = (RelativeLayout) view
				.findViewById(R.id.rl_cancel);
		
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteModel(modelName);
				deleteModelDetail(modelName);
				dialog.dismiss();
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
	}

	private void updateModelDetail(final String oldModel, String newModel) {
		ModelDetailDao modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(ContextModelActivity.this);
		SQLiteDatabase modelDetailDatebase = modelDetailDao.getDatabase();
		Cursor modelDetailQuery = modelDetailDatebase.query(
				ModelDetailDao.TABLENAME, null, null, null, null, null, null);
		if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {
			while (modelDetailQuery.moveToNext()) {
				ModelDetail modelDetail = new ModelDetail();
				Long _id = modelDetailQuery
						.getLong(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Id.columnName));
				String jsonMassage = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));
				String address = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
				String name = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Name.columnName));
				Tools.logSh("修改前jsonMassage=" + jsonMassage);
				try {
					JSONObject json = new JSONObject(jsonMassage);
					Object object = json.get(oldModel);

					if (object != null) {

						String value = json.getString(oldModel);

						json.remove(oldModel);
						json.put(newModel, value);
						jsonMassage = json.toString();
						Tools.logSh("修改后jsonMassage=" + jsonMassage);
					} else {

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				modelDetail.setId(_id);
				modelDetail.setAddress(address);
				modelDetail.setName(name);
				modelDetail.setMassage(jsonMassage);
				Tools.logSh("修改了：：" + address + ":::" + oldModel + "___-to____"
						+ newModel);
				modelDetailDao.update(modelDetail);
			}
			modelDetailQuery.close();
		}
	}

	public void updateModel(String oldModel, String newModel) {

		// 查询系统数据库，获取当前的情景模式状态
		ModelDao modelDao = ModelDaoUtils
				.getModelDao(ContextModelActivity.this);
		SQLiteDatabase modelDaoDatabase = modelDao.getDatabase();
		Cursor modelQuery = modelDaoDatabase.query(ModelDao.TABLENAME, null,
				ModelDao.Properties.Model_name.columnName + "=?",
				new String[] { oldModel }, null, null, null);

		if (modelQuery != null && modelQuery.getCount() > 0) {
			while (modelQuery.moveToNext()) {
				Long _id = modelQuery.getLong(modelQuery
						.getColumnIndex(ModelDao.Properties.Id.columnName));
				int type = modelQuery
						.getInt(modelQuery
								.getColumnIndex(ModelDao.Properties.Model_type.columnName));

				Model model = new Model();
				model.setId(_id);
				model.setModel_name(newModel);
				model.setModel_type(type);

				Tools.logSh("modelName=" + newModel + ":::_id" + _id
						+ ":::type" + type);
				modelDao.update(model);
			}
			modelQuery.close();
		}
		models.clear();
		models = modeUtils.getModels();
		modelAdapter.notifyDataSetChanged();
	}

	/*
	 * 删除model表中的相关信息
	 */
	public void deleteModel(String modelName) {
		ModelDao modelDao = ModelDaoUtils
				.getModelDao(ContextModelActivity.this);
		SQLiteDatabase modelDaoDatabase = modelDao.getDatabase();
		int delete = modelDaoDatabase.delete(ModelDao.TABLENAME,
				ModelDao.Properties.Model_name.columnName + "=?",
				new String[] { modelName });
		if (delete > 0) {
			Toast.makeText(ContextModelActivity.this, "删除了一个情景模式",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ContextModelActivity.this, "删除情景模式失败",
					Toast.LENGTH_SHORT).show();
		}
		models.clear();
		models = modeUtils.getModels();
		modelAdapter.notifyDataSetChanged();

		if (models != null && models.size() > 0) {
			tv_empty.setVisibility(View.GONE);
		} else {
			tv_empty.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * 删除modelDetail表中的信息
	 */
	public void deleteModelDetail(String modelName) {
		ModelDetailDao modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(ContextModelActivity.this);
		SQLiteDatabase modelDetailDatebase = modelDetailDao.getDatabase();
		Cursor modelDetailQuery = modelDetailDatebase.query(
				ModelDetailDao.TABLENAME, null, null, null, null, null, null);
		if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {
			while (modelDetailQuery.moveToNext()) {

				ModelDetail modelDetail = new ModelDetail();
				Long _id = modelDetailQuery.getLong(modelDetailQuery
						.getColumnIndex(ModelDao.Properties.Id.columnName));
				String jsonMassage = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));
				String address = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
				String name = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Name.columnName));
				try {
					JSONObject json = new JSONObject(jsonMassage);
					Object object = json.get(modelName);
					if (object != null) {
						Tools.logSh("移除了：：" + address + ":::" + modelName);
						json.remove(modelName);
						jsonMassage = json.toString();
					} else {

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				modelDetail.setId(_id);
				modelDetail.setAddress(address);
				modelDetail.setName(name);
				modelDetail.setMassage(jsonMassage);
				modelDetailDao.update(modelDetail);
			}
			modelDetailQuery.close();
		}
	}

	private class ModelBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			models.clear();
			models = modeUtils.getModels();
			modelAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(recevier);
		super.onDestroy();
	}

}
