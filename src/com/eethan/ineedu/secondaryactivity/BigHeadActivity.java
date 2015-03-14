package com.eethan.ineedu.secondaryactivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.polites.android.GestureImageView;

public class BigHeadActivity extends BaseActivity{
	GestureImageView bigHeadpic;
	private int userId;
	private DisplayImageOptions options;
	private ImageLoader imageLoader=ImageLoader.getInstance();
	LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("BigHeadActivity");
	  	requestWindowFeature(Window.FEATURE_NO_TITLE);
	  	setContentView(R.layout.standard_image);
		loadingDialog=new LoadingDialog(this);
		userId=getIntent().getExtras().getInt("userId");
		
		options = new DisplayImageOptions.Builder()  
        .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
        //.displayer(new RoundedBitmapDisplayer(100))  // 设置成圆角图片  
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build(); 
		
		
		bigHeadpic=(GestureImageView)findViewById(R.id.image);
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL
				+userId+".png", bigHeadpic,options,
				new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						loadingDialog.show();
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
					}
				});
	}
}
