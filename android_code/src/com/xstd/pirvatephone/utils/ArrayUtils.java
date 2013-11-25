package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import com.xstd.privatephone.tools.Tools;

public class ArrayUtils {

	public static String[] listToArray(ArrayList<String> arrays){
		String[] selectPhones = null ;
		
		if (arrays.size() > 0) {
			selectPhones = new String[arrays.size()];
			for (int i = 0; i < arrays.size(); i++) {
				selectPhones[i] = arrays.get(i);
				Tools.logSh("selectPhones[i]=" + selectPhones[i]);
			}
		} else {
			return null;
		}

		return selectPhones;
	}
}
