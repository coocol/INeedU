package com.eethan.ineedu.manager;




/**
 * 位置管理类
 */


public class LocateManager {

	
	private static double latitude;
	private static double lontitude;
	private static String city;
	
	private static LocateManager locateManager;
	
	/**
	 * 单例模式,初始化LocateManager并返回
	 * @return LocateManager
	 */
	
	public static LocateManager getInstance() {
		if(locateManager==null) 
			locateManager = new LocateManager();
		return locateManager;
	}
	
	
	public void setLatitude(double latitude) {
		LocateManager.latitude = latitude;
	}
	
	public void setLontitude(double lontitude) {
		LocateManager.lontitude = lontitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLontitude() {
		return lontitude;
	}
	
	public void setCity(String city) {
		LocateManager.city = city;
	}
	
	public String getCity() {
		return city;
	}
	
}
