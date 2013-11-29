package com.xstd.privatephone.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

/**
 * Created by Chrain on 13-11-1.
 */
public class AddCommonPhoneAdapter extends CursorAdapter {

	private Context ctx;

	private LayoutInflater inflater;

	private DateFormat df;

	public AddCommonPhoneAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		ctx = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df = new SimpleDateFormat("yyyy-MM-dd kk:mm");
		initPhoneStatusDrawable();
	}

	/** Call log type for incoming calls. */
	Drawable incoming_type;
	/** Call log type for outgoing calls. */
	Drawable outgoing_type;
	/** Call log type for missed calls. */
	Drawable missed_type;

	private void initPhoneStatusDrawable() {
		incoming_type = ctx.getResources().getDrawable(android.R.drawable.sym_call_incoming);
		outgoing_type = ctx.getResources().getDrawable(android.R.drawable.sym_call_outgoing);
		missed_type = ctx.getResources().getDrawable(android.R.drawable.sym_call_outgoing);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = inflater.inflate(R.layout.call_log_item, null);
		ViewHolder holder = new ViewHolder();
		holder.main = (TextView) view.findViewById(R.id.main);
		holder.mr = (TextView) view.findViewById(R.id.mr);
		holder.mb = (TextView) view.findViewById(R.id.mb);
		holder.pr = (ImageView) view.findViewById(R.id.pr);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		final String number = cursor.getString(1);
		Long date = cursor.getLong(2);
		String name = cursor.getString(3);
		int type = cursor.getInt(4);
		holder.main.setText(name);
		holder.mr.setText("(" + number + ")");
		holder.mb.setText(df.format(new Date(date)));
		switch (type) {
		case CallLog.Calls.INCOMING_TYPE:
			holder.pr.setImageDrawable(incoming_type);
			break;
		case CallLog.Calls.OUTGOING_TYPE:
			holder.pr.setImageDrawable(outgoing_type);
			break;
		case CallLog.Calls.MISSED_TYPE:
			holder.pr.setImageDrawable(missed_type);
			break;
		}
		holder.pr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ number));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
			}
		});
	}

	static class ViewHolder {
		TextView main;
		TextView mr;
		TextView mb;
		ImageView pr;
	}
}
