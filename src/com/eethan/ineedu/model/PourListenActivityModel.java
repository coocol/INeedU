package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;

public class PourListenActivityModel {
	@Id
	private int _id=-1;
	private int id;//pourlistenId
	private int ownerId;//发布者id
	private String imageUrl;
	private String content;
	private int numOfComment;
	
	private double lat;
	private double lng;
	private Long time;
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
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	private String sex;
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getNumOfComment() {
		return numOfComment;
	}
	public void setNumOfComment(int numOfComment) {
		this.numOfComment = numOfComment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int get_Id() {
		return _id;
	}
	public void set_Id(int id) {
		this._id = id;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
}
