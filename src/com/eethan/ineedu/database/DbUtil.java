package com.eethan.ineedu.database;

import android.content.Context;

import com.lidroid.xutils.DbUtils;

public class DbUtil {
	public static String DbName = "ineed";
	
	public static DbUtils getDbUtils(Context context)
	{
		return DbUtils.create(context,DbName);
	}
}
