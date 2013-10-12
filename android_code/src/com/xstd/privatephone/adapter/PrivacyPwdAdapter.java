package com.xstd.privatephone.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyPwd;

public class PrivacyPwdAdapter extends BaseAdapter {

	private Context mContext;
	private List<PrivacyPwd> datas;

	public PrivacyPwdAdapter(Context mContext, List<PrivacyPwd> datas) {
		this.mContext = mContext;
		this.datas = datas;
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
			convertView = View.inflate(mContext, R.layout.item_privacy_pwd,
					null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.pwdname = (TextView) convertView.findViewById(R.id.pwdname);
			holder.pwdnum = (TextView) convertView.findViewById(R.id.pwdnum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PrivacyPwd mapping = datas.get(position);
		holder.pwdname.setText(mapping.getName());
		holder.pwdnum.setText(mapping.getNumber());
		return convertView;
	}

	static class ViewHolder {
		ImageView icon;
		TextView pwdname;
		TextView pwdnum;
	}

}
