package com.xstd.privatephone.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.module.Item;
import com.xstd.privatephone.view.GalleryPickerItem;

public class ShowSDCardMediaAdapter extends BaseAdapter {

	public ArrayList<Item> mItems = new ArrayList<Item>();

	LayoutInflater mInflater;

	public ShowSDCardMediaAdapter(LayoutInflater inflater) {
		mInflater = inflater;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;

		if (convertView == null) {
			v = mInflater.inflate(R.layout.gallery_picker_item, null);
		} else {
			v = convertView;
		}

		TextView titleView = (TextView) v.findViewById(R.id.title);

		GalleryPickerItem iv = (GalleryPickerItem) v
				.findViewById(R.id.thumbnail);
		Item item = mItems.get(position);
		iv.setOverlay(item.getOverlay());
		if (item.mThumbBitmap != null) {
			iv.setImageBitmap(item.mThumbBitmap);
			String title = item.mName + " (" + item.mCount + ")";
			titleView.setText(title);
		} else {
			iv.setImageResource(android.R.color.transparent);
			titleView.setText(item.mName);
		}

		// An workaround due to a bug in TextView. If the length of text is
		// different from the previous in convertView, the layout would be
		// wrong.
		titleView.requestLayout();

		return v;
	}

	public void clear() {
		mItems.clear();
	}

	public void updateDisplay() {
		notifyDataSetChanged();
	}

	public void addItem(Item item) {
		mItems.add(item);
	}

}
