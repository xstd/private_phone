package com.xstd.pirvatephone.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class DateUtils {
	public static String parseDate(Long date) {
		String time = "";
		String time1 = "";
		String time3 = "";

		// 2013-11-22 下午 12:34:33-------》11/1 下午 2:20
		SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd hh:mm");
		time = dateformat.format(date);
		time1 = time.substring(6, 8);
		int time2 = Integer.valueOf(time1);
		if (time2 < 12) {
			time3 = "上午";
		} else {
			time3 = "下午";
		}

		String[] time4 = time.split(" ");
		for (int i = 0; i < time4.length; i++) {
			time = time4[0] + " " + time3 + " " + time4[1];
		}
		return time;
	}
	
	public static String parseDuration(Long date){
		Integer time = Integer.valueOf(date.toString());
		StringBuffer buffTime = new StringBuffer("");
		Integer currTime = 0;
		if(time/3600>0){
			buffTime.append(date/3600+"时");
			currTime = time%3600;
		} 
		
		if(currTime/60>0){
			buffTime.append(date/60+"分");
			currTime = time%60;
		}
		
		if(currTime>0){
			buffTime.append(currTime+"秒");
		}
		
		if("".equals(buffTime.toString())){
			return "0秒";
		}
		
		return buffTime.toString();
	}
}
