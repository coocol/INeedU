package com.eethan.ineedu.databasebeans;

public class City {

	private int cid;
	public City() {
	}
	private int provinceID;
	public City(int cid, int provinceID, int cityID, String city) {
		super();
		this.cid = cid;
		this.provinceID = provinceID;
		this.cityID = cityID;
		this.city = city;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getProvinceID() {
		return provinceID;
	}
	public void setProvinceID(int provinceID) {
		this.provinceID = provinceID;
	}
	public int getCityID() {
		return cityID;
	}
	public void setCityID(int cityID) {
		this.cityID = cityID;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	private int cityID;
	private String city;
}
