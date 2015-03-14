package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;





public class NeedFragmentModel {
	@Id
	private int id;//数据库用的ID
	private int needId;
	private int userId;
	private String nickname;
	private String sex;
	private String content;
	private String cademy;
	public String getCademy() {
		return cademy;
	}
	public void setCademy(String cademy) {
		this.cademy = cademy;
	}
	private String reward;
	private String helperName;
	private String picLocation;
	private int loveNum;
	private int popularityNum;
	private int solveId;
	private int type;
	private int commentNum;
	private long time;
	private long timeLimit;
	private double lat;
	private double lng;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public NeedFragmentModel()
	{
		
	}
	//picLocation暂时不加进去
	public NeedFragmentModel( 
	 String nickname,
	 String sex,
	 String content,
	 String reward,
	 String helperName,
	 String cadamy,
	 int id,
	 int userId,
	 int loveNum,
	 int popularityNum,
	 int solveId,
	 int type,
	 int commentNum,
	 long time,
	 long timeLimit)
	{
		this.nickname=nickname;
		this.sex=sex;
		this.content=content;
		this.reward=reward;
		this.helperName=helperName;
		this.needId=id;
		this.userId=userId;
		this.cademy = cadamy;
		this.loveNum=loveNum;
		this.popularityNum=popularityNum;
		this.solveId=solveId;
		this.type=type;
		this.commentNum=commentNum;
		this.time=time;
		this.timeLimit=timeLimit;
	}
	public String getPicLocation() {
		return picLocation;
	}
	public void setPicLocation(String picLocation) {
		this.picLocation = picLocation;
	}


	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getLoveNum() {
		return loveNum;
	}
	public void setLoveNum(int loveNum) {
		this.loveNum = loveNum;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getHelperName() {
		return helperName;
	}
	public void setHelperName(String helperName) {
		this.helperName = helperName;
	}
	public int getSolveId() {
		return solveId;
	}
	public void setSolveId(int solveId) {
		this.solveId = solveId;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	public int getPopularityNum() {
		return popularityNum;
	}
	public void setPopularityNum(int popularityNum) {
		this.popularityNum = popularityNum;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}
	public int getId() {
		return needId;
	}
	public void setId(int id) {
		this.needId = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
