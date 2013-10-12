package com.xstd.pirvatephone.module;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.xstd.pirvatephone.dao.simulacomm.SimulateComm;

public abstract class SimulaComm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 要模拟的电话号码
	 */
	private long phonenumber;

	/**
	 * 将来的某一时刻
	 */
	private Date futuretime;
	protected static Context mCtx;

	protected SimulaComm() {
	}

	public long getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(long phonenumber) {
		this.phonenumber = phonenumber;
	}

	public Date getFuturetime() {
		return futuretime;
	}

	public void setFuturetime(Date futuretime) {
		this.futuretime = futuretime;
	}

	/**
	 * 通过类型得到当前类型所有
	 * 
	 * @param type
	 * @return
	 */
	public abstract List<SimulateComm> getSimulaCommByType();

}
