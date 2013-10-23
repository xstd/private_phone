package com.xstd.privatephone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.MediaModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public class AddFileAdapter extends BaseAdapter {

    private static final String TAG = "AddFileAdapter";
    private Context mCtx;

    private Map<String, List<MediaModule>> mDatas = new HashMap<String, List<MediaModule>>();

    private List<String> mMapKeys = new ArrayList<String>();

    public AddFileAdapter(Context context) {
        mCtx = context;
    }

    @Override
    public int getCount() {
        return mMapKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(mMapKeys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mCtx, R.layout.item_add_file, null);
            holder.folderImage = (ImageView) convertView.findViewById(R.id.folderImage);
            holder.folderName = (TextView) convertView.findViewById(R.id.folderName);
            holder.path = (TextView) convertView.findViewById(R.id.path);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            holder.cb = (CheckBox) convertView.findViewById(R.id.select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = mMapKeys.get(position);
        holder.path.setText(path);
        holder.folderName.setText(path.substring(path.lastIndexOf("/") + 1) + "(" + mDatas.get(path).size() + "个)");
        return convertView;
    }

    public void showEdit(ArrayList<MediaModule> mediaModules) {
    }

    private class ViewHolder {
        ImageView folderImage;
        TextView folderName;
        TextView path;
        ImageView arrow;
        CheckBox cb;
    }


    public void changeData(Map<String, ArrayList<MediaModule>> data) {
        mDatas.clear();
        mDatas.putAll(data);
        notifyDataSetChanged();
    }
}
