package com.xstd.privatephone.view;

import android.app.DatePickerDialog;
import android.content.Context;

public class MyDatePickerDialog extends DatePickerDialog {

	public MyDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
	
	@Override
	protected void onStop() {
		//super.onStop();         //避免按手机返回键时执行回调。
	}

}
