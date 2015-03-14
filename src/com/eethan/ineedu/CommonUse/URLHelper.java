package com.eethan.ineedu.CommonUse;

import com.eethan.ineedu.constant.URLConstant;

public class URLHelper {
	public static String getHeadUrl(int userId)
	{
		return URLConstant.BIG_HEAD_PIC_URL+userId+".png";
	}
}
