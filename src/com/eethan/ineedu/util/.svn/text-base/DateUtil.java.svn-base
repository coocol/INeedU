package com.eethan.ineedu.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import com.eethan.ineedu.constant.Constant;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	
	private static SimpleDateFormat formatBuilder;

	public static String getDate(String format) {
		formatBuilder = new SimpleDateFormat(format);
		return formatBuilder.format(new Date());
	}

	public static String getDate() {
		return getDate("MM-dd  HH:mm");
	}
	
	public static long getMSTime() {
		Date now = new Date();
		return now.getTime();
	}
	
	public static String getStringTime(){
		Date now = new Date();
		return String.valueOf(now.getTime());
	}
	
	public static String getDateByStringTime(String time){
		Date date = new Date(Long.parseLong(time));
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH:mm");
		return sdf.format(date);
	}
	
	public static String getDateByMSTime(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.TIME_FORMART);
		return sdf.format(date);
	}
	
	
}
