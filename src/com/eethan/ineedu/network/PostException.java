package com.eethan.ineedu.network;


public class PostException extends Exception{
	String msg;
	PostException(String msg)
	{
		this.msg=msg;
	}
	public String getMessage()
	{
		return msg;
	}
}
