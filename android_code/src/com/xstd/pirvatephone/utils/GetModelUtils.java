package com.xstd.pirvatephone.utils;

import java.util.ArrayList;

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
import com.xstd.privatephone.bean.MyContactInfo;

public class GetModelUtils {
	private Context mContext;

	private ArrayList<Model> mModels = new ArrayList<Model>();

	public GetModelUtils(Context context) {
		this.mContext = context;
	}

	public ArrayList<Model> getModels() {
		ModelDao modelDao = ModelDaoUtils
				.getModelDao(mContext);
		SQLiteDatabase modelDatabase = modelDao.getDatabase();
		Cursor modelCursor = modelDatabase.query(ModelDao.TABLENAME, null, null, null,
				null, null, null);
		if(modelCursor!=null && modelCursor.getCount()>0){
			while(modelCursor.moveToNext()){
				String modelName = modelCursor.getString(modelCursor.getColumnIndex(ModelDao.Properties.Model_name.columnName));
				Long _id = modelCursor.getLong(modelCursor.getColumnIndex(ModelDao.Properties.Id.columnName));
				Integer modelType = modelCursor.getInt(modelCursor.getColumnIndex(ModelDao.Properties.Model_type.columnName));
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
	
	public String getCurrentModel(){
		getModels();
		
		for (Model model : mModels) {
			if(model.getModel_type()==1){
				return model.getModel_name();
			}
		}
		return null;
	}

}
