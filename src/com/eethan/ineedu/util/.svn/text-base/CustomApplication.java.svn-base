package com.eethan.ineedu.util;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;

public class CustomApplication extends Application
{
	    @Override
	    public void onCreate()
	    {
	        super.onCreate();
	        ImageLoaderConfiguration configuration = ImageLoaderConfiguration  
	                .createDefault(this);  
	        ImageLoader.getInstance().init(configuration);//全局初始化
	    }
    
}