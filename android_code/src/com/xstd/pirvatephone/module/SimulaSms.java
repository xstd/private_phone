package com.xstd.pirvatephone.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.xstd.pirvatephone.activity.SimulaCommActivity;
import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;
import com.xstd.pirvatephone.dao.simulacomm.SimulateCommDao;
import com.xstd.pirvatephone.dao.simulacomm.SimulateDaoUtils;

public class SimulaSms extends SimulaComm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = "SimulaSms";
	/**
	 * 短信模拟中的短信内容
	 */
	private String content;
	private static SimulaComm instance;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public List<SimulateComm> getSimulaCommByType() {
		SimulateCommDao dao = SimulateDaoUtils.getSimulateDao(mCtx);
		Cursor cursor = dao.getDatabase().query(dao.getTablename(),
				dao.getAllColumns(), SimulateCommDao.Properties.Type.columnName + "=?",
				new String[] { String.valueOf(SimulaCommActivity.SIMULA_SMS) },
				null, null, null);
		List<SimulateComm> results = new ArrayList<SimulateComm>();
		if(cursor!=null) {
			while(cursor.moveToNext()) {
				SimulateComm comm = new SimulateComm();
				comm.setId(cursor.getLong(cursor.getColumnIndex(SimulateCommDao.Properties.Id.columnName)));
				comm.setPhonenumber(cursor.getLong(cursor.getColumnIndex(SimulateCommDao.Properties.Phonenumber.columnName)));
				comm.setFuturetime(new Date(cursor.getLong(cursor.getColumnIndex(SimulateCommDao.Properties.Futuretime.columnName))));
				comm.setContemt(cursor.getString(cursor.getColumnIndex(SimulateCommDao.Properties.Contemt.columnName)));
				comm.setType(cursor.getInt(cursor.getColumnIndex(SimulateCommDao.Properties.Type.columnName)));
				results.add(comm);
			}
		}
		return results;
	}

	public static synchronized SimulaComm getInstance(Context ctx) {
		if (instance == null) {
			instance = new SimulaSms();
		}
		mCtx = ctx;
		return instance;
	}

}
