package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.Context;

import com.xstd.privatephone.tools.Tools;

public class RecordToSysUtils {
	private Context mContext;
	private String[] selectPhones;
	
	
	public RecordToSysUtils(Context context){
		this.mContext = context;
	}
	
	private String[] parseArray(ArrayList<String> selectContactsNumbers) {
		Tools.logSh("parseArray");
		selectPhones = null;

		if (selectContactsNumbers.size() > 0) {
			selectPhones = new String[selectContactsNumbers.size()];
			for (int i = 0; i <selectContactsNumbers.size(); i++) {
				selectPhones[i] = selectContactsNumbers.get(i);
				Tools.logSh("selectPhones[i]="+selectPhones[i]);
			}
		}
		
		return  selectPhones;
	}

	/**
	 * 将选定联系人信息移除我们数据库，返回到系统数据库中
	 */
	public void restoreContact(ArrayList<String> selectContactsNumbers,boolean flag) {
		
		String[] selectNumbers = parseArray(selectContactsNumbers);

		Tools.logSh("selectNumbers===" + selectNumbers.length);

		restoreContact(selectNumbers,flag);

	}
	
	public void restoreContact(String[] selectNumbers, boolean flag){
		
		if(flag){
			// 短信恢复，向系统短信数据库添加短信
			RestoreSystemSmsUtils.restoreSms(mContext, selectNumbers);

			// 通话记录恢复到手机上
			RestoreSystemPhoneUtils.restorePhone(mContext, selectNumbers);

		}else{
		
		// record短信息移除
		DelectOurSmsRecordsUtils.deleteSmsRecords(mContext, selectNumbers);

		// detail短信息移除
		DelectOurSmsDetailsUtils.deleteSmsDetails(mContext, selectNumbers);

		// 通话记details录移除
		DelectOurPhoneDetailsUtils.deletePhoneDetails(mContext, selectNumbers);

		// 通话记录record移除
		DelectOurPhoneRecordsUtils.deletePhoneRecords(mContext, selectNumbers);
		}
	}

}
