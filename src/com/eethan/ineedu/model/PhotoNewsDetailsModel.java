package com.eethan.ineedu.model;

import com.eethan.ineedu.databasebeans.TakePhotosComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;

public class PhotoNewsDetailsModel {
	
	private TakePhotosComment takePhotosComment;
	private UserInfo userInfo;
	private UserLocation userLocation;
	public UserLocation getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(UserLocation userLocation) {
		this.userLocation = userLocation;
	}
	public TakePhotosComment getTakePhotosComment() {
		return takePhotosComment;
	}
	public void setTakePhotosComment(TakePhotosComment takePhotosComment) {
		this.takePhotosComment = takePhotosComment;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
