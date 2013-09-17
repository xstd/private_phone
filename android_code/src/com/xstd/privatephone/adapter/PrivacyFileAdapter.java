package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.SrcToDestMapping;

public class PrivacyFileAdapter extends BaseAdapter {

	private Context mContext;
	private List<SrcToDestMapping> datas;
	private DateFormat df = new SimpleDateFormat();

	public PrivacyFileAdapter(Context context, List<SrcToDestMapping> result) {
		mContext = context;
		datas = result;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_privacy_file,
					null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.filename);
			holder.lastModify = (TextView) convertView
					.findViewById(R.id.filemodify);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SrcToDestMapping mapping = datas.get(position);
		holder.fileName.setText(mapping.getSrcName());
		holder.lastModify.setText(df.format(mapping.getMisstime()));
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView fileName;
		TextView lastModify;
	}

}
