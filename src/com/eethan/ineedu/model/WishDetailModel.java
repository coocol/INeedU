package com.eethan.ineedu.model;

import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.databasebeans.WishComment;

public class WishDetailModel {

	private UserInfo commUserInfo;
	private WishComment wishComment;
	private UserLocation commUserLocation;
	public UserLocation getCommUserLocation() {
		return commUserLocation;
	}
	public void setCommUserLocation(UserLocation commUserLocation) {
		this.commUserLocation = commUserLocation;
	}
	public UserInfo getCommUserInfo() {
		return commUserInfo;
	}
	public void setCommUserInfo(UserInfo commUserInfo) {
		this.commUserInfo = commUserInfo;
	}
	public WishComment getWishComment() {
		return wishComment;
	}
	public void setWishComment(WishComment wishComment) {
		this.wishComment = wishComment;
	}
}
