package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.privatephone.adapter.PhoneDetailAdapter.ViewHold;
import com.xstd.privatephone.bean.MyContactInfo;
import com.xstd.privatephone.tools.Tools;

public class ModelAdapter extends CursorAdapter {
	
	private Context mContext;
	private static int CURMODEL = 1;

	public ModelAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		ViewHold views = (ViewHold) view.getTag();
		String model_name = cursor.getString(cursor.getColumnIndex(ModelDao.Properties.Model_name.columnName));
		int model_type = cursor.getInt(cursor.getColumnIndex(ModelDao.Properties.Model_type.columnName));
		views.modelname.setText(model_name);
		if(model_type==CURMODEL){
			views.btn_check.setChecked(true);
		}else{
			views.btn_check.setChecked(false);
		}
		views.btn_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

}