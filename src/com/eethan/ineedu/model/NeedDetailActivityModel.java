package com.eethan.ineedu.model;



public class NeedDetailActivityModel {
	private int userId;
	private String helperName;
	private long time;
	private String content;
	private int commentId;
	private int commentedUserId;
	private String thatManName;//被评论的那个人的名字（realName)
	
	private String sex;
	private String distance;
	private String lastLogTime;
	
	public String getLastLogTime() {
		return lastLogTime;
	}
	public void setLastLogTime(String lastLogTime) {
		this.lastLogTime = lastLogTime;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	private String headPic;//暂时静态
	
	public String getHelperName() {
		return helperName;
	}
	public void setHelperName(String helperName) {
		this.helperName = helperName;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCommentedUserId() {
		return commentedUserId;
	}
	public void setCommentedUserId(int commentedUserId) {
		this.commentedUserId = commentedUserId;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getThatManName() {
		return thatManName;
	}
	public void setThatManName(String thatManName) {
		this.thatManName = thatManName;
	}
}
