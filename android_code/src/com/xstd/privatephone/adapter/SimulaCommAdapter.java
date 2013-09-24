package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xstd.pirvatephone.activity.SimulaCommActivity;
import com.xstd.pirvatephone.module.SimulaComm;
import com.xstd.pirvatephone.module.SimulaPhone;
import com.xstd.pirvatephone.module.SimulaSms;

public class SimulaCommAdapter extends BaseAdapter {

	private Context mCtx;
	private List<SimulaComm> mDatas = new ArrayList<SimulaComm>();

	public SimulaCommAdapter(Context ctx) {
		mCtx = ctx;
	}

	/**
	 * 通过类型来修改数据
	 * @param type {@link com.xstd.pirvatephone.activity.SimulaCommActivity#SIMULA_SMS}、{@link com.xstd.pirvatephone.activity.SimulaCommActivity#SIMULA_PHONE}
	 */
	public void changeDatas(int type) {
		if (type == SimulaCommActivity.SIMULA_SMS) {
			mDatas = SimulaSms.getInstance(mCtx).getSimulaCommByType();
		} else if (type == SimulaCommActivity.SIMULA_PHONE) {
			mDatas = SimulaPhone.getInstance(mCtx).getSimulaCommByType();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
