package com.xstd.privatephone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.IImage;
import com.xstd.pirvatephone.module.IImageList;
import com.xstd.pirvatephone.utils.ImageManager;

public class ShowFolderMediaAdapter extends BaseAdapter {

	private Context mContext;

	private IImageList mIImageList;

	public ShowFolderMediaAdapter(Context ctx, IImageList list) {
		mContext = ctx;
		if (list == null)
			mIImageList = ImageManager.makeEmptyImageList();
		else
			mIImageList = list;
	}

	@Override
	public int getCount() {
		return mIImageList.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mIImageList.getImageAt(position);
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
			convertView = View.inflate(mContext, R.layout.item_image, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		IImage image = mIImageList.getImageAt(position);
		
		holder.iv.setImageBitmap(image.miniThumbBitmap());
		return convertView;
	}

	public void setImageList(IImageList list) {
		mIImageList = list;
	}

	static class ViewHolder {
		ImageView iv;
	}

}
