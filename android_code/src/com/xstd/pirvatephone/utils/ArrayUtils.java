package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import com.xstd.privatephone.tools.Tools;

public class ArrayUtils {
	
	private String[] selectPhones;

	public String[] listToArray(ArrayList<String> arrays){
		if(selectPhones!=null && selectPhones.length>0){
			selectPhones = null;
		}
		
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
