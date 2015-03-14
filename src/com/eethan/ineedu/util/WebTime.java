package com.eethan.ineedu.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class WebTime {
	public static Long getTime()
	{
		try {
			URL url=new URL("http://www.bjtime.cn");//取得资源对象
	        URLConnection uc=url.openConnection();//生成连接对象
	        uc.connect(); //发出连接
	        long ld=uc.getDate(); //取得网站日期时间
			return ld;
		} catch (Exception e) {
			return new Date().getTime();
		}
		
	}
}
