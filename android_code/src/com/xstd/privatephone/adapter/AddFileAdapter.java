package com.xstd.privatephone.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.MediaModule;

/**
 * Created with IntelliJ IDEA. User: Chrain Date: 13-10-21 Time: 下午1:49 To
 * change this template use File | Settings | File Templates.
 */
public class AddFileAdapter extends BaseAdapter {

	private static final String TAG = "AddFileAdapter";
	private Context mCtx;

	private List<String> keys = new ArrayList<String>();

	private Map<String, List<MediaModule>> mData = new HashMap<String, List<MediaModule>>();

	private int displayPosition = -1;

	public AddFileAdapter(Context context) {
		mCtx = context;
	}

	@Override
	public int getCount() {
		if (displayPosition == -1)
			return keys.size();
		else
			return mData.get(keys.get(displayPosition)).size();
	}

	@Override
	public Object getItem(int position) {
		if (displayPosition == -1)
			return mData.get(keys.get(position));
		else
			return mData.get(keys.get(displayPosition)).get(position);
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
			holder.fileThumb = (ImageView) convertView
					.findViewById(R.id.file_thumb);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.file_name);
			holder.filePath = (TextView) convertView
					.findViewById(R.id.file_path);
			holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
			holder.cb = (CheckBox) convertView.findViewById(R.id.select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (displayPosition == -1) {
			holder.arrow.setVisibility(View.VISIBLE);
			holder.cb.setVisibility(View.GONE);
			String path = keys.get(position);
			List<MediaModule> medias = mData.get(path);
			for (MediaModule media : medias) {
				if (media.getThumb() != null)
					holder.fileThumb.setImageBitmap(media.getThumb());
				break;
			}
			holder.filePath.setText(path);
			holder.fileName.setText(path.substring(path.lastIndexOf("/") + 1)
					+ "(" + mData.get(path).size() + "个)");
		} else {
			holder.arrow.setVisibility(View.GONE);
			holder.cb.setVisibility(View.VISIBLE);
			MediaModule module = (MediaModule) getItem(position);
			if (module.getThumb() != null)
				holder.fileThumb.setImageBitmap(module.getThumb());
			holder.cb.setChecked(module.isSelect());
			holder.fileName.setText(module.getDisplay_name());
			holder.filePath.setText(module.getPath());
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView fileThumb;
		TextView fileName;
		TextView filePath;
		ImageView arrow;
		CheckBox cb;
	}

	public void changeData(Map<String, ArrayList<MediaModule>> data,
			int position) {
		displayPosition = position;
		mData.clear();
		mData.putAll(data);
		keys.clear();
		for (Map.Entry<String, ArrayList<MediaModule>> entry : data.entrySet()) {
			String key = entry.getKey();
			keys.add(key);
		}
		notifyDataSetChanged();
	}
}
