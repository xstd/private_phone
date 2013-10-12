package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.service.ProxyServiceDaoUtils;
import com.xstd.pirvatephone.dao.service.ProxyTalk;
import com.xstd.pirvatephone.dao.service.ProxyTalkDao;

public class ProxyTalkListAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = "ProxyTalkListAdapter";
	
	private Context mCtx;
	private LayoutInflater inflater;
	private List<ProxyTalk> datas;
	private DateFormat df;

	public ProxyTalkListAdapter(Context ctx) {
		mCtx = ctx;
		datas = new ArrayList<ProxyTalk>();
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df = android.text.format.DateFormat.getTimeFormat(ctx);
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_proxy_talk, null);
			holder.service_name = (TextView) convertView.findViewById(R.id.service_name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.type = (TextView) convertView.findViewById(R.id.type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProxyTalk proxyTalk = datas.get(position);
		holder.service_name.setText(proxyTalk.getPhonenumber() + proxyTalk.getWeixinnumber());
		holder.time.setText(df.format(proxyTalk.getStarttime()) + "——" + df.format(proxyTalk.getEndtime()));
		holder.type.setText(proxyTalk.getType() == 0 ? "被动回复" : "主动回复");
		return convertView;
	}

	private static class ViewHolder {
		TextView service_name;
		TextView time;
		TextView type;
	}

	/**
	 * 更新ListView显示的数据
	 */
	public void updateDatas() {
		datas.clear();
		ProxyTalkDao dao = ProxyServiceDaoUtils.getProxyTalkDao(mCtx);
		Cursor cursor = dao.getDatabase().query(dao.getTablename(), dao.getAllColumns(), null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				ProxyTalk proxyTalk = new ProxyTalk();
				proxyTalk.setId(cursor.getLong(cursor.getColumnIndex(ProxyTalkDao.Properties.Id.columnName)));
				proxyTalk.setPhonenumber(cursor.getString(cursor.getColumnIndex(ProxyTalkDao.Properties.Phonenumber.columnName)));
				proxyTalk.setWeixinnumber(cursor.getString(cursor.getColumnIndex(ProxyTalkDao.Properties.Weixinnumber.columnName)));
				proxyTalk.setWeixinpwd(cursor.getString(cursor.getColumnIndex(ProxyTalkDao.Properties.Weixinpwd.columnName)));
				proxyTalk.setTalk_content(cursor.getString(cursor.getColumnIndex(ProxyTalkDao.Properties.Talk_content.columnName)));
				proxyTalk.setStarttime(new Date(cursor.getLong(cursor.getColumnIndex(ProxyTalkDao.Properties.Starttime.columnName))));
				proxyTalk.setEndtime(new Date(cursor.getLong(cursor.getColumnIndex(ProxyTalkDao.Properties.Endtime.columnName))));
				proxyTalk.setType(cursor.getInt(cursor.getColumnIndex(ProxyTalkDao.Properties.Type.columnName)));
				datas.add(proxyTalk);
			}
		}
		cursor.close();
		notifyDataSetChanged();
	}
}
