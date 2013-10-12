package com.xstd.privatephone.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class ModelAdapter extends CursorAdapter {

	private Context mContext;
	private static int CURMODEL = 1;
	private String model_name;
	private int model_type;
	private String selectModel = "";

	public ModelAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHold views = (ViewHold) view.getTag();
		model_name = cursor.getString(cursor
				.getColumnIndex(ModelDao.Properties.Model_name.columnName));
		model_type = cursor.getInt(cursor
				.getColumnIndex(ModelDao.Properties.Model_type.columnName));
		views.modelname.setText(model_name);
		if (model_type == CURMODEL) {
			views.btn_check.setChecked(true);
			selectModel = model_name;
		} else {
			views.btn_check.setChecked(false);
		}
		views.btn_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CheckBox check_box = (CheckBox) v.findViewById(R.id.btn_check);

				if (check_box.isChecked()) {
					// 开始为选中
					model_type = 1;
				} else {
					model_type = 0;
				}
				// 使其他选项为未选中状态

				updateModel(model_name, model_type);
			}
		});

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.context_model_item, null);
		ViewHold views = new ViewHold();

		views.modelname = (TextView) view.findViewById(R.id.tv_modelname);
		views.btn_check = (CheckBox) view.findViewById(R.id.btn_check);

		view.setTag(views);
		return view;
	}

	static class ViewHold {
		TextView modelname;
		CheckBox btn_check;
	}

	private void updateModel(String model, int type) {
		// 查询出此model,跟新数据库
		ModelDao modelDao = ModelDaoUtils.getModelDao(mContext);
		SQLiteDatabase modelDatabase = modelDao.getDatabase();
		Cursor modelQuery = modelDatabase.query(ModelDao.TABLENAME, null, null,
				null, null, null, null);
		if (modelQuery != null && modelQuery.getCount() > 0) {
			while (modelQuery.moveToNext()) {
				Long _id = modelQuery.getLong(modelQuery
						.getColumnIndex(ModelDao.Properties.Id.columnName));
				String modelname = modelQuery
						.getString(modelQuery
								.getColumnIndex(ModelDao.Properties.Model_name.columnName));
				Model mod = new Model();
				mod.setId(_id);
				mod.setModel_name(modelname);
				if (model.equals(modelname)) {
					mod.setModel_type(type);
				} else {
					mod.setModel_type(0);
				}

				modelDao.update(mod);
				Tools.logSh("更改了当前的情景模式");
			}
			modelQuery.close();
		}
	}
}