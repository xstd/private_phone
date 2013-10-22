package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.Context;

import com.xstd.privatephone.tools.Tools;

public class RecordToUsUtils {
	private Context mContext;
	private String[] selectPhones;
	
	
	public RecordToUsUtils(Context context){
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

	public void removeContactRecord(ArrayList<String> selectContactsNumbers , boolean delete) {
		Tools.logSh("removeContactRecord的长度为"+selectContactsNumbers.size());
		String[] selectNumbers = parseArray(selectContactsNumbers);
		
		Tools.logSh("selectNumbers==="+selectNumbers.length);
		
		removeContactRecord(selectNumbers,delete);
		
	}
	
	
	public void removeContactRecord(String[] selectNumbers, boolean delete){
		
		if(selectNumbers!=null && selectNumbers.length>0){
			// 将系统通话记录detail复制到我们数据库
			WritePhoneDetailUtils mWritePhoneDetailUtils = new WritePhoneDetailUtils(
					mContext, selectNumbers);
			mWritePhoneDetailUtils.writePhoneDetail();

			// 将系统通话记录record复制到我们数据库
			WritePhoneRecordUtils mWritePhoneRecordUtils = new WritePhoneRecordUtils(
					mContext, selectNumbers);
			mWritePhoneRecordUtils.writePhoneRecord();

			// 将系统sms detail复制到我们数据库
			WriteSmsDetailUtils mWriteSmsDetailUtils = new WriteSmsDetailUtils(
					mContext, selectNumbers);
			mWriteSmsDetailUtils.writeSmsDetail();

			// 将系统sms record复制到我们数据库
			WriteSmsRecordUtils mWriteSmsRecordUtils = new WriteSmsRecordUtils(
					mContext, selectNumbers);
			mWriteSmsRecordUtils.writeSmsRecord();
		}
		
		if(delete){
			 deleteSytemDetail(selectNumbers);
		}
	}
	
	/**
	 * 删除系统数据库的记录
	 * @param selectNumbers
	 */
	private void deleteSytemDetail(String[] selectNumbers) {
		Tools.logSh("deleteSytemDetail");
		DelectSystemPhoneUtils mDelectSystemPhoneUtils = new DelectSystemPhoneUtils(
				mContext, selectNumbers);
		mDelectSystemPhoneUtils.deletePhone();

		DelectSystemSmsUtils mDelectSystemSmsUtils = new DelectSystemSmsUtils(
				mContext, selectNumbers);
		mDelectSystemSmsUtils.deleteSms();
	}

}
