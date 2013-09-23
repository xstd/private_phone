package com.xstd.pirvatephone.module;

import java.util.List;

import android.content.Context;

public class SimulaPhone extends SimulaComm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimulaComm instance;

	@Override
	public List<SimulaComm> getSimulaCommByType(int type) {
		return null;
	}

	@Override
	public synchronized SimulaComm getInstance(Context ctx) {
		if (instance == null) {
			instance = new SimulaPhone();
		}
		return instance;
	}

}
