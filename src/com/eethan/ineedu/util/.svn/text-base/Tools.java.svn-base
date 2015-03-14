package com.eethan.ineedu.util;

import android.os.Environment;
/**
 * 
 * @author MaGiga
 * Create at 2014-7-15
 */
public class Tools {
	/**
	 * ����Ƿ����SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}
