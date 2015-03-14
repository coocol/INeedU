package com.eethan.ineedu.model;

import android.R.integer;

import com.eethan.ineedu.databasebeans.UserInfo;

public class FlowerFansModel {

	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public int getLastNum() {
		return lastNum;
	}
	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}
	private UserInfo userInfo;
	private int lastNum;
}
