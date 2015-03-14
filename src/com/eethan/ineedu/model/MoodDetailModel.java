package com.eethan.ineedu.model;

import com.eethan.ineedu.databasebeans.MoodComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;

public class MoodDetailModel {
	
	private MoodComment moodComment;
	public MoodComment getMoodComment() {
		return moodComment;
	}
	public void setMoodComment(MoodComment moodComment) {
		this.moodComment = moodComment;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public UserLocation getUserLocation() {
		return userLocation;
	}
	public void setUserLocation(UserLocation userLocation) {
		this.userLocation = userLocation;
	}
	private UserInfo userInfo;
	private UserLocation userLocation;

}
