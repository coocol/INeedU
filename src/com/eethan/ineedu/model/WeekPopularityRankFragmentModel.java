package com.eethan.ineedu.model;

public class WeekPopularityRankFragmentModel {

	private int id;
	private String name;
	private int distant;
	private int popularityNum;
	private String headPic;
	private int rankNum=1;
	private int popularity_addnum;
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
	public int getPopularityNum() {
		return popularityNum;
	}
	public void setPopularityNum(int popularityNum) {
		this.popularityNum = popularityNum;
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
	public int getPopularity_addnum() {
		return popularity_addnum;
	}
	public void setPopularity_addnum(int popularity_addnum) {
		this.popularity_addnum = popularity_addnum;
	}
}
