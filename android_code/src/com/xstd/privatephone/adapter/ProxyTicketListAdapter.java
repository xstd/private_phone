package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.service.ProxyServiceDaoUtils;
import com.xstd.pirvatephone.dao.service.ProxyTicket;
import com.xstd.pirvatephone.dao.service.ProxyTicketDao;

public class ProxyTicketListAdapter extends BaseAdapter {

	private Context mCtx;
	private LayoutInflater inflater;
	private List<ProxyTicket> datas;
	private DateFormat df;

	public ProxyTicketListAdapter(Context ctx) {
		mCtx = ctx;
		datas = new ArrayList<ProxyTicket>();
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df = android.text.format.DateFormat.getDateFormat(ctx);
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
			convertView = inflater.inflate(R.layout.item_proxy_ticket, null);
			holder.info = (TextView) convertView.findViewById(R.id.flightinfo);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProxyTicket ticket = datas.get(position);
		holder.info.setText(df.format(ticket.getDate()) + "  " + ticket.getFlightnumber());
		holder.name.setText(ticket.getName());
		holder.status.setText(TextUtils.isEmpty(ticket.getStatus()) ? "等待受理" : ticket.getStatus());
		return convertView;
	}

	private static class ViewHolder {
		TextView info;
		TextView name;
		TextView status;
	}

	public void updateDatas() {
		datas.clear();
		ProxyTicketDao dao = ProxyServiceDaoUtils.getProxyTicketDao(mCtx);
		Cursor cursor = dao.getDatabase().query(dao.getTablename(), dao.getAllColumns(), null, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				ProxyTicket proxyTicket = new ProxyTicket();
				proxyTicket.setId(cursor.getLong(cursor.getColumnIndex(ProxyTicketDao.Properties.Id.columnName)));
				proxyTicket.setName(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Name.columnName)));
				proxyTicket.setCardid(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Cardid.columnName)));
				proxyTicket.setFlightnumber(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Flightnumber.columnName)));
				proxyTicket.setDate(new Date(cursor.getLong(cursor.getColumnIndex(ProxyTicketDao.Properties.Date.columnName))));
				proxyTicket.setReceiver(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Receiver.columnName)));
				proxyTicket.setPhonenumber(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Phonenumber.columnName)));
				proxyTicket.setStatus(cursor.getString(cursor.getColumnIndex(ProxyTicketDao.Properties.Status.columnName)));
				datas.add(proxyTicket);
			}
		}
		cursor.close();
		notifyDataSetChanged();
	}

}
