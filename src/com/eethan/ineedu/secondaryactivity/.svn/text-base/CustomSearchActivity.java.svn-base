package com.eethan.ineedu.secondaryactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import u.aly.m;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.NetCondition;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.IssuePourListenActivity;
import com.eethan.ineedu.primaryactivity.PourListenBackgroundActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.BitmapSaver;
import com.eethan.ineedu.util.GetWebImgUtil;
import com.eethan.ineedu.util.UploadHelper;

public class CustomSearchActivity extends BaseActivity{
	EditText searchContent;
	Button searchButton;
	ImageView local;
	ImageView camera;
	ImageButton back;
	MyTakeDialog myTakeDialog;
	public static final int SEARCH_SUCCESS=1;
	public static final int SEARCH_FAILED=0;

	File file;
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int RESULT_REQUEST_CODE = 2;
	public final static int CAMERA_RESULT = 6666;
	private File mPhotoFile;
	private String imageName;
	private String mPhotoPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("CustomSearchActivity");
		setContentView(R.layout.activity_custom_search);
		findView();
	}
	private void findView() {
		// TODO Auto-generated method stub
		searchButton=(Button)findViewById(R.id.search_ok_button);
		searchContent=(EditText)findViewById(R.id.search_content);
		local=(ImageView)findViewById(R.id.custom_search_local);
		back=(ImageButton)findViewById(R.id.imgbt_back);
		camera=(ImageView)findViewById(R.id.custom_search_camera);
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(searchContent.getText().toString().trim().equals(""))
				{
					searchContent.setError(Html.fromHtml("<font color=#E10979>搜索内容不能为空</font>"));
					return;
				}
				if(!NetCondition.isNetworkConnected(getContext()))
				{
					MyToast.showToast(getContext(), "无网络连接");
					return;
				}
				else{
					if(!NetCondition.isWifiConnected(getContext()))//3G状态下提醒是否继续
					{
						 myTakeDialog=new MyTakeDialog(getContext()){
							@Override
							public void onYesButtonClick() {
								myTakeDialog.dismiss();
								loadingDialogShow();
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										PourListenBackgroundActivity.imageUrls.clear();
										ArrayList<String> imageUrls
											=GetWebImgUtil.getByKeyword(searchContent.getText().toString());
										PourListenBackgroundActivity.imageUrls=imageUrls;
										
										Message message=myhandHandler.obtainMessage();
										message.what=SEARCH_SUCCESS;
										message.sendToTarget();
									}
								}).start();
							};
						};
						myTakeDialog.setText("搜索图片消耗流量较多,确认继续?");
						myTakeDialog.show();
						return;
					}
					else{
						loadingDialogShow();
						
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PourListenBackgroundActivity.imageUrls.clear();
								ArrayList<String> imageUrls
									=GetWebImgUtil.getByKeyword(searchContent.getText().toString());
								PourListenBackgroundActivity.imageUrls=imageUrls;
								
								Message message=myhandHandler.obtainMessage();
								message.what=SEARCH_SUCCESS;
								message.sendToTarget();
							}
						}).start();
					}
				}
				
			}
		});
		local.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentFromGallery = new Intent();
				intentFromGallery.setType("image/*"); // 设置文件类型
				intentFromGallery
						.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentFromGallery,
						0);
			}
		});
		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					      imageName=new Date().getTime()+"";
					      mPhotoPath = "mnt/sdcard/DCIM/Camera/" + imageName + ".png";
					      mPhotoFile = new File(mPhotoPath);
					      if (!mPhotoFile.exists()) {
					    	  		mPhotoFile.createNewFile();
					       	}
					    intent.putExtra(MediaStore.EXTRA_OUTPUT,
					             Uri.fromFile(mPhotoFile));
					          startActivityForResult(intent, CAMERA_RESULT);
				      } catch (Exception e) {
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	@SuppressLint("HandlerLeak")
	Handler myhandHandler=new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SEARCH_SUCCESS:
				loadingDialogDismiss();
				Intent intent=new Intent(CustomSearchActivity.this,PourListenBackgroundActivity.class);
				intent.putExtra("isCustom", true);
				startActivity(intent);
				break;
			case SEARCH_FAILED:
				
				break;
			default:
				break;
			}
			
		};
	};
	
	/**
	 * onActvityResult method
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					try {
						getImageToView(data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case CAMERA_RESULT:
				BitmapFactory.Options options = new BitmapFactory.Options();    
				options.inSampleSize=4;//图片高宽度都为本来的二分之一，即图片大小为本来的大小的四分之一    
				options.inTempStorage = new byte[5*1024]; //设置16MB的姑且存储空间（不过感化还没看出来，待验证）
				 Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, options);
				 bitmap=BitmapSaver.imageZoom(bitmap, 1000);
				 
//				try {
//					BitmapSaver.saveImage(bitmap,mPhotoPath, imageName+".png");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				 
				Intent intent=new Intent();
				intent.putExtra("isLocal", true);
				intent.putExtra("imageUrl", imageName);
				int num=3;
				intent.putExtra("exitPageNum",num);
				intent.setClass(CustomSearchActivity.this, IssuePourListenActivity.class);
				startActivity(intent);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
	 /**
	 * 保存裁剪之后的图片数据为文件并上传、更改
	 * 
	 * @param picdata
	 * @throws Exception 
	 */
private void getImageToView(Intent data) throws Exception {
	Bundle extras = data.getExtras();
	if (extras != null) {
		Bitmap photo = extras.getParcelable("data");
		String imageName=new Date().getTime()+"";
//		UploadHelper.uploadCustomBg(photo, imageName);
		BitmapSaver.saveImage(photo, Constant.PHOTO_PATH,imageName+".png");
		Intent intent=new Intent();
//		intent.putExtra("imageUrl", URLConstant.CUSTOM_BG_FOLDER+imageName+".png");
		intent.putExtra("isLocal", true);
		intent.putExtra("imageUrl", imageName);
		int num=3;
		intent.putExtra("exitPageNum",num);
		intent.setClass(CustomSearchActivity.this, IssuePourListenActivity.class);
		startActivity(intent);
	}
}
}
