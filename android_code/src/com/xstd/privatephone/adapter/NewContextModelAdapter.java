package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import com.xstd.pirvatephone.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewContextModelAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> mNumbers = new ArrayList<String>();
	private ArrayList<String> mNames = new ArrayList<String>();

	public NewContextModelAdapter(Context context, ArrayList<String> numbers,
			ArrayList<String> names) {
		this.mContext = context;
		this.mNumbers = numbers;
		this.mNames = names;
	}

	@Override
	public int getCount() {
		if (mNumbers != null) {
			return mNumbers.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext,R.layout.private_new_context_model_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_phone_num);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
			
		}
		
		holder.tv_name.setText(mNumbers.get(position));
		holder.tv_number.setText(mNames.get(position));
		
		return convertView;
	}

	private static class ViewHolder {
		TextView tv_name;
		TextView tv_number;
	}

}
