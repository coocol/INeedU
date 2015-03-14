package com.eethan.ineedu.model;

import com.eethan.ineedu.databasebeans.Mood;
import com.eethan.ineedu.databasebeans.Pourlisten;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;

public class MoodModel {


	private UserInfo ownerInfo;
	private UserInfo atInfo;
	private int lastNum;
	private UserDetailInfo ownerDetailInfo;
	private UserLocation userLocation;
	private int flag;
	public Mood getMood() {
		return mood;
	}
	public void setMood(Mood mood) {
		this.mood = mood;
	}
	private Mood mood;
	public UserInfo getOwnerInfo() {
		return ownerInfo;
	}
	public void setOwnerInfo(UserInfo ownerInfo) {
		this.ownerInfo = ownerInfo;
	}
	public UserInfo getAtInfo() {
		return atInfo;
	}
	public void setAtInfo(UserInfo atInfo) {
		this.atInfo = atInfo;
	}
	public int getLastNum() {
		return lastNum;
	}
	public void setLastNum(int lastNum) {
		this.lastNum = lastNum;
	}

	public UserDetailInfo getOwnerDetailInfo() {
		return ownerDetailInfo;
	}
	public void setOwnerDetailInfo(UserDetailInfo ownerDetailInfo) {
		this.ownerDetailInfo = ownerDetailInfo;
	}
	public UserLocation getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(UserLocation userLocation) {
		this.userLocation = userLocation;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	

}
