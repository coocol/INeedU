package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;


public class MoodLike {
	
	public static String ID = "id";
	public static String USERID = "userId";
	public static String MOODID = "moodId";
	
	private int userId;
	private int moodId;
	
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
	public int getMoodId() {
		return moodId;
	}
	public void setMoodId(int moodId) {
		this.moodId = moodId;
	}
	

}
