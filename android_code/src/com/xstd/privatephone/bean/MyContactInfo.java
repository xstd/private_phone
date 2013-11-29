package com.xstd.privatephone.bean;

public class MyContactInfo {
	
	private String address;
	private String name;
	private Long photoId;
	private Long contactId;
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	public boolean isChecked;
	
	public Long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}
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
	public MyContactInfo(String address, String name, boolean isChecked, Long photoId,Long contactId) {
		super();
		this.address = address;
		this.name = name;
		this.photoId = photoId;
		this.contactId = contactId;
		this.isChecked = isChecked;
	}
	public MyContactInfo(){
		super();
	}
	
}
