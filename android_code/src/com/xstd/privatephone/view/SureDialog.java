package com.xstd.privatephone.view;

import com.xstd.pirvatephone.R;
import com.xstd.pirvatephone.dao.phone.PhoneRecordDaoUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneDetailsUtils;
import com.xstd.pirvatephone.utils.DelectOurPhoneRecordsUtils;
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

public class SureDialog extends Dialog implements
		View.OnClickListener {
	private Button btn_sure;
	private Button btn_cancel;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private String mNumber;
	private Context mContext;

	public SureDialog(Context context, String number) {
		super(context);
		mContext = context;
		this.mNumber = number;
		builder = new Builder(context);
		View entryView = LayoutInflater.from(context).inflate(
				R.layout.dialog_select_sure, null, true);
		builder.setView(entryView);

		dialog = builder.create();
		dialog.show();

		btn_sure = ((Button) entryView.findViewById(R.id.dialog_ok_btn));
		btn_sure.setOnClickListener(this);
		btn_cancel = ((Button) entryView.findViewById(R.id.dialog_cancel_btn));
		btn_cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View paramView) {
		if (paramView == btn_sure) {
			Toast.makeText(mContext, "----开始删除-----", Toast.LENGTH_SHORT).show();
			DelectOurPhoneRecordsUtils.deletePhoneRecords(mContext, new String[]{mNumber});
			DelectOurPhoneDetailsUtils.deletePhoneDetails(mContext, new String[]{mNumber});
			dialog.dismiss();
			Toast.makeText(mContext, "----执行完毕-----", Toast.LENGTH_SHORT).show();
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