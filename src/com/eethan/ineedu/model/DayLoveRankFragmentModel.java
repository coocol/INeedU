package com.eethan.ineedu.model;

public class DayLoveRankFragmentModel {
	private int id;
	private String name;
	private int distant;
	private int loveNum;
	private String headPic;
	private int rankNum=1;
	private int love_addnum;
	private String sex;

	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLoveNum() {
		return loveNum;
	}
	public void setLoveNum(int loveNum) {
		this.loveNum = loveNum;
	}
	public int getRankNum() {
		return rankNum;
	}
	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}
	public int getDistant() {
		return distant;
	}
	public void setDistant(int distant) {
		this.distant = distant;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public int getLove_addnum() {
		return love_addnum;
	}
	public void setLove_addnum(int love_addnum) {
		this.love_addnum = love_addnum;
	}
	
}
