package com.xstd.privatephone.tools;

import android.util.Log;

public class Tools {

	public static boolean isShow = true;
	
	public static void logSh(String msg){
		if(isShow){
			Log.i("sh", msg);
		}
	}
}
