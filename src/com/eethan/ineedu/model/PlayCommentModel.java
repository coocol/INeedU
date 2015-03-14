package com.eethan.ineedu.model;

import java.util.ArrayList;

import com.eethan.ineedu.databasebeans.PlaysComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;

public class PlayCommentModel {
	
	public UserInfo getCommUserInfo() {
		return commUserInfo;
	}
	public void setCommUserInfo(UserInfo commUserInfo) {
		this.commUserInfo = commUserInfo;
	}
	public PlaysComment getPlaysComment() {
		return playsComment;
	}
	public void setPlaysComment(PlaysComment playsComment) {
		this.playsComment = playsComment;
	}
	private UserInfo commUserInfo;
	private PlaysComment playsComment;
	private UserLocation commUserLocation;
	public UserLocation getCommUserLocation() {
		return commUserLocation;
	}
	public void setCommUserLocation(UserLocation commUserLocation) {
		this.commUserLocation = commUserLocation;
	}

}
