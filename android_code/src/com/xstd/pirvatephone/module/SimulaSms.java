package com.xstd.pirvatephone.module;

import java.util.List;

import android.content.Context;

public class SimulaSms extends SimulaComm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 短信模拟中的短信内容
	 */
	private String content;
	private SimulaComm instance;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public List<SimulaComm> getSimulaCommByType(int type) {
		return null;
	}

	@Override
	public synchronized SimulaComm getInstance(Context ctx) {
		if (instance == null) {
			instance = new SimulaSms();
		}
		return instance;
	}

}
