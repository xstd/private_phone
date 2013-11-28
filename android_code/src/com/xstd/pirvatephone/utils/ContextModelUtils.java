package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.activity.IntereptActivity;
import com.xstd.pirvatephone.activity.ModelEditActivity;
import com.xstd.pirvatephone.activity.NewContextModelActivity;
import com.xstd.pirvatephone.dao.contact.ContactInfoDao;
import com.xstd.pirvatephone.dao.contact.ContactInfoDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class ContextModelUtils {
	private String[] selectPhones;

	private ArrayList<String> intereptNumbers = new ArrayList<String>();
	private ArrayList<String> intereptNames = new ArrayList<String>();

	private ArrayList<String> noIntereptNumbers = new ArrayList<String>();
	private ArrayList<String> noIntereptNames = new ArrayList<String>();

	private String[] parseArray(ArrayList<String> selectContactsNumbers) {
		selectPhones = null;

		if (selectContactsNumbers.size() > 0) {
			selectPhones = new String[selectContactsNumbers.size()];
			for (int i = 0; i < selectContactsNumbers.size(); i++) {
				selectPhones[i] = selectContactsNumbers.get(i);
			}
		} else {
			return null;
		}

		return selectPhones;
	}

	/**
	 * 获取一个隐私联系人的号码处理方式
	 * 
	 * @param phoneNumber
	 * @param context
	 * @return //type:0-正常接听，1-立即挂断
	 */
	public int getPhoneModelType(String phoneNumber, Context context) {
		ContactInfoDao contactInfoDao = ContactInfoDaoUtils
				.getContactInfoDao(context);
		SQLiteDatabase ContactDatabase = contactInfoDao.getDatabase();
		Cursor query = ContactDatabase.query(ContactInfoDao.TABLENAME, null,
				ContactInfoDao.Properties.Phone_number.columnName + "=?",
				new String[] { phoneNumber }, null, null, null);
		if (query != null && query.getCount() > 0) {
			while (query.moveToNext()) {
				int type = query
						.getInt(query
								.getColumnIndex(ContactInfoDao.Properties.Type.columnName));
				return type;
			}
			query.close();
		}

		return 0;
	}

	public String[] getIntereptNumber(Context context, String modelName) {
		return parseArray(getIntereptNumbers(context, modelName));
	}

	public String[] getIntereptName(Context context, String modelName) {
		return parseArray(getIntereptNames(context, modelName));
	}

	public String[] getNotIntereptNumber(Context context, String modelName) {
		return parseArray(getNotIntereptNumbers(context, modelName));
	}

	public String[] getNotIntereptName(Context context, String modelName) {
		return parseArray(getNotIntereptNumbers(context, modelName));
	}

	public ArrayList<String> getIntereptNumbers(Context context,
			String modelName) {
		clearAll();
		getCurrentModelNumbers(context, modelName);
		return intereptNumbers;
	}

	public ArrayList<String> getIntereptNames(Context context, String modelName) {
		clearAll();
		getCurrentModelNumbers(context, modelName);
		return intereptNames;
	}

	public ArrayList<String> getNotIntereptNumbers(Context context,
			String modelName) {
		clearAll();
		getCurrentModelNumbers(context, modelName);
		return noIntereptNumbers;
	}

	public ArrayList<String> getNotIntereptNames(Context context,
			String modelName) {
		clearAll();
		getCurrentModelNumbers(context, modelName);
		return noIntereptNames;
	}

	private void clearAll() {
		if (intereptNumbers != null) {
			intereptNumbers.clear();
		}
		if (intereptNames != null) {
			intereptNames.clear();
		}
		if (noIntereptNumbers != null) {
			noIntereptNumbers.clear();
		}
		if (noIntereptNames != null) {
			noIntereptNames.clear();
		}
	}

	/**
	 * 获取情景模式下拦截联系人的号码
	 * 
	 * @param context
	 * @param modelName
	 *            :null,获取当前情景模式下的号码，not null, 获取指定model下的号码
	 * @return
	 */
	private ArrayList<String> getCurrentModelNumbers(Context context,
			String modelName) {

		// if modelName is null ,get Current Model Numbers;else get give
		// modelName Numbers;
		if (modelName == null) {
			// 1.获取当前的拦截模式
			GetModelUtils modelUtils = new GetModelUtils(context);
			String currentModel = modelUtils.getCurrentModel();
			if (currentModel == null) {
				return null;
			}else{
				modelName = currentModel;
			}
		}

		ModelDetailDao modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(context);

		SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
		Cursor modelDetailQuery = modelDetailDatabase.query(
				ModelDetailDao.TABLENAME, null, null, null, null, null, null);
		if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {
			// 根据情景模式查询出----拦截与不拦截的号码
			while (modelDetailQuery.moveToNext()) {
				String message = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));
				String address = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
				String name = modelDetailQuery
						.getString(modelDetailQuery
								.getColumnIndex(ModelDetailDao.Properties.Name.columnName));
				// JSON.toJSON(message);

				try {
					JSONObject jsonObject = new JSONObject(message);

					Integer type = jsonObject.getInt(modelName);
					if (type == 0) {
						noIntereptNames.add(name);
						noIntereptNumbers.add(address);
					} else if (type == 1) {
						intereptNames.add(name);
						intereptNumbers.add(address);
					} else {
						// currentModel not in this number
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			modelDetailQuery.close();
		}

		return null;
	}

	public static void deleteModelDetail(Context mContext,
			String[] seleteNumbers) {

		for (int i = 0; i < seleteNumbers.length; i++) {
			String number = seleteNumbers[i];
			ModelDetailDao modelDetailDao = ModelDetailDaoUtils
					.getModelDetailDao(mContext);
			SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
			int delete = modelDetailDatabase.delete(ModelDetailDao.TABLENAME,
					ModelDetailDao.Properties.Address.columnName + "=?",
					new String[] { number });
			if (delete > 0) {
				Tools.logSh("删除一个");
			}
		}
	}

	public static void saveModelDetail(Context mContext, String modelName,
			ArrayList<String> selectContactsNames,
			ArrayList<String> selectContactsNumbers, int type, boolean delete) {

		for (int i = 0; i < selectContactsNumbers.size(); i++) {
			String number = selectContactsNumbers.get(i);
			// 查询该号码是否存在于modelDetail中
			ModelDetailDao modelDetailDao = ModelDetailDaoUtils
					.getModelDetailDao(mContext);
			SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
			Cursor modelDetailQuery = modelDetailDatabase.query(
					ModelDetailDao.TABLENAME, null,
					ModelDetailDao.Properties.Address.columnName + "=?",
					new String[] { number }, null, null, null);

			// ModelDetail有数据时
			if (modelDetailQuery != null && modelDetailQuery.getCount() > 0) {

				while (modelDetailQuery.moveToNext()) {
					String num = modelDetailQuery
							.getString(modelDetailQuery
									.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
					if (number.equals(num)) {// 已有该号码的相关信息，更新
						String jsonMassage = modelDetailQuery
								.getString(modelDetailQuery
										.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));

						Long _id = modelDetailQuery
								.getLong(modelDetailQuery
										.getColumnIndex(ModelDetailDao.Properties.Id.columnName));

						ModelDetail modelDetail = new ModelDetail();
						modelDetail.setId(_id);
						modelDetail.setAddress(number);
						modelDetail.setName(selectContactsNames.get(i));
						Tools.logSh("address" + number + "::" + "message=="
								+ jsonMassage);
						try {
							// {"home":0,"company":1}
							JSONObject json = new JSONObject(jsonMassage);
							json.put(modelName, type);
							jsonMassage = json.toString();
							Tools.logSh("jsonMassage====" + jsonMassage);
						} catch (JSONException ex) {
							// 键为null或使用json不支持的数字格式(NaN, infinities)
							throw new RuntimeException(ex);
						}
						modelDetail.setMassage(jsonMassage);

						modelDetailDao.update(modelDetail);
					} else {// 未有该号码的相关信息，添加
						ModelDetail modelDetail = new ModelDetail();

						modelDetail.setAddress(number);
						String name = selectContactsNames.get(i);
						modelDetail.setName(name);

						// Json--
						String msg = "";
						try {
							// 首先最外层是{}，是创建一个对象
							JSONObject model = new JSONObject();

							// 0，不拦截；1，拦截
							model.put(modelName, type);
							msg = model.toString();
							Tools.logSh("name=" + name + ":::" + "address"
									+ number + "::" + "message==" + msg);
							/*
							 * { "家里"："0","公司":"1" }
							 */

						} catch (JSONException ex) {
							// 键为null或使用json不支持的数字格式(NaN, infinities)
							throw new RuntimeException(ex);
						}

						modelDetail.setMassage(msg);
						Tools.logSh("向modelDetail添加了一条数据");
						modelDetailDao.insert(modelDetail);
					}

				}
				modelDetailQuery.close();

			} else {
				ModelDetail modelDetail = new ModelDetail();

				modelDetail.setAddress(number);
				String name = selectContactsNames.get(i);
				modelDetail.setName(name);

				// Json--
				String msg = "";
				try {
					// 首先最外层是{}，是创建一个对象
					JSONObject model = new JSONObject();

					// 0，不拦截；1，拦截
					model.put(modelName, type);
					msg = model.toString();
					Tools.logSh("name=" + name + ":::" + "address" + number
							+ "::" + "message==" + msg);
					/*
					 * { "家里"："0","公司":"1" }
					 */

				} catch (JSONException ex) {
					// 键为null或使用json不支持的数字格式(NaN, infinities)
					throw new RuntimeException(ex);
				}

				modelDetail.setMassage(msg);
				Tools.logSh("向modelDetail添加了一条数据");
				modelDetailDao.insert(modelDetail);
				modelDetailQuery.close();
			}
		}

		// normal
		if (type == 0) {
			if (delete) {
				RecordToSysUtils recordToSysUtils = new RecordToSysUtils(
						mContext);
				recordToSysUtils.restoreContact(selectContactsNumbers,true);
			}

		} else {// interept
			if (delete) {
				RecordToUsUtils recordToUsUtils = new RecordToUsUtils(mContext);
				recordToUsUtils.removeContactRecord(selectContactsNumbers,
						delete);
			}
		}
		
		ContactUtils.modelChangeContact(mContext);//需要继续修改
	}
}
