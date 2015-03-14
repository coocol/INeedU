package com.eethan.ineedu.network;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
	public static int n=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		n++;
		if(n%2==0)
		{
			n=0;
			return;
		}
		Log.i("NET","网络状态更改");
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			Log.i("NET","网络连接成功");
			// 手机网络连接成功
//			ServiceManager serviceManager=new ServiceManager(context);
//			serviceManager.startService();
//			Constant.OFFLINE_MODE=false;
		} 
		else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) 
		{
			Log.i("NET","网络连接失败");
			// 手机没有任何的网络
//			ServiceManager serviceManager=new ServiceManager(context);
//			serviceManager.stopService();
//			Constant.OFFLINE_MODE=true;
		} 
		else if (wifiState != null && State.CONNECTED == wifiState) 
		{
			Log.i("NET","WIFI连接成功");
			// 无线网络连接成功
//			ServiceManager serviceManager=new ServiceManager(context);
//			serviceManager.startService();
//			Constant.OFFLINE_MODE=false;
		}

	}

}
