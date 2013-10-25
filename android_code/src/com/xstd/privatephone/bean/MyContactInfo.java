package com.xstd.privatephone.bean;

public class MyContactInfo {
	
	private String address;
	private String name;
	public boolean isChecked;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public MyContactInfo(String address, String name, boolean isChecked) {
		super();
		this.address = address;
		this.name = name;
		this.isChecked = isChecked;
	}
	
	public MyContactInfo(){
		super();
	}
	
}
