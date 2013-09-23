package com.xstd.privatephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xstd.pirvatephone.R;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-9-22
 * Time: PM2:30
 * To change this template use File | Settings | File Templates.
 */
public class ServiceAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ServiceAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View ret = view;
        if (ret == null) {
            ret = mLayoutInflater.inflate(R.layout.service_list_item, null);
        }

        ImageView cover = (ImageView) ret.findViewById(R.id.image);
        TextView cate = (TextView) ret.findViewById(R.id.cate);
        switch (i) {
            case 0:
                cate.setText(R.string.service_dailiao);
                break;
            case 1:
                cate.setText(R.string.service_jipiao);
                break;
            case 2:
                cate.setText(R.string.service_liwu);
                break;
        }

        return ret;
    }
}
