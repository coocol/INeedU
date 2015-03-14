package com.eethan.ineedu.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TimeUtil {
	public static long getLong() throws MalformedURLException,IOException{
		  //取得资源对象
		  URL url = new URL("http://www.bjtime.cn");
		  //生成连接对象
		  URLConnection uc = url.openConnection();
		  //发出连接
		  uc.connect();
		  long time = uc.getDate();
		  return time;
		 }
}
