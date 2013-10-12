package com.xstd.pirvatephone.activity;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.utils.GetModelUtils;
import com.xstd.privatephone.adapter.MyModelAdapter;
import com.xstd.privatephone.tools.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContextModelActivity extends Activity {

	private ListView model_lv;
	private Button add_btn;
	private MyModelAdapter modelAdapter;
	private ModelBroadcastReciver recevier;
	private IntentFilter intentFilter;
	private ArrayList<Model> models;
	private GetModelUtils modeUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		modelAdapter = new MyModelAdapter(ContextModelActivity.this, models);
		model_lv.setAdapter(modelAdapter);

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
				Intent intent = new Intent(ContextModelActivity.this,
						ModelEditActivity.class);
				startActivity(intent);

			}

		});
	}

	private void initView() {
		model_lv = (ListView) findViewById(R.id.model_lv);
		add_btn = (Button) findViewById(R.id.add_btn);

		add_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ContextModelActivity.this,
						NewContextModelActivity.class);
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
		final Builder builder = new AlertDialog.Builder(
				ContextModelActivity.this);
		builder.setItems(new String[] { "重命名", "删除" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						switch (which) {
						case 0:
							Tools.logSh("oldMOdel"+modelName);
							showRenameDialog(modelName);
							break;
						case 1:
							showDeleteDialog(modelName);
							break;
						}
					}
				});
		builder.create().show();

	}

	public void showRenameDialog(final String modelName) {
		final Builder builder = new AlertDialog.Builder(
				ContextModelActivity.this);
		final EditText text = new EditText(getApplicationContext());
		builder.setView(text);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 修改数据库中数据
				String change_model = text.getText().toString();
				if (!TextUtils.isEmpty(change_model)) {
					Tools.logSh("开始修改modelName="+modelName+":::change_model=" + change_model);
					updateModel(modelName, change_model);

					dialog.dismiss();
				} else {
					Toast.makeText(ContextModelActivity.this, "情景模式不能为空",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	protected void showDeleteDialog(final String modelName) {
		AlertDialog.Builder builder = new Builder(ContextModelActivity.this);

		builder.setMessage("删除此情景模式？");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Tools.logSh("选择了确认按钮，删除了情景模式");
				deleteModel(modelName);
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
	protected void onDestroy() {
		unregisterReceiver(recevier);
		super.onDestroy();
	}

}
