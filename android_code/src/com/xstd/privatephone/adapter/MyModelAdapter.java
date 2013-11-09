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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.utils.ContactUtils;
import com.xstd.privatephone.tools.Tools;

public class MyModelAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Model> mModels;
	private Integer checkedId = -1;

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

	public View getView(final int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mContext, R.layout.context_model_item,
					null);
			TextView modelName = (TextView) view
					.findViewById(R.id.tv_modelname);
			CheckBox modelType = (CheckBox) view
					.findViewById(R.id.btn_check);

		final Model mModel = mModels.get(position);
		int type = mModel.getModel_type();
		if (type == 1) {
			checkedId = position;
			Tools.logSh("checkedId===="+checkedId);
			modelType.setChecked(true);
		} else {
			modelType.setChecked(false);
		}
		modelName.setText(mModel.getModel_name());
		
		modelType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(checkedId!=-1){// hasChecked
						Tools.logSh("以前备选中的清除了===="+checkedId+"号");
						mModels.get(checkedId).setModel_type(0);
						update(mModels.get(checkedId));
					}
					
					checkedId = position;
					Tools.logSh("新选中了===="+checkedId+"号");
					mModel.setModel_type(1);
					update(mModels.get(checkedId));
				ContactUtils.modelChangeContact(mContext);//需要继续修改
					
				}else{
					
					Tools.logSh("撤消了选中de===="+checkedId+"号");
					
					mModel.setModel_type(0);
					update(mModels.get(checkedId));
				}
			}
		});
		
		return view;
	}

	private void update(Model model) {
		//跟新数据库
		ModelDao modelDao = ModelDaoUtils.getModelDao(mContext);
		modelDao.update(model);
		
		// 更新UI
		Intent intent = new Intent();
		intent.setAction("ModelBroadcastReciver");
		mContext.sendBroadcast(intent);
	}

}