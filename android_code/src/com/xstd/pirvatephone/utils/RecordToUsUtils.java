package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
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
	
	/**
	 * 获取未重复的号码以及已经存在于我们数据库的隐私联系人号码
	 */
	public ArrayList<String> removeRepeat(ArrayList<String> mSelectPhones) {

		ArrayList<String> mNoRepeatPhones = new ArrayList<String>();
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(mContext);
		SQLiteDatabase contactDatabase = contactInfoDao.getDatabase();

		// 去除重复
		for (int i = 0; i < mSelectPhones.size(); i++) {
			if (!mNoRepeatPhones.contains(mSelectPhones.get(i))) {
				mNoRepeatPhones.add(mSelectPhones.get(i));
			}
		}

		return mNoRepeatPhones;
	}

	public void removeContactRecord(String[] selectNumbers, boolean delete){
		
		selectNumbers = ArrayUtils.listToArray(removeRepeat(ArrayUtils.arrayToList(selectNumbers)));
		
		if(selectNumbers!=null && selectNumbers.length>0){
			// 将系统通话记录detail复制到我们数据库
			WritePhoneDetailUtils.writePhoneDetail(mContext,selectNumbers);

			// 将系统通话记录record复制到我们数据库
			WritePhoneRecordUtils.writePhoneRecord(mContext,selectNumbers);

			// 将系统sms detail复制到我们数据库
			WriteSmsDetailUtils.writeSmsDetail(mContext,selectNumbers);

			// 将系统sms record复制到我们数据库
			WriteSmsRecordUtils.writeSmsRecord(mContext,selectNumbers);
			if(delete){
				 deleteSytemDetail(selectNumbers);
			}
		}
	}
	
	/**
	 * 删除系统数据库的记录
	 * @param selectNumbers
	 */
	private void deleteSytemDetail(String[] selectNumbers) {
		Tools.logSh("deleteSytemDetail");
		DelectSystemPhoneUtils.deletePhone(mContext,selectNumbers);

		DelectSystemSmsUtils.deleteSms(mContext,selectNumbers);
	}

}
