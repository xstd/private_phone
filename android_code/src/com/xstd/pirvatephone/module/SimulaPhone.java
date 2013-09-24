package com.xstd.pirvatephone.module;

import java.util.List;

import android.content.Context;

public class SimulaPhone extends SimulaComm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SimulaComm instance;

	@Override
	public List<SimulaComm> getSimulaCommByType() {
		return null;
	}

	public static synchronized SimulaComm getInstance(Context ctx) {
		if (instance == null) {
			instance = new SimulaPhone();
		}
		mCtx = ctx;
		return instance;
	}

}
