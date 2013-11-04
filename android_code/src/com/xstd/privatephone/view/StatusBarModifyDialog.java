package com.xstd.privatephone.view;

import com.xstd.pirvatephone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
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

	public StatusBarModifyDialog(Context context) {
		super(context);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		final View entryView = LayoutInflater.from(context).inflate(
				R.layout.dialog_modify_statusbar, null);

		builder.setIcon(R.drawable.ic_statusbar_0);

		builder.setTitle("修改提示信息");

		builder.setView(entryView);

		builder.create().show();

		titleEdit = (TextView) entryView.findViewById(R.id.titleEdit);
		contentEdit = (TextView) entryView.findViewById(R.id.contentEdit);
		
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
			 dismiss();
		}

	}

}