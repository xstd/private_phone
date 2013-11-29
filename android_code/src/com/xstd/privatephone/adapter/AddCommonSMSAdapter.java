package com.xstd.privatephone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chrain on 13-11-1.
 */
public class AddCommonSMSAdapter extends CursorAdapter {

	private DateFormat df;

	LayoutInflater inflater;

	public AddCommonSMSAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df = new SimpleDateFormat("yyyy-MM-dd kk:mm");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = inflater.inflate(R.layout.simulate_comm_item, null);
		ViewHolder holder = new ViewHolder();
		holder.main = (TextView) view.findViewById(R.id.main);
		holder.mr = (TextView) view.findViewById(R.id.mr);
		holder.mb = (TextView) view.findViewById(R.id.mb);
		holder.pr = (TextView) view.findViewById(R.id.pr);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.main.setText(cursor.getString(1));
		// holder.mr.setText("(50)");
		holder.mb.setText(cursor.getString(3));
		holder.pr.setText(df.format(new Date(cursor.getLong(2))));
	}

	static class ViewHolder {
		TextView main;
		TextView mr;
		TextView mb;
		TextView pr;
	}
}
