package com.eethan.ineedu.primaryactivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.util.CrashHandler;
import com.eethan.ineedu.util.SysApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;


public class BaseActivity extends Activity{
	private Context context;
	private String pageName;
	private LoadingDialog loadingDialog;
	public static DisplayImageOptions  headOptions;//显示头像的option
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;
		//禁用重力感应
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		SysApplication.getInstance().addActivity(this);
		CrashHandler crashHandler=CrashHandler.getInstance();
		crashHandler.init(this);
		loadingDialog=new LoadingDialog(this);
		
		headOptions = new DisplayImageOptions.Builder()  
	    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
	    .cacheOnDisc(true)    // 设置下载的图片是否缓存在SD卡中
	    .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
	    .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
	    .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
	    .imageScaleType(ImageScaleType.EXACTLY)
	    .build();
		
//		NetCondition.check(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SysApplication.getInstance().removeActivity(this);
	}
	
	//友盟数据统计
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(pageName); //统计页面
	    MobclickAgent.onResume(this);          //统计时长
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(pageName); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
	public void setPageName(String pageName) {
		this.pageName=pageName;
	}
	
	public void Exit()
	{
		SysApplication.getInstance().exitAll();
	}
	
	public void loadingDialogShow()
	{
		loadingDialog.show();
	}
	public void loadingDialogDismiss()
	{
		loadingDialog.dismiss();
	}
	public Context getContext()
	{
		return context;
	}
	public DisplayImageOptions getHeadDisplayOption()
	{
		return headOptions;
	}
}
