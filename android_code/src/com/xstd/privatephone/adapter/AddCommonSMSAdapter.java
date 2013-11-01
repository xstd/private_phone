package com.xstd.privatephone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.xstd.pirvatephone.R;

/**
 * Created by Chrain on 13-11-1.
 */
public class AddCommonSMSAdapter extends CursorAdapter {

    private Context ctx;

    LayoutInflater inflater;

    public AddCommonSMSAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        ctx = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        inflater.inflate(R.layout.sms_item,null);
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    static class ViewHolder {

    }
}
