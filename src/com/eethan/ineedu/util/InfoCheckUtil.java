package com.eethan.ineedu.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoCheckUtil {
	public static boolean isEmail(String email)
	{
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	public static boolean isPhoneNum(String phone)
	{
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,1,2,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}
}
