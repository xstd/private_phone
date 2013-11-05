package com.xstd.privatephone.view;

import com.xstd.pirvatephone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatusBarModifyDialog extends Dialog implements View.OnClickListener {
	private Button btn_sure;
	private Button btn_cancel;
	private TextView titleEdit;
	private TextView contentEdit;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	public StatusBarModifyDialog(Context context) {
		super(context);
		
		builder = new Builder(context);
		View entryView = LayoutInflater.from(context).inflate(R.layout.dialog_modify_statusbar, null, true);
		builder.setView(entryView);

		dialog = builder.create();
		dialog.show();

		SharedPreferences sp = context.getSharedPreferences("Setting_Info", 0);
		
		titleEdit = (TextView) entryView.findViewById(R.id.titleEdit);
		contentEdit = (TextView) entryView.findViewById(R.id.contentEdit);
		titleEdit.setText(sp.getString("Title", ""));
		contentEdit.setText(sp.getString("Desc", ""));
		
		btn_sure = ((Button) entryView.findViewById(R.id.dialog_ok_btn));
		btn_sure.setOnClickListener(this);
		btn_cancel = ((Button) entryView.findViewById(R.id.dialog_cancel_btn));
		btn_cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View paramView) {
		if(paramView==btn_sure){
			String str1 = titleEdit.getEditableText().toString();
		      String str2 = contentEdit.getEditableText().toString();
		      return ;
		}
		if(paramView==btn_cancel){
			if(dialog!=null){
				dialog.dismiss();
			}
		}
	}

}