package com.eethan.ineedu.jackson;

import com.eethan.ineedu.databasebeans.WakeUp;

public class WakeUpMatchJsonObject {
	public WakeUp wakeUp;
	public String tele;
	public int matchCondition;
	public double lng;
	public double lat;
	
	public WakeUpMatchJsonObject(WakeUp wakeUp, String tele, int matchCondition, double lng, double lat){
		this.wakeUp = wakeUp;
		this.tele =tele;
		this.matchCondition = matchCondition;
		this.lng =lng;
		this.lat = lat;
	}
	public WakeUpMatchJsonObject(){}
}
