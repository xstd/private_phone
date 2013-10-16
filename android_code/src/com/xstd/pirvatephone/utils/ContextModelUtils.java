package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xstd.pirvatephone.activity.IntereptActivity;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.privatephone.tools.Tools;

public class ContextModelUtils {
	
	public static void saveModelDetail(Context context,String modelName, ArrayList<String> selectContactsNames,ArrayList<String> selectContactsNumbers, int type) {

		for (int i = 0; i < selectContactsNumbers.size(); i++) {
			String number = selectContactsNumbers.get(i);
			// 查询该号码是否存在于modelDetail中
			ModelDetailDao modelDetailDao = ModelDetailDaoUtils
					.getModelDetailDao(context);
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
							// {"home":1,"company":2}
							JSONObject json = new JSONObject(jsonMassage);
							json.put(modelName, type);
							jsonMassage = json.toString();
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

							// 1，不拦截；2，拦截
							model.put(modelName, type);
							msg = model.toString();
							Tools.logSh("name=" + name + ":::" + "address"
									+ number + "::" + "message==" + msg);
							/*
							 * { "家里"："1","公司":"2" }
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

					// 1，不拦截；2，拦截
					model.put(modelName, type);
					msg = model.toString();
					Tools.logSh("name=" + name + ":::" + "address" + number
							+ "::" + "message==" + msg);
					/*
					 * { "家里"："1","公司":"2" }
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
	}

}
