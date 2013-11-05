package com.xstd.privatephone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.privacy.PrivacyPic;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chrain
 * Date: 13-10-21
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */
public class AddPrivacyPicAdapter extends BaseAdapter {

    private Context mCtx;

    private List<PrivacyPic> data;

    public AddPrivacyPicAdapter(Context ctx, List<PrivacyPic> img_name) {
        mCtx = ctx;
        data = img_name;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
            convertView = View.inflate(mCtx, R.layout.item_show_img_folder, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        if(position == data.size()-1) holder.iv.setImageResource(R.drawable.selector_add_album_btn);
        String str = data.get(position).getName();
        holder.name.setText(str);
        return convertView;
    }

    private class ViewHolder {
        ImageView iv;
        TextView name;
        TextView count;
    }
}
