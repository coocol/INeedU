package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;

public class LastWrite {

	public static String ID = "id";
	public static String USERID = "userId";
	public static String NUM = "num";
	public static String TIMESTR = "timeStr";
	public static String TYPE = "hope";
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
	private String hope;
	public String getHope() {
		return hope;
	}
	public void setHope(String hope) {
		this.hope = hope;
	}
	private int num;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
