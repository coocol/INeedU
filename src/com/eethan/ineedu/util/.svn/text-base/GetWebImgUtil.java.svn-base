package com.eethan.ineedu.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetWebImgUtil {
	 static String string;
	 static URL url;
	 static BufferedReader reader;
	 static ArrayList<String> imageUrls=new ArrayList<String>();
	
	 public static ArrayList<String> getByKeyword(String keyword)
	 {
		 keyword=ChineseToPinyin.HanyuToPinyin(keyword);
		 imageUrls.clear();
		 string="";
		 reader=null;
		 final String url = "http://image.baidu.com/i?tn=baiduimage&word="+keyword+"&ie=utf-8";   //网站路径
		 readPage(url);
		 return imageUrls;
		 
	 }
	 public static ArrayList<String> readPage(String uu) {
		  String line;
		  try {
		   url = new URL(uu);
		   reader = new BufferedReader(new InputStreamReader(url.openStream()));
		   
		  int l=0;
		  while ((line = reader.readLine()) != null) {
			  l++;
			 if(l>110&&l<1900)
		    {string+=line;}
		   }
		  return process();
		 } catch (Exception ie) {
		   
		  } finally {
		   try {
		    if (reader != null)
		     reader.close();
		   } catch (Exception e) {
		   }
		  }
		  return null;
		 }

		 public static ArrayList<String> process() {
			
			 String regEx = "\"thumbURL\":\".*?\",";  
			 Pattern pat = Pattern.compile(regEx);  
			 Matcher mat = pat.matcher(string);  
			 while(mat.find())
			 {
				 String str=mat.group(0);
				 //去掉多余的符号
				 String s1=str.replace("\"thumbURL\":\"", "");
				 String s2=s1.replace("\",", "");
				 imageUrls.add(s2);
			 }
			 return imageUrls;
		 }
}
