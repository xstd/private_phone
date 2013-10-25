package com.xstd.privatephone.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.xstd.pirvatephone.module.MediaModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-23
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
public class SelectFilesAdapter extends BaseAdapter {

    private List<MediaModule> mDatas;

    public SelectFilesAdapter(ArrayList<MediaModule> datas) {
        mDatas = datas;
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
        return convertView;
    }
}
