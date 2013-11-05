package com.xstd.privatephone.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.utils.AppUtils;

public class ShowAllInstallAppAdapter extends BaseAdapter {

	private Context mContext;

	List<PackageInfo> packages;

	public ShowAllInstallAppAdapter(Context context) {
		mContext = context;
		packages = AppUtils.getAllInstallApp(mContext);
	}

	@Override
	public int getCount() {
		return packages.size();
	}

	@Override
	public Object getItem(int arg0) {
		return packages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,R.layout.item_show_install_app, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			holder.version = (TextView) convertView.findViewById(R.id.app_version);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		PackageInfo packageInfo = packages.get(position);
		holder.icon.setImageDrawable(packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()));
		holder.name.setText(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()));
		holder.version.setText(packageInfo.versionName);
		return convertView;
	}

	static class ViewHolder {
		ImageView icon;
		TextView name;
		TextView version;
	}

}
