package com.eethan.ineedu.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.content.Context;

import com.eethan.ineedu.CommonUse.LoginToOpenfire;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;

public class DataTraslator {

	public static final Map<Integer, Integer> LEAP_MONTH_DAYS_MAP = new HashMap<Integer, Integer>() {
		{
			put(1, 31);
			put(2, 28);
			put(3, 31);
			put(4, 30);
			put(5, 31);
			put(6, 30);
			put(7, 31);
			put(8, 31);
			put(9, 30);
			put(10, 31);
			put(11, 31);
			put(12, 30);
		}
	};

	public static final Map<Integer, Integer> NOLEAP_MONTH_DAYS_MAP = new HashMap<Integer, Integer>() {
		{
			put(1, 31);
			put(2, 29);
			put(3, 31);
			put(4, 30);
			put(5, 31);
			put(6, 30);
			put(7, 31);
			put(8, 31);
			put(9, 30);
			put(10, 31);
			put(11, 31);
			put(12, 30);
		}
	};

	public static int GetDistance(double lat_a, double lng_a, double lat_b,
			double lng_b) {
		double EARTH_RADIUS = 6378137.0;
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return (int) s;
	}

	public static int GetDistanceToMe(Context context, double lat, double lng) {
		double lat_b = new SPHelper(context).GetLat();
		double lng_b = new SPHelper(context).GetLng();
		double EARTH_RADIUS = 6378137.0;
		double radLat1 = (lat * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return (int) s;
	}

	public static String LongToTimeRemain(Long time) {
		long day, hour, min;

		day = time / Constant.DAY;
		time -= day * Constant.DAY;
		hour = time / Constant.HOUR;
		time -= hour * Constant.HOUR;
		min = time / Constant.MIN;

		if (day != 0)
			if (hour != 0)
				return day + "天零" + hour + "个小时";
			else
				return "约" + day + "天";
		if (hour != 0)
			if (min != 0)
				return hour + "小时" + min + "分钟";
			else
				return "约" + hour + "个小时";
		if (min != 0)
			return min + "分钟";
		else
			return "刚刚";

	}

	public static String LongToTimePast(long DueTime) {
		long year, month, hour, min;
		long timeNow = new Date().getTime();
		long time = timeNow - DueTime;

		time = time % Constant.DAY;
		hour = time / Constant.HOUR;
		time -= hour * Constant.HOUR;
		min = time / Constant.MIN;
		time -= min * Constant.MIN;

		String a2 = longToFormatString(DueTime, "HH时mm分");
		year = Integer.parseInt(longToFormatString(timeNow, "yyyy"))
				- Integer.parseInt(longToFormatString(DueTime, "yyyy"));

		month = Integer.parseInt(longToFormatString(timeNow, "MM"))
				- Integer.parseInt(longToFormatString(DueTime, "MM"));

		int dayDiff = Integer.parseInt(longToFormatString(timeNow, "dd"))
				- Integer.parseInt(longToFormatString(DueTime, "dd"));

		if (year != 0)
			return longToFormatString(DueTime, "yy/MM/dd HH:mm");
		if (month != 0)
			return longToFormatString(DueTime, "yy/MM/dd HH:mm");

		if (dayDiff == 0)
			if (hour != 0)
				return hour + "小时" + min + "分钟前";
			else if (hour == 0) {
				if (min == 0)
					return "刚刚";
				else
					return min + "分钟前";
			}

		if (dayDiff == 1)
			return "昨天" + a2;
		if (dayDiff == 2)
			return "前天" + a2;

		return longToFormatString(DueTime, "yy/MM/dd HH:mm");
	}

	public static String LongToTimePastGeneral(long DueTime) {
		long year, month, hour, min;
		long timeNow = new Date().getTime();
		long time = timeNow - DueTime;
		long timegap = time;
		time = time % Constant.DAY;
		hour = time / Constant.HOUR;
		time -= hour * Constant.HOUR;
		min = time / Constant.MIN;
		time -= min * Constant.MIN;

		String a2 = longToFormatString(DueTime, "HH时mm分");
		year = Integer.parseInt(longToFormatString(timeNow, "yyyy"))
				- Integer.parseInt(longToFormatString(DueTime, "yyyy"));

		month = Integer.parseInt(longToFormatString(timeNow, "MM"))
				- Integer.parseInt(longToFormatString(DueTime, "MM"));

		int dayDiff = Integer.parseInt(longToFormatString(timeNow, "dd"))
				- Integer.parseInt(longToFormatString(DueTime, "dd"));

		if (year != 0){
			String yearstr =  longToFormatString(DueTime, "yyyy年");
			if(yearstr!=null && yearstr.contains("1970"))
				return "很久以前";
			return yearstr;
		}
			
			
		if(month!=0){
			if(month==1){
				int daygap =  (int)(timegap/(1000.0f*24*60*60));
				if(daygap==1){
					return "昨天"+a2;
				}else if(daygap==2){
					return "前天"+a2;
				}else if(daygap>=3 && daygap<=30)
					return daygap+"天前";
			}else if(month>=2){
				return month+"个月前";
			}
		}
		if (dayDiff == 0)
			if (hour != 0)
				return hour + "小时前";
			else if (hour == 0) {
				if (min == 0)
					return "刚刚";
				else
					return min + "分钟前";
			}

		if (dayDiff == 1)
			return "昨天" + a2;
		if (dayDiff == 2)
			return "前天" + a2;
		if(dayDiff>2 && dayDiff<31)
			return dayDiff+"天前";

		return longToFormatString(DueTime, "yy/MM/dd HH:mm");
	}
	
	 public static boolean isLeapYear(int year) {  
		  
	        boolean isLeapYear = false;  
	        if (year % 4 == 0 && year % 100 != 0) {  
	            isLeapYear = true;  
	        } else if (year % 400 == 0) {  
	            isLeapYear = true;  
	        }  
	        return isLeapYear;  
	    }

	public static String longToFormatString(long time, String format) {
		SimpleDateFormat dateformat = new SimpleDateFormat(format); // "HH时mm分"
		String result = dateformat.format(new Date(time));
		return result;
	}

	public static String DistanceToString(int distance) {
		if (distance < 1000)
			return distance + "米";
		if (distance % 1000 == 0)
			return distance / 1000 + "千米";
		return distance / 1000 + "." + (distance % 1000) / 100 + "千米";
	}

	public static String getDateNow() {
		String date = new java.sql.Date(new Date().getTime()).toString();
		return date;
	}
}
