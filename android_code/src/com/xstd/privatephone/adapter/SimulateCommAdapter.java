package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.activity.SimulaCommActivity;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.module.SimulaPhone;
import com.xstd.pirvatephone.module.SimulaSms;

public class SimulateCommAdapter extends BaseAdapter {

	private Context mCtx;
	private List<SimulateComm> mDatas = new ArrayList<SimulateComm>();
	private int type;
	private DateFormat df;

	public SimulateCommAdapter(Context ctx, int type) {
		mCtx = ctx;
		this.type = type;
		df = new SimpleDateFormat("yyyy-MM-dd kk:mm");
	}

	/**
	 * 通过类型来修改数据
	 */
	public void changeDatas() {
		if (type == SimulaCommActivity.SIMULATE_SMS) {
			mDatas = SimulaSms.getInstance(mCtx).getSimulaCommByType();
		} else if (type == SimulaCommActivity.SIMULATE_PHONE) {
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(mCtx, R.layout.simulate_comm_item, null);
			holder.main = (TextView) convertView.findViewById(R.id.main);
			holder.mr = (TextView) convertView.findViewById(R.id.mr);
			holder.mb = (TextView) convertView.findViewById(R.id.mb);
			holder.pr = (TextView) convertView.findViewById(R.id.pr);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SimulateComm comm = mDatas.get(position);
		String futuretime = df.format(comm.getFuturetime());
		holder.main.setText(comm.getName());
		if (type == SimulaCommActivity.SIMULATE_PHONE) {
			holder.mr.setText("(" + comm.getPhonenumber() + ")");
			holder.mb.setText(futuretime);
			if (comm.getFuturetime().getTime() > System.currentTimeMillis()) {
				holder.pr.setTextColor(0XFF3E76A9);
				holder.pr.setText("预约中...");
			} else {
				holder.pr.setTextColor(0XFF3EA946);
				holder.pr.setText("预约成功");
			}
		} else if (type == SimulaCommActivity.SIMULATE_SMS) {
			// holder.mr.setText("(50)");
			holder.pr.setTextColor(0XFF208ECD);
			holder.pr.setText(futuretime);
			holder.mb.setText(comm.getContent());
		}
		return convertView;
	}

	/**
     *
     */
	class ViewHolder {
		TextView main;
		TextView mr;
		TextView mb;
		TextView pr;
	}

}
