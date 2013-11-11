package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;

import com.xstd.pirvatephone.activity.ContextModelActivity;
import com.xstd.pirvatephone.dao.model.Model;
import com.xstd.pirvatephone.dao.model.ModelDao;
import com.xstd.pirvatephone.dao.model.ModelDaoUtils;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetail;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDao;
import com.xstd.pirvatephone.dao.modeldetail.ModelDetailDaoUtils;
import com.xstd.privatephone.bean.MyContactInfo;

public class GetModelUtils {
	private Context mContext;

	private ArrayList<Model> mModels = new ArrayList<Model>();

	public GetModelUtils(Context context) {
		this.mContext = context;
	}

	public ArrayList<Model> getModels() {
		ModelDao modelDao = ModelDaoUtils.getModelDao(mContext);
		SQLiteDatabase modelDatabase = modelDao.getDatabase();
		Cursor modelCursor = modelDatabase.query(ModelDao.TABLENAME, null,
				null, null, null, null, null);
		if (modelCursor != null && modelCursor.getCount() > 0) {
			while (modelCursor.moveToNext()) {
				String modelName = modelCursor
						.getString(modelCursor
								.getColumnIndex(ModelDao.Properties.Model_name.columnName));
				Long _id = modelCursor.getLong(modelCursor
						.getColumnIndex(ModelDao.Properties.Id.columnName));
				Integer modelType = modelCursor
						.getInt(modelCursor
								.getColumnIndex(ModelDao.Properties.Model_type.columnName));
				Model modelInfo = new Model();
				modelInfo.setModel_name(modelName);
				modelInfo.setModel_type(modelType);
				modelInfo.setId(_id);
				mModels.add(modelInfo);
			}
			modelCursor.close();
		}

		return mModels;

	}

	public String getCurrentModel() {
		getModels();

		for (Model model : mModels) {
			if (model.getModel_type() == 1) {
				return model.getModel_name();
			}
		}
		return null;
	}

	public void updateModel(String number, String modelName, int type) {
		ModelDetailDao modelDetailDao = ModelDetailDaoUtils
				.getModelDetailDao(mContext);
		SQLiteDatabase modelDetailDatabase = modelDetailDao.getDatabase();
		Cursor query = modelDetailDatabase.query(ModelDetailDao.TABLENAME,
				null, ModelDetailDao.Properties.Address.columnName + "=?",
				new String[] { number }, null, null, null);
		if (query != null && query.getCount() > 0) {
			while (query.moveToNext()) {
				Long _id = query
						.getLong(query
								.getColumnIndex(ModelDetailDao.Properties.Id.columnName));
				String address = query
						.getString(query
								.getColumnIndex(ModelDetailDao.Properties.Address.columnName));
				String name = query
						.getString(query
								.getColumnIndex(ModelDetailDao.Properties.Name.columnName));
				String massage = query
						.getString(query
								.getColumnIndex(ModelDetailDao.Properties.Massage.columnName));
				
				JSONObject json;
				try {
					json = new JSONObject(massage);
					json.put(modelName, type);
					massage = json.toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ModelDetail modelDetail = new ModelDetail();
				modelDetail.setId(_id);
				modelDetail.setAddress(address);
				modelDetail.setMassage(massage);
				modelDetail.setName(name);
				
				modelDetailDao.update(modelDetail);
			}
		}
	}

}
