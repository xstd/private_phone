package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
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

    private static final String TAG = null;
    private Context mCtx;
    private List<SimulateComm> mDatas = new ArrayList<SimulateComm>();
    private int type;

    public SimulateCommAdapter(Context ctx, int type) {
        mCtx = ctx;
        this.type = type;
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
            if (type == SimulaCommActivity.SIMULATE_PHONE) {
                convertView = View.inflate(mCtx, R.layout.simulate_phone_item,
                        null);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.displayName = (TextView) convertView
                        .findViewById(R.id.displayName);
                holder.phoneNumber = (TextView) convertView
                        .findViewById(R.id.phoneNumber);
            } else if (type == SimulaCommActivity.SIMULATE_SMS) {
                convertView = View.inflate(mCtx, R.layout.simulate_sms_item,
                        null);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.displayName = (TextView) convertView
                        .findViewById(R.id.displayName);
                holder.content = (TextView) convertView
                        .findViewById(R.id.content);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SimulateComm comm = mDatas.get(position);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String futuretime = df.format(comm.getFuturetime());
        holder.time.setText(futuretime);
        if (type == SimulaCommActivity.SIMULATE_PHONE) {
            holder.phoneNumber.setText(comm.getPhonenumber() + "");
            if (TextUtils.isEmpty(comm.getName())) {
                holder.displayName.setText(mCtx.getString(R.string.s_stranger));
            } else {
                holder.displayName.setText(comm.getName());
            }
        } else if (type == SimulaCommActivity.SIMULATE_SMS) {
            holder.content.setText(comm.getContent());
            if (TextUtils.isEmpty(comm.getName())) {
                holder.displayName.setText(mCtx.getString(R.string.s_stranger)
                        + "(" + comm.getPhonenumber() + ")");
            } else {
                holder.displayName.setText(comm.getName());
            }

        }
        return convertView;
    }

    /**
     *
     */
    class ViewHolder {
        TextView time;
        TextView displayName;
        /**
         * phone中的
         */
        TextView phoneNumber;
        /**
         * sms中的
         */
        TextView content;
    }

}
