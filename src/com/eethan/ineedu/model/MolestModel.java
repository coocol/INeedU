package com.eethan.ineedu.model;



public class MolestModel {
	private int userId;
	private int flag;
	private String commentedUserName = "";//被评论者的姓名
	private String name;//我的姓名
	private String time;
	private String content;
	public MolestModel() {
		// TODO Auto-generated constructor stub
	}
	public MolestModel(int userId,int flag,
			String commentedUserName,String name,String time,String content) {
		// TODO Auto-generated constructor stub
		this.setUserId(userId);
		this.setCommentedUserName(commentedUserName);
		this.setName(name);
		this.setTime(time);
		this.setFlag(flag);
		this.setContent(content);
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCommentedUserName() {
		return commentedUserName;
	}
	public void setCommentedUserName(String commentedUserName) {
		this.commentedUserName = commentedUserName;
	}
}
