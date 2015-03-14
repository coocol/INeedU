package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;

public class WishFlower {
	
	public static String ID = "id";
	public static String USERID = "userId";
	public static String WISHID = "wishId";
	
	private int userId;
	private int wishId;
	
	public int getWishId() {
		return wishId;
	}
	public void setWishId(int wishId) {
		this.wishId = wishId;
	}
	@Id
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	

}
