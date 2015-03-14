package com.eethan.ineedu.model;

public class PourlistenDetailModel {
	private int userId;
	private String comment;
	private long time;
	private int commentId;
	private int commentedUserId;
	
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getLastLogTime() {
		return lastLogTime;
	}
	public void setLastLogTime(String lastLogTime) {
		this.lastLogTime = lastLogTime;
	}
	private String distance;
	private String sex;
	private String lastLogTime;
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public int getCommentedUserId() {
		return commentedUserId;
	}
	public void setCommentedUserId(int commentedUserId) {
		this.commentedUserId = commentedUserId;
	}
}
