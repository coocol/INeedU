package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;


public class PlayJoin {
	
	public static String ID = "id";
	public static String USERID = "userId";
	public static String PLAYID = "playId";
	
	private int userId;
	private int playId;
	
	@Id
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPlayId() {
		return playId;
	}
	public void setPlayId(int playId) {
		this.playId = playId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	
	

}
