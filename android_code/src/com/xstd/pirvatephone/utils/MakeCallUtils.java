package com.xstd.pirvatephone.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MakeCallUtils {
	
	public static void makeCall(Context context,String number){
		// 封装一个拨打电话的intent，并且将电话号码包装成一个Uri对象传入
		Intent intent = new Intent(Intent.ACTION_CALL, Uri
				.parse("tel:" + number));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);// 内部类
	}
	
}
