package com.eethan.ineedu.util;

import java.io.File;

import com.eethan.ineedu.constant.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Header {
	public static Drawable getHead(int id){
	String lfm="sdcard/DCIM/"+id+".png";
	File myfacefile=new File(lfm);
	if (myfacefile.exists()) {
		Bitmap maybit=BitmapSaver.getImageFromSDCard(lfm);
		Drawable drawa=new BitmapDrawable(maybit);
		return drawa;
	}
	return null;
	}
}
