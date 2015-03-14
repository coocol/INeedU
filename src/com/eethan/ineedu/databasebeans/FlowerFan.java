package com.eethan.ineedu.databasebeans;

public class FlowerFan {

	private int id;
	private int ownerid;
	
	public FlowerFan(){}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(int ownerid) {
		this.ownerid = ownerid;
	}
	public int getFansid() {
		return fansid;
	}
	public void setFansid(int fansid) {
		this.fansid = fansid;
	}
	private int fansid;
}
