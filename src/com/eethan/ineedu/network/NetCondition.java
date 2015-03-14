package com.eethan.ineedu.network;

import com.eethan.ineedu.constant.Constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetCondition {
		public static void check(Context context)
		{
			if(isNetworkConnected(context))
			{
				Log.i("NET","有网络连接");
				Constant.OFFLINE_MODE=false;
			}
			else
			{
				Log.i("NET","无网络连接");
				Constant.OFFLINE_MODE=true;
			}
		}
		public static boolean isOnlyMobileConnected(Context context) { 
			  if(isNetworkConnected(context))
				  if(!isWifiConnected(context))
					  return true;
		      return false;
		 } 
		 public static boolean isNetworkConnected(Context context) { 
			      boolean flag=false;
				  if(isWifiConnected(context))
				    	flag=true;
				  if(isMobileConnected(context))
			    	 	flag=true;
			      return flag;
			 } 

			//判断WIFI网络是否可用
		  public static boolean isWifiConnected(Context context) { 
		      if (context != null) { 
		          ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
		                  .getSystemService(Context.CONNECTIVITY_SERVICE); 
		          NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
		                  .getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		          if (mWiFiNetworkInfo != null) { 
		              return mWiFiNetworkInfo.isAvailable(); 
		          } 
		     } 
		     return false; 
		 } 

		 

		//判断MOBILE网络是否可用

		  public static boolean isMobileConnected(Context context) { 
		      if (context != null) { 
		          ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
		                  .getSystemService(Context.CONNECTIVITY_SERVICE); 
		          NetworkInfo mMobileNetworkInfo = mConnectivityManager 
		                  .getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		          if (mMobileNetworkInfo != null) { 
		              return mMobileNetworkInfo.isAvailable(); 
		          } 
		     } 
		     return false; 
		 } 
}
