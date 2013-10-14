package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class MyModelAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Model> mModels;

	/** 选取转换为隐私联系人的号码 **/

	public MyModelAdapter(Context context, ArrayList<Model> modelInfos) {
		mContext = context;
		mModels = modelInfos;
	}

	public int getCount() {
		// 设置绘制数量
		return mModels.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public Object getItem(int position) {
		return mModels.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = View.inflate(mContext, R.layout.context_model_item,
					null);
			hold = new ViewHold();

			hold.modelName = (TextView) convertView
					.findViewById(R.id.tv_modelname);
			hold.modelType = (CheckBox) convertView
					.findViewById(R.id.btn_check);

			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}

		final Model mModel = mModels.get(position);
		// 绘制联系人名称
		hold.modelName.setText(mModel.getModel_name());
		int type = mModel.getModel_type();
		if (type == 1) {
			hold.modelType.setChecked(true);
		} else {
			hold.modelType.setChecked(false);
		}

		hold.modelType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mModel.getModel_type() == 1) {
					setAllModeType();
					mModel.setModel_type(0);
					update();
				} else {
					setAllModeType();
					mModel.setModel_type(1);
					update();
				}
				Tools.logSh(mModel.getModel_type() + "");
			}
		});
		return convertView;
	}

	static class ViewHold {
		TextView modelName;
		CheckBox modelType;
	}

	private void setAllModeType() {
		for (Model modelInfo : mModels) {
			modelInfo.setModel_type(0);
		}
	}

	private void update() {
		//跟新数据库
		ModelDao modelDao = ModelDaoUtils.getModelDao(mContext);
		SQLiteDatabase modelDatabase = modelDao.getDatabase();
		Cursor query = modelDatabase.query(ModelDao.TABLENAME, null, null, null, null, null, null);
		
		if(query!=null && query.getCount()>0){
			while(query.moveToNext()){
				for (Model modelInfo : mModels) {
					modelDao.update(modelInfo);
				}	
			}
			query.close();
		}
		
		// 更新UI
		Intent intent = new Intent();
		intent.setAction("ModelBroadcastReciver");
		mContext.sendBroadcast(intent);
	}

}