package com.xstd.privatephone.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xstd.pirvatephone.R;

import java.text.DateFormat;
import java.util.Date;

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
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        df = DateFormat.getInstance();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.call_log_item,null);
        ViewHolder holder = new ViewHolder();
        holder.displayName = (TextView) view.findViewById(R.id.displayName);
        holder.phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
        holder.phoneStatusText = (TextView) view.findViewById(R.id.phoneStatusText);
        holder.dial = (ImageView) view.findViewById(R.id.dial);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final String number = cursor.getString(1);
        Long date = cursor.getLong(2);
        String name = cursor.getString(3);
        holder.displayName.setText(TextUtils.isEmpty(name)?number:name);
        holder.phoneNumber.setText(number);
        holder.phoneStatusText.setText(df.format(new Date(date)));
        holder.dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
    }

    static class ViewHolder {
         TextView displayName;
         TextView phoneNumber;
         TextView phoneStatusText;
        ImageView dial;
    }
}
