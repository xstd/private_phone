package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xstd.pirvatephone.dao.service.ProxyTalk;

public class ProxyTalkListAdapter extends BaseAdapter {

	private Context mCtx;
	private LayoutInflater inflater;
	private ArrayList<ProxyTalk> datas;

	public ProxyTalkListAdapter(Context ctx) {
		mCtx = ctx;
		datas = new ArrayList<ProxyTalk>();
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return null;
	}

	public void updateDatas() {

	}

}
