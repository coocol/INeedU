package com.eethan.ineedu.primaryactivity;

import java.io.File;
import java.util.Date;








import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Mood;
import com.eethan.ineedu.databasebeans.Pourlisten;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.BitmapSaver;
import com.eethan.ineedu.util.SysApplication;
import com.eethan.ineedu.util.UploadHelper;
import com.eethan.ineedu.util.WebTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class IssuePourListenActivity extends BaseActivity{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ImageView background;
	ImageView back;
	TextView finish;
	EditText contentEditText;
	
	public final int ISSUE_OK=1;
	public final int ISSUE_FAILED=0;
	String imageUrl;//图片的URL
	int exitPageNum;//issue之后需要finish的Activity数目
	boolean isLocal=false;//使用网络URL显示为背景还是使用本地图片
	Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("IssuePourlistenActivity");
		Init();
		SetListener();
	}
	private void Init() {
		// TODO Auto-generated method stub
		setContentView(R.layout.pour_listen_issue_page);
		
        
        exitPageNum=this.getIntent().getExtras().getInt("exitPageNum");
        imageUrl=this.getIntent().getExtras().getString("imageUrl");
        isLocal=this.getIntent().getExtras().getBoolean("isLocal");
        
		background=(ImageView)findViewById(R.id.pour_listen_issue_page_background);
		
		back=(ImageView)findViewById(R.id.pour_listen_issue_page_back_button);
		finish=(TextView)findViewById(R.id.pour_listen_issue_page_tv_finish);
		contentEditText=(EditText)findViewById(R.id.pour_listen_issue_page_content);
		if(isLocal)
		{
			BitmapFactory.Options options = new BitmapFactory.Options();    
			options.inSampleSize=4;//图片高宽度都为本来的二分之一，即图片大小为本来的大小的四分之一    
			options.inTempStorage = new byte[5*1024]; //设置16MB的姑且存储空间（不过感化还没看出来，待验证）
			bitmap=BitmapFactory.decodeFile(Constant.PHOTO_PATH+imageUrl+".png", options);
			bitmap=BitmapSaver.imageZoom(bitmap, 1000);
//			bitmap=BitmapSaver.getImageFromSDCard(Constant.PHOTO_PATH+imageUrl+".png");
			background.setImageBitmap(bitmap);
		}else
			imageLoader.displayImage(imageUrl, background,options);

	}
	
	private void SetListener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				deletePhoto();
				finish();
			}
		});
		
		
		
		
		finish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				String content=contentEditText.getText().toString().trim();
				if(content.equals(""))
				{
					contentEditText.setError(Html.fromHtml("<font color=#E10979>倾诉内容不能为空!</font>"));
					return;
				}
				loadingDialogShow();
				if(isLocal)
					UploadHelper.uploadCustomBg(bitmap, imageUrl);
				new Thread(new Runnable() {
					@Override
					public void run() {
						//发送到服务器端
						Pourlisten pourlisten=new Pourlisten();
						int userId,commentNum=0;
						String content;
						long time;
					
						
						Date date=new Date();
						//time=date.getTime();
						try {
							time=WebTime.getTime();
						} catch (Exception e1) {
							time=date.getTime();
						}
						content=contentEditText.getText().toString();
						SharedPreferences settings=getSharedPreferences(Constant.INEEDUSPR, 0);
						userId=settings.getInt(Constant.ID, 0);
						
						Mood mood = new Mood();
						mood.setCommentNum(commentNum);
						mood.setTime(String.valueOf(time));
						mood.setContent(content);
						mood.setUserId(userId);
						mood.setFlag(Constant.MOOD_FLAG_POUR);
						
						pourlisten.setCommentNum(commentNum);
						pourlisten.setTime(time);
						pourlisten.setContent(content);
						pourlisten.setUserId(userId);
						if(isLocal)
							mood.setImageUrl(URLConstant.CUSTOM_BG_FOLDER+imageUrl+".png");
							//pourlisten.setImageUrl(URLConstant.CUSTOM_BG_FOLDER+imageUrl+".png");
						else
							//pourlisten.setImageUrl(imageUrl);
							mood.setImageUrl(imageUrl);
						double lat=LocateManager.getInstance().getLatitude();
						double lng=LocateManager.getInstance().getLontitude();
						mood.setLat(lat);
						mood.setLng(lng);
						pourlisten.setLng(lat);
						pourlisten.setLat(lng);

						String result;
						try {
							result = ServerCommunication.request(mood, URLConstant.COMMIT_MOOD_URL);
						} catch (PostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message msg=finishHandler.obtainMessage();
							msg.what=Constant.CONNECT_ERROR;
							msg.obj=e.getMessage();
							msg.sendToTarget();
							return;
						}
						
						Message msg=finishHandler.obtainMessage();
						if(result!=null && result.equals("true"))
							msg.what=ISSUE_OK;
						else
							msg.what=ISSUE_FAILED;
						msg.sendToTarget();
						
					}
				}).start();
			}
		});
		
	}
	
	@SuppressLint("HandlerLeak")
	private Handler finishHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ISSUE_OK:
				int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
				new AddLoveNum(IssuePourListenActivity.this, 2, userid).execute();
				PourListenActivity.isRefresh=true;
				for(int i=0;i<exitPageNum;i++)
					SysApplication.getInstance().exitActivity();
				break;
			case ISSUE_FAILED:
				MyToast.showToast(IssuePourListenActivity.this, "发布失败，请重试", true);
				
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(IssuePourListenActivity.this, (String)msg.obj, true);
				break;
			default:
				break;
			}
			loadingDialogDismiss();
			
		};
	};
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		deletePhoto();
	};
	private void deletePhoto()
	{
		if(isLocal)//不上传就删除
		{
			File file=new File(Constant.PHOTO_PATH+imageUrl+".png");
			file.delete();
		}
	}
}
