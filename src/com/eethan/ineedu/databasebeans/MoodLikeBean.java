package com.eethan.ineedu.databasebeans;

public class MoodLikeBean {
	public int id;
	public int moodId;
	public int userId;
	public int praisedUserId;
	
	public int getMoodId() {
		return moodId;
	}
	public void setMoodId(int moodId) {
		this.moodId = moodId;
	}
	public MoodLikeBean(int id,int moodId,int userId,int praisedUserId)
	{
		this.id = id;
		this.moodId = moodId;
		this.userId = userId;
		this.praisedUserId = praisedUserId;
	}
	public MoodLikeBean(int moodId,int userId,int praisedUserId)
	{
		this.moodId = moodId;
		this.userId = userId;
		this.praisedUserId = praisedUserId;
	}
	public MoodLikeBean(){}
	
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
	public int getPraisedUserId() {
		return praisedUserId;
	}
	public void setPraisedUserId(int praisedUserId) {
		this.praisedUserId = praisedUserId;
	}
}
