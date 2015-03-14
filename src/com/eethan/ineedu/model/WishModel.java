package com.eethan.ineedu.model;

import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.WishWithWannaNum;

public class WishModel {
	
	private WishWithWannaNum wish;
	public WishWithWannaNum getWish() {
		return wish;
	}
	public void setWish(WishWithWannaNum wish) {
		this.wish = wish;
	}
	public UserInfo getOwnerInfo() {
		return ownerInfo;
	}
	public void setOwnerInfo(UserInfo ownerInfo) {
		this.ownerInfo = ownerInfo;
	}
	private UserInfo ownerInfo;
	private String academy;
	public String getAcademy() {
		return academy;
	}
	public void setAcademy(String academy) {
		this.academy = academy;
	}

}
