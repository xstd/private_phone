package com.xstd.privatephone.adapter;

import com.xstd.pirvatephone.R;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA. User: Chrain Date: 13-10-21 Time: 上午10:16 To
 * change this template use File | Settings | File Templates.
 */
public class AddPrivacyPicAdapter extends CursorAdapter {

	private Context mCtx;

	public AddPrivacyPicAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mCtx = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d("TAG", "bindView");
		ViewHolder holder = (ViewHolder) view.getTag();
		if (cursor.getPosition() == cursor.getCount() - 1) {
			holder.iv_p.setVisibility(View.INVISIBLE);
			holder.iv.setImageResource(R.drawable.selector_add_album_btn);
		} else {
			holder.iv_p.setVisibility(View.VISIBLE);
			holder.iv.setImageResource(R.drawable.album_default_icon);
		}
		String str = cursor.getString(1);
		holder.name.setText(str);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.d("TAG", "newView");
		View view = View.inflate(mCtx, R.layout.item_show_img_folder, null);
		ViewHolder holder = new ViewHolder();
		holder.iv_p = (ImageView) view.findViewById(R.id.iv_p);
		holder.iv = (ImageView) view.findViewById(R.id.iv);
		holder.name = (TextView) view.findViewById(R.id.name);
		holder.count = (TextView) view.findViewById(R.id.count);
		view.setTag(holder);
		return view;
	}

	class ViewHolder {
		ImageView iv_p;
		ImageView iv;
		TextView name;
		TextView count;
	}
}
