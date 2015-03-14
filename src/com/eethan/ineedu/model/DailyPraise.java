package com.eethan.ineedu.model;

import com.lidroid.xutils.db.annotation.Id;



public class DailyPraise {
	public static String ID = "DbId";
	public static String SENDERID = "SenderId";
	public static String BESENDERID = "beSenderId";
	
	@Id
	private int DbId;//数据库用的ID
	
	private int SenderId;
	private int beSenderId;
	
	public int getSenderId() {
		return SenderId;
	}
	public void setSenderId(int senderId) {
		SenderId = senderId;
	}
	public int getBeSenderId() {
		return beSenderId;
	}
	public void setBeSenderId(int beSenderId) {
		this.beSenderId = beSenderId;
	}
}
