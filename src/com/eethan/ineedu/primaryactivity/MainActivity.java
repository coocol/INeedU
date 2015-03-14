package com.eethan.ineedu.primaryactivity;


import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.LoginToOpenfire;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.fragment.MainFragment;
import com.eethan.ineedu.fragment.SlidingMenuFragment;
import com.eethan.ineedu.im.ConnectOpenfire;
import com.eethan.ineedu.im.Notice;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.manager.ServiceManager;
import com.eethan.ineedu.model.DailyPraise;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.mycontrol.MessageDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.util.CrashHandler;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.SysApplication;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class MainActivity extends SlidingFragmentActivity {

	private final String TAG="MainActivity";
	private ServiceManager serviceManager;
	//侧边栏
	private static SlidingMenu slidingMenu;
	private static final int RECONNECTOPENFIRE = 111;
	public static SlidingMenu GetSlidingMenu()
	{
		return slidingMenu;
	}
	private static Activity mainActivity;
	public static Activity getActivity()
	{
		if(mainActivity!=null)
			return mainActivity;
		return null;
	}
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CrashHandler crashHandler=CrashHandler.getInstance();
		crashHandler.init(this);
		LocateInit();
		mainActivity=MainActivity.this;
		Log.i(TAG,"onCreate");
		MobclickAgent.openActivityDurationTrack(false);
		//禁用重力感应
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		SysApplication.getInstance().addActivity(this);
		UmengUpdateAgent.update(this);
		//侧边页面容器
		setBehindContentView(R.layout.fragment_left);
		//初始化侧边栏
		initSlidingMenu();
        //数据库初始化
//        DatabaseToModel.databaseToModel=new DatabaseToModel(this);
        CreateDb();
        DailyCheck();
        //自动更新
        UmengUpdateAgent.setDeltaUpdate(false);//全量更新
        UmengUpdateAgent.update(MainActivity.this);
        UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {
			
			@Override
			public void OnDownloadUpdate(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnDownloadStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnDownloadEnd(int result, String arg1) {
				// TODO Auto-generated method stub
				switch (result) {
				case UpdateStatus.DOWNLOAD_COMPLETE_FAIL://下载失败
					MessageDialog myTakeDialog=new MessageDialog(MainActivity.this);
					myTakeDialog.setText("十分抱歉,下载失败...请尝试到官网http://www.eethan.com/ 下载最新版");
					myTakeDialog.show();
					break;
				case UpdateStatus.DOWNLOAD_COMPLETE_SUCCESS://下载失败
					
					break;
				default:
					break;
				}
			}
		});
        
        serviceManager = new ServiceManager(MainActivity.this);
        serviceManager.startService();
//        if(getIntent()!=null && getIntent().getStringExtra("mode")!=null && getIntent().getStringExtra("mode").equals("reg")){
//        	new ConnectToOpenfire(MainActivity.this,Constant.REGISTER_MODE).execute();
//        }else {
//        	new ConnectToOpenfire(MainActivity.this,Constant.LOGIN_MODE).execute();
//		}
        
        new LoginToOpenfire(MainActivity.this).execute();
        int userId = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
        new AddLoveNum(MainActivity.this, 1, userId).execute();
	}
	
	private void CreateDb() {
		// TODO Auto-generated method stub
		DbUtils db = DbUtils.create(MainActivity.this);
		try {
			db.save(new NeedFragmentModel());
			db.save(new Notice());
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void LocateInit() {
		// TODO Auto-generated method stub
		 //初始化地理位置
		SharedPreferences setting = this.getSharedPreferences(
  				Constant.INEEDUSPR, Context.MODE_PRIVATE);
  		String lat = setting.getString(Constant.LAT, Constant.DEFAULT_LAT);
  		String lng = setting.getString(Constant.LNG, Constant.DEFAULT_LNG);
  		
  		LocateManager.getInstance().setLatitude(Double.parseDouble(lat));
  		LocateManager.getInstance().setLontitude(Double.parseDouble(lng));
	}
	public void DailyCheck()
	{
		SPHelper spHelper=new SPHelper(MainActivity.this);
		String day=(String) spHelper.get(Constant.DAILY_LOVE);
		String today=DataTraslator.longToFormatString(new Date().getTime(), "yyyy-MM-dd");
		if(day==null||day.equals(""))//老用户
		{
			spHelper.put(Constant.DAILY_LOVE,today);
			return;
		}
		if(day.equals(today))//一天之内
		{
			return;
		}
		else//新的一天
		{
			//清空每日送爱心表
			try {
				DbUtil.getDbUtils(MainActivity.this).deleteAll(DailyPraise.class);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//每日匹配次数的清空
			new SPHelper(MainActivity.this).put(SPHelper.MATCH_TIME, 0);
			return;
		}
	}
	//初始化侧边栏
	private void initSlidingMenu(){
		 
        // customize the SlidingMenu
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);//设置左右都可以划出SlidingMenu菜单
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu划出时主页面显示的剩余宽度
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        //设置 SlidingMenu 内容
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.left_menu, new SlidingMenuFragment());
        fragmentTransaction.replace(R.id.main, new MainFragment());
      
        fragmentTransaction.commit();
	}
	
	//重写返回键
	 long waitTime = 2000;  
	 long touchTime = 0;  
	   
	 @Override  
	 public boolean onKeyDown(int keyCode, KeyEvent event) {  
	     if(event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {  
	         long currentTime = System.currentTimeMillis();  
	         if((currentTime-touchTime)>=waitTime) {  
	             
	             MyToast.showToast(this, "再按一次退出~", false);
	             touchTime = currentTime;  
	         }else { 
	        	 
	     			new Thread(new Runnable() {
	     				@Override
	     				public void run() {
	     					// TODO Auto-generated method stub
	     					if(XmppConnection.getConnection().isConnected())
	     						XmppConnection.closeConnection();
	     				}
	     			}).start();
	        	 
	        	 android.os.Process  
                 .killProcess(android.os.Process  
                         .myPid());  
	             finish();  
	         }  
	         return true;  
	     }  
	     return super.onKeyDown(keyCode, event);  
	 }  
//	 @Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		//未下线回到软件时重新登录Openfire
//		Log.i(TAG,"MainActivityOnResume");
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if(!XmppConnection.getConnection().isConnected())//未在线
//				{
//					Log.i(TAG,"Not Connected");
//					Message message = handler.obtainMessage();
//					message.what = RECONNECTOPENFIRE;
//					message.sendToTarget();
//				}
//				else
//				{
//					if(XmppConnection.getConnection().isAuthenticated())//是否授权登录
//					{
//						Log.i(TAG,"Has Login");
//						Presence presence = new Presence(Presence.Type.available);
//						XmppConnection.getConnection().sendPacket(presence);
//					}
//					else{
//						Log.i(TAG,"Start Login");
//						Context context = MainActivity.this;
//						String accounts = new SPHelper(context).GetUserId()+"";
//						String password=(String) new SPHelper(context).get(Constant.PASSWORD);
//						
//						try {
//							XmppConnection.getConnection().login(accounts, password);
//						} catch (XMPPException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						// 连接服务器成功，更改在线状态
//						Presence presence = new Presence(Presence.Type.available);
//						XmppConnection.getConnection().sendPacket(presence);
//					}
//					
//				}
//			}
//		}).start();
//		
//		Log.i(TAG,"onResume");
//		MobclickAgent.onResume(this);       //统计时长
//	}
	 @SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		 @Override
		 public void handleMessage(android.os.Message msg) {
			 switch (msg.what) {
			case RECONNECTOPENFIRE:
				Log.i(TAG,"Start Connect");
				new ConnectOpenfire(MainActivity.this, Constant.LOGIN_MODE).execute();
				break;

			default:
				break;
			}
		 };
	 };
	 //生命周期测试
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		 Log.i(TAG,"onPause");
		super.onPause();
		MobclickAgent.onPause(this);
	}
	 @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG,"onDestroy");
	}
	 @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onStart");
	}
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG,"onStop");
	}
	 
	 
	 
}
