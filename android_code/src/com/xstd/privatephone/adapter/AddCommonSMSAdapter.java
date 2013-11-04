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
import java.util.Date;

/**
 * Created by Chrain on 13-11-1.
 */
public class AddCommonSMSAdapter extends CursorAdapter {

    private Context ctx;

    private DateFormat df;

    LayoutInflater inflater;

    public AddCommonSMSAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        ctx = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        df = DateFormat.getDateTimeInstance();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.sms_item,null);
        ViewHolder holder = new ViewHolder();
        holder.address = (TextView) view.findViewById(R.id.address);
        holder.date = (TextView) view.findViewById(R.id.date);
        holder.content = (TextView) view.findViewById(R.id.content);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.address.setText(cursor.getString(0));
        holder.date.setText(df.format(new Date(cursor.getLong(1))));
        holder.content.setText(cursor.getString(2));
    }

    static class ViewHolder {
        TextView address;
        TextView date;
        TextView content;
    }
}
