package com.eethan.ineedu.service;

import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.WebTime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * 定位服务
 *
 */
public class LocateService extends Service{

	
	private LocationClient mLocationClient = null;
	private LocateManager locateManager = null;
	
	private SharedPreferences iNeedUSharedPreferences = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		iNeedUSharedPreferences = getSharedPreferences(Constant.INEEDUSPR, Context.MODE_PRIVATE);
		
		locateManager = LocateManager.getInstance();
		
		mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类  

		mLocationClient.setAK("79zMvAA1vPhq5nx55O2q91w9");

		mLocationClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null ) { 
                    return; 
                }  
				if(location.getLatitude()<1)
					return;
				Log.i("BaiDu","ReceiveRequest");
                locateManager.setLatitude(location.getLatitude());
                locateManager.setLontitude(location.getLongitude());
                location.getRadius(); 

				SharedPreferences sharedPreferences = getSharedPreferences(Constant.INEEDUSPR, 0);
				final int userId = sharedPreferences.getInt(Constant.ID, -1);
                new Thread(new Runnable() {
					public void run() {
						UserLocation userLocation = new UserLocation();
						userLocation.setUserId(userId);
						userLocation.setLng(locateManager.getLontitude());
						userLocation.setLat(locateManager.getLatitude());
						try {
							userLocation.setTime(WebTime.getTime());
						} catch (Exception e) {
							userLocation.setTime(new Date().getTime());
						}
						try {
							String result = ServerCommunication.request(userLocation, URLConstant.UPDATE_LOCATION_URL);
						} catch (PostException e) {
							e.printStackTrace();
							return;
						}
					}
				}).start();
                
                SharedPreferences.Editor editor = iNeedUSharedPreferences.edit();
                editor.putString(Constant.LAT,location.getLatitude()+"")
                .putString(Constant.LNG,location.getLongitude()+"");
                editor.commit();
			}
			
			@Override
			public void onReceivePoi(BDLocation arg0) {
			}
		});
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(Constant.CHECK_DELAY*10);//设置发起定位请求的间隔时间为60000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(1);    //最多返回POI个数   
		option.setPoiDistance(1000); //poi查询距离       
		option.setPoiExtraInfo(false); //是否需要POI的电话和地址等详细信息        
		mLocationClient.setLocOption(option);
		mLocationClient.start(); 
        /* 
         *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
         *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
         *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
         *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
         *定时定位时，调用一次requestLocation，会定时监听到定位结果。
         */ 
		if (mLocationClient != null && mLocationClient.isStarted())  
			mLocationClient.requestLocation();
	}
	
}
