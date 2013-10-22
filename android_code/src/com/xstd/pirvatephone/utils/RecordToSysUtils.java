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
	public void restoreContact(ArrayList<String> selectContactsNumbers) {
		
		String[] selectNumbers = parseArray(selectContactsNumbers);

		Tools.logSh("selectNumbers===" + selectNumbers.length);

		restoreContact(selectNumbers);

	}
	
	public void restoreContact(String[] selectNumbers){
		// 短信恢复，向系统短信数据库添加短信
		RestoreSystemSmsUtils mRestoreSystemSmsUtils = new RestoreSystemSmsUtils(
				mContext, selectNumbers);
		mRestoreSystemSmsUtils.restoreSms();

		// 通话记录恢复到手机上
		RestoreSystemPhoneUtils mRestoreSystemPhoneUtils = new RestoreSystemPhoneUtils(
				mContext, selectNumbers);
		mRestoreSystemPhoneUtils.restorePhone();

		// record短信息移除
		DelectOurSmsRecordsUtils mDelectOurSmsRecordsUtils = new DelectOurSmsRecordsUtils(
				mContext, selectNumbers);
		mDelectOurSmsRecordsUtils.deleteSmsRecords();

		// detail短信息移除
		DelectOurSmsDetailsUtils mDelectOurSmsDetailsUtils = new DelectOurSmsDetailsUtils(
				mContext, selectNumbers);
		mDelectOurSmsDetailsUtils.deleteSmsDetails();

		// 通话记details录移除
		DelectOurPhoneDetailsUtils mDelectOurPhoneDetailsUtils = new DelectOurPhoneDetailsUtils(
				mContext, selectNumbers);
		mDelectOurPhoneDetailsUtils.deletePhoneDetails();

		// 通话记录record移除
		DelectOurPhoneRecordsUtils mDelectOurPhoneRecordsUtils = new DelectOurPhoneRecordsUtils(
				mContext, selectNumbers);
		mDelectOurPhoneRecordsUtils.deletePhoneRecords();
	}

}
