package com.xstd.privatephone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.MediaModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public class AddFileAdapter extends BaseAdapter {

    private Context mCtx;

    private List<MediaModule> mDatas = new ArrayList<MediaModule>();

    public AddFileAdapter(Context context) {
        mCtx = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int position) {
        return position;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mCtx, R.layout.item_add_file,null);
            holder.thumb = (ImageView) convertView.findViewById(R.id.thumb);
//            holder.display_name = (TextView) convertView.findViewById(R.id.display_name);
//            holder.date = (TextView) convertView.findViewById(R.id.date);
//            holder.size = (TextView) convertView.findViewById(R.id.size);
        }
        return convertView;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private class ViewHolder {
        ImageView thumb;
        TextView display_name;
        TextView date;
        TextView size;
    }


    public void changeData(List<MediaModule> datas) {
        mDatas.clear();
        mDatas.containsAll(datas);
        notifyDataSetChanged();
    }
}
