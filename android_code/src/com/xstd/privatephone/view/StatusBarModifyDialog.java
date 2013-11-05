package com.xstd.privatephone.view;

import com.xstd.pirvatephone.R;
import com.xstd.privatephone.tools.Tools;

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
import android.widget.Toast;

public class StatusBarModifyDialog extends Dialog implements
		View.OnClickListener {
	private Button btn_sure;
	private Button btn_cancel;
	private TextView titleEdit;
	private TextView contentEdit;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private SharedPreferences sp;
	private String mTitle;
	private String mDesc;
	private Context mContext;

	public StatusBarModifyDialog(Context context, String title ,String desc) {
		super(context);
		mContext = context;
		this.mTitle = title;
		this.mDesc = desc;
		builder = new Builder(context);
		View entryView = LayoutInflater.from(context).inflate(
				R.layout.dialog_modify_statusbar, null, true);
		builder.setView(entryView);

		dialog = builder.create();
		dialog.show();

		sp = context.getSharedPreferences("Setting_Info", 0);

		titleEdit = (TextView) entryView.findViewById(R.id.titleEdit);
		contentEdit = (TextView) entryView.findViewById(R.id.contentEdit);
		titleEdit.setText(mTitle);
		contentEdit.setText(mDesc);

		btn_sure = ((Button) entryView.findViewById(R.id.dialog_ok_btn));
		btn_sure.setOnClickListener(this);
		btn_cancel = ((Button) entryView.findViewById(R.id.dialog_cancel_btn));
		btn_cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View paramView) {
		if (paramView == btn_sure) {
			int checkedId = sp.getInt("CheckedId", 0);
			String Id = checkedId + "";
			String str1 = titleEdit.getText().toString();
			String str2 = contentEdit.getText().toString();
			
			sp.edit().putString(Id, str1+":"+str2).commit();
			Tools.logSh(Id+"======str1=="+str1+"====str2==="+str2);
			
			dialog.dismiss();
			return;
		}
		if (paramView == btn_cancel) {
			Toast.makeText(mContext, "-----****-----", Toast.LENGTH_SHORT).show();
			if (dialog != null) {
				dialog.dismiss();
			}
		}
	}

}