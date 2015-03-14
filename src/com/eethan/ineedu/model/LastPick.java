package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;

public class LastPick {

	public static String ID = "id";
	public static String USERID = "userId";
	public static String TIMESTR = "timeStr";
	
	
	@Id
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private int userId;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getTimeStr() {
		return timeStr;
	}
	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}
	private String timeStr;
	
	
}
