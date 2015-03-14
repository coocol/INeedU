package com.eethan.ineedu.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class ChineseToPinyin {
	 private static String pinyinName = "";
	 public static String HanyuToPinyin(String name){
	        char[] nameChar = name.toCharArray();
	        HanyuPinyinOutputFormat defaultFormat = 
	                                           new HanyuPinyinOutputFormat();
	        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        for (int i = 0; i < nameChar.length; i++) {
	            if (nameChar[i] > 128) {
	                try {
	                    pinyinName += PinyinHelper.toHanyuPinyinStringArray
	                                           (nameChar[i], defaultFormat)[0];
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            } 
	            else
	            	pinyinName+=nameChar[i];
	        }
	        return pinyinName;
	    }
}
