package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.adapter.NeedDetailActivityAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Need;
import com.eethan.ineedu.databasebeans.NeedComment;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.DetailNeedJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.NeedDetailActivityModel;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.CustomerHttpClient;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.ToastHandler;
import com.eethan.ineedu.util.UploadHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.scrshot.UMScrShotController.OnScreenshotListener;
import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;


public class NeedDetailActivity extends BaseActivity{
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	// 声明mShakeController, 参数1为sdk 控制器描述符
	UMShakeService mShakeController = UMShakeServiceFactory
	                              .getShakeService("write.your.content");
	
	private ImageButton headPic;//暂时使用静态
	private ImageView sexPic;
	private TextView loveNumTextView;
	private TextView nicknameTextView;
	private TextView contentTextView;
	private ImageView state_button;
	private TextView rewardTextView;
	private TextView timeTextView;
	private TextView timeRemainTextView;
	
	private LinearLayout helperLinearLayout;//Helper所在的Linearlayout，未解决时不显示
	private TextView helperNameTextView;
	private ImageButton helperHeadPic;
	
	private TextView popularityNumTextView;
	private TextView commentNumTextView;
	private TextView deleteTextView;
	
	private ScrollListView detailListView;
	private ImageButton giveHeartButton; 
	private ImageButton messageButton;//私信
	private ImageButton commentButton;
	private ImageButton backButton;
	
	private TextView distanceTxtVu;
	private TextView sendtimeTxtVu;
	
	private TextView needNumTxtVu;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ScaleGestureDetector mScaleDetector;  
    private GestureDetector mGestureDetector;  
	//Json
	public Need need;//显示的Need
	public UserInfo needUserInfo;//Need主人的信息
	public List<UserInfo> otherUserInfos;//评论者信息
	public List<UserLocation> otherUserlocaltions;//评论者信息
	public List<NeedComment> needComments;//评论
	public UserInfo solveUserInfo;//解决者信息
	public UserDetailInfo userDetailInfo;
	
	//Model
	private ArrayList<NeedDetailActivityModel> commentsForAdapter=new ArrayList<NeedDetailActivityModel>();
	
	//Adapter
	NeedDetailActivityAdapter detailAdapter;
	
	//Need解决时用
	public int solveId=-1;
	public int needId;
	public int userId;
	public int longClickPosition;
	
	private float mScaleFactor = 1;  
	private static final int ZOOM_IN = 4;  
    private static final int ZOOM_OUT = 5;  
    private final int MAX_ZOOM_IN_SIZE = 60;  
    private final int MAX_ZOOM_OUT_SIZE = 20;  
    private final int THE_SIZE_OF_PER_ZOOM = 9;  
    private float mTextSize = 27;  
    private int mZoomMsg = -1;  
    
	private ToastHandler toastHandler=new ToastHandler(NeedDetailActivity.this);
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("NeedDetailActivity");
		setContentView(R.layout.need_detail_page);
		loadingDialogShow();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				UMShareInit();
			}
		}).start();
		int userIdtmp = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
		needId=getIntent().getExtras().getInt("needId");//获取传过来的needId
		userId=getIntent().getExtras().getInt("userId",userIdtmp);
		findView();//找到View
		
		//设置头像获得焦点，这样子点进去的时候ScrollView就不会自动翻到最底部了
		headPic.setFocusable(true);
		headPic.setFocusableInTouchMode(true);
		headPic.requestFocus();
		
		
		new GetDataTask().execute();//获取服务器数据
		
		//ListView绑定Adapter
		SetListener();

	}
	private void UMShareInit()
	{
		mController.getConfig().removePlatform(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE);
		String appId="1102452990";
		String appSecret="KgWvoq6629aW17di";
		// 友盟     设置分享内容
//		mController.setShareContent("伊伊，校园约会神器。http://115.29.179.60/");
		// 设置分享图片, 参数2为图片的url地址
//		mController.setShareMedia(new UMImage(this, 
//		                                      "http://www.umeng.com/images/pic/banner_module_social.png"));
		
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appSecret);//QQ空间
		qZoneSsoHandler.addToSocialSDK();
		UMQQSsoHandler umqqSsoHandler=new UMQQSsoHandler(this, appId, appSecret);//QQ
		umqqSsoHandler.addToSocialSDK();
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		//人人
		RenrenSsoHandler renrenSsoHandler=new RenrenSsoHandler(this, "271768", "0705dfc159df45f8a86bf8dec4098d69", "309a12d507c4458990897c12c2cbe6f0");
		mController.getConfig().setSsoHandler(renrenSsoHandler);
//		UMWXHandler umwxHandler=new UMWXHandler(this,appId ,appSecret );//微信
//		umwxHandler.addToSocialSDK();
//		UMWXHandler wxCircleHandler=new UMWXHandler(this, appId,appSecret);//朋友圈
//		wxCircleHandler.setToCircle(true);
//		wxCircleHandler.addToSocialSDK();
		
//		mController.getConfig().setPlatformOrder(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,
//				SHARE_MEDIA.TENCENT,SHARE_MEDIA.RENREN);
	}
	private void setUMShareContent(String name,Bitmap bitmap)
	{
		String imageUrl =URLConstant.SHARE_FOLDER_URL + name+".jpg";
		Bitmap image = bitmap;
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent("伊伊，校园约会神器");
		qqShareContent.setTitle("分享到QQ:");
		qqShareContent.setShareImage(new UMImage(NeedDetailActivity.this, image));
		qqShareContent.setTargetUrl(imageUrl);
		mController.setShareMedia(qqShareContent);
		
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("伊伊，校园约会神器");
		qzone.setTargetUrl(imageUrl);
		qzone.setTitle("分享到QQ空间:");
		qzone.setShareImage(new UMImage(NeedDetailActivity.this, image));
		mController.setShareMedia(qzone);
	
		SinaShareContent sina = new SinaShareContent();
		sina.setShareContent("伊伊，校园约会神器");
		sina.setTargetUrl(imageUrl);
		sina.setTitle("分享到新浪微博:");
		qzone.setShareImage(new UMImage(NeedDetailActivity.this, image));
		mController.setShareMedia(sina);
		
		TencentWbShareContent tencentWbShareContent=new TencentWbShareContent();
		tencentWbShareContent.setShareContent("伊伊，校园约会神器");
		tencentWbShareContent.setTargetUrl(imageUrl);
		tencentWbShareContent.setTitle("分享到腾讯微博:");
		qzone.setShareImage(new UMImage(NeedDetailActivity.this, image));
		mController.setShareMedia(tencentWbShareContent);
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	   
	}
	private void SetListener() {
		
		deleteTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyTakeDialog myTakeDialog=new MyTakeDialog(NeedDetailActivity.this,R.style.MyDialog)
				{
					@Override
					public void onYesButtonClick() {
						// TODO Auto-generated method stub
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								String response;
								boolean result = false;
								try {
									int ii = needId;
									response = ServerCommunication.requestWithoutEncrypt(needId, URLConstant.DELETE_NEED_URL);
									result = JacksonUtil.json().fromJsonToObject(response,Boolean.class);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Message msg=toastHandler.obtainMessage();
									msg.what=Constant.CONNECT_FAILED;
									msg.obj=e.getMessage();
									msg.sendToTarget();
									return;
								}

								Message msg=toastHandler.obtainMessage();
								if(result)
									msg.what=Constant.DELETE_SUCCESS;
								else
									msg.what=Constant.DELETE_FAILED;

								msg.sendToTarget();
								dismiss();
							}
						}).start();
						
					}
				};
				myTakeDialog.setText("确认要删除该条Need?");
				myTakeDialog.show();
			}
		});
		detailListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        return; 
			    } 
				TextView commentNumTextView=(TextView)findViewById(R.id.home_page_item_numofcomment);
				int realPosition=(int)id; 
				realPosition=check(realPosition);
				new NeedDetailCommentUtil(NeedDetailActivity.this, 
						needId, commentsForAdapter.get(realPosition).getCommentId(),
						commentNumTextView, Constant.COMMENT_NEED_DETAIL);
			}
		});
		headPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(NeedDetailActivity.this, need.getUserId()).click();
			}
		});
		
		helperHeadPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(solveUserInfo.getUserId()==-1)
					return;
				Intent intent = new Intent(NeedDetailActivity.this,PersonInformationActivity.class);
				intent.putExtra("userId",String.valueOf(solveUserInfo.getUserId()));
				startActivity(intent);
			}
		});
		
		giveHeartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
					
				int beSendManUserId=needUserInfo.getUserId();
				new GiveHeartEvent(getContext(), beSendManUserId, needId).start();
				
			}
		});
		
		//私信
		messageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 是否只有已登录用户才能打开分享选择页
//				mController.directShare(getContext(), SHARE_MEDIA.RENREN, new SnsPostListener() {
//					
//					@Override
//					public void onStart() {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
		         mController.openShare(NeedDetailActivity.this, false);
		         UMAppAdapter appAdapter = new UMAppAdapter(NeedDetailActivity.this);    
		         mShakeController.takeScrShot(NeedDetailActivity.this, appAdapter, new OnScreenshotListener() {
		 			
		 			@Override
		 			public void onComplete(Bitmap bmp) {
		 				// TODO Auto-generated method stub
		 				if(bmp!=null)
		 				{
		 					long time=new Date().getTime();
		 					String name=time+"";
		 					new UploadHelper(0).uploadScrShot(bmp, name);
		 					setUMShareContent(name, bmp);
		 				}
		 				else
		 				{
		 					MyToast.showToast(NeedDetailActivity.this, "截图失败，请稍后再试...");
		 				}
		 			}
		 		});
				
//				String friendId = needUserInfo.getUserId()+"";
//				String friend_Name = needUserInfo.getNickName();
//				SharedPreferences lightDB = getSharedPreferences(Constant.INEEDUSPR,0);
//				String userId = ""+lightDB.getInt(Constant.ID,-1);
//				
//				if(friendId.equals(userId))
//				{
//					
//					MyToast.showToast(getApplicationContext(), "你要和自己私聊？~", false);
//					return;
//				}
//				
//				Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
//				chatIntent.putExtra("USERID",userId);
//				chatIntent.putExtra("FRIENDID",friendId);
//				chatIntent.putExtra("FRIEND_NAME",friend_Name);
//				startActivity(chatIntent);
				
			}
		});
		
		commentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TextView commentNumTextView=(TextView)findViewById(R.id.home_page_item_numofcomment);
				new NeedDetailCommentUtil(NeedDetailActivity.this, needId, -1, commentNumTextView, Constant.COMMENT_NEED_DETAIL);
				isScrollDown=true;
			}
		});
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				NeedDetailActivity.this.finish();
			}
		});
	}
	
	
	private void InitDataSource() {
		if(needComments.size()==0||otherUserInfos.size()==0)
			return;
		
		
		commentsForAdapter.clear();
		for(int i=0;i<needComments.size();i++)
		{
			if(needComments.get(i).getCommentedUserId()==-1)
			{
				addDetailComment(i);
				checkReply();
			}

		}
	}
	public void addDetailComment(int i)
	{
		NeedDetailActivityModel d=new NeedDetailActivityModel();
		d.setUserId(needComments.get(i).getUserId());
		d.setHelperName(otherUserInfos.get(i).getNickName());//评论者姓名
		d.setSex(otherUserInfos.get(i).getSex());
		int distance = DataTraslator.GetDistanceToMe(NeedDetailActivity.this, otherUserlocaltions.get(i).getLat(),otherUserlocaltions.get(i).getLng());
		d.setDistance(DataTraslator.DistanceToString(distance));
		d.setLastLogTime(DataTraslator.LongToTimePastGeneral(otherUserlocaltions.get(i).getTime()));
		d.setContent(needComments.get(i).getComment());
		d.setTime(needComments.get(i).getTime());
		d.setCommentId(needComments.get(i).getId());
		d.setCommentedUserId(needComments.get(i).getCommentedUserId());
		if(d.getCommentedUserId()!=-1)
			d.setThatManName(commentsForAdapter.get(commentsForAdapter.size()-1).getHelperName());
		
		commentsForAdapter.add(d);
	}
	public void checkReply()
	{
		for(int j=0;j<needComments.size();j++)
		{
			if(needComments.get(j).getCommentedUserId()!=-1)//对评论的回复
			if(commentsForAdapter.get(commentsForAdapter.size()-1).getCommentId()
					==needComments.get(j).getCommentedUserId())
			{
				addDetailComment(j);
				checkReply();
			}
		}
	}
	public int check(int pos){
		if(pos==commentsForAdapter.size()-1)
			return pos;
		if(commentsForAdapter.get(pos).getCommentedUserId()==-1)
			if((pos+1)>commentsForAdapter.size()-1)
				return pos;
			if(commentsForAdapter.get(pos+1).getCommentedUserId()==-1)
				return pos;
		pos++;
		if(commentsForAdapter.get(pos).getCommentedUserId()==-1)
		{
			return pos-1;
		}
		return check(pos);
	}
	public class GetDataTask extends AsyncTask<Void, Void, Object> {
		
		@Override
		protected Object doInBackground(Void... params) {
			//后台获取数据
			String response;
			try {
				int xx= needId;
				response = ServerCommunication.requestWithoutEncrypt(needId+"", URLConstant.SHOW_NEED_DETAIL_URL);
			} catch (PostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			try {
				DetailNeedJsonObject needDetail = JacksonUtil.json().fromJsonToObject(response,DetailNeedJsonObject.class);
				return needDetail;
			} catch (Exception e) {
				return  null;
			}
			
		}

		
		@Override
		protected void onPostExecute(Object result) {
			if(result==null){
				MyToast.showToast(NeedDetailActivity.this,"加载失败");
				loadingDialogDismiss();
				return;
			}
			if(!ServerCommunication.checkResult(NeedDetailActivity.this, result))//各种网络异常的处理部分
			{
				MyToast.showToast(NeedDetailActivity.this, (String)result);
				loadingDialogDismiss();
				return;
			}
			DetailNeedJsonObject needDetail=(DetailNeedJsonObject)result;
			if(needDetail==null||needDetail.getNeed()==null)
			{
			
				MyToast.showToast(NeedDetailActivity.this, "该见面已被主人删除", true);
				loadingDialogDismiss();
				finish();
				super.onPostExecute(result);
				return;
			}
			
			need=needDetail.getNeed();
			needUserInfo=needDetail.getMyUserInfo();
			needComments=needDetail.getNeedComments();
			otherUserInfos=needDetail.getOtherUserInfos();
			otherUserlocaltions=needDetail.getOtherUserLocations();
			solveUserInfo=needDetail.getSolveUserInfo();
			userDetailInfo=needDetail.getUserDetailInfo();
			
			int userIdtmp = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
			if(needUserInfo.getUserId()==userIdtmp){
				deleteTextView.setVisibility(View.VISIBLE);
			}
			
			InitView();//初始化控件显示信息
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					InitDataSource();//初始化Adapter数据源details  评论多了有点卡UI。。。
				}
			}).start();
			
			detailAdapter=new NeedDetailActivityAdapter(NeedDetailActivity.this, commentsForAdapter,needUserInfo.getUserId());
			detailListView.setAdapter(detailAdapter);
			detailAdapter.notifyDataSetChanged();
			OnitemLongClick();
			
			if(isScrollDown)
			{
				final ScrollView scrollView=(ScrollView)findViewById(R.id.need_detail_page_scrollview);
				new Handler().post(new Runnable() {
				    @Override
				    public void run() {
				        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				    }
				});
				isScrollDown=false;
			}
			loadingDialogDismiss();
			super.onPostExecute(result);
		}


		private void OnitemLongClick() {
			// TODO Auto-generated method stub
			final SharedPreferences setting=getSharedPreferences(Constant.INEEDUSPR, Context.MODE_PRIVATE);
			
			
					detailListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
						@Override
						public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							if(id<-1) { 
						        // 点击的是headerView或者footerView 
						        return false; 
						    } 
							longClickPosition=(int)id; 
							
							if(needUserInfo.getUserId()!=setting.getInt(Constant.ID, 0))
							{
								MyToast.showToast(NeedDetailActivity.this, "只有主人才能选择解决者哦~", true);
								return false;
							}
							if(need.getSolveId()!=-1)
							{
								MyToast.showToast(NeedDetailActivity.this, "已解决~", true);
								return false;
							}
							//弹出Dialog
							if(needComments.get(longClickPosition).getUserId()!=needUserInfo.getUserId())
							{
								MyTakeDialog dialog2 = new MyTakeDialog(NeedDetailActivity.this,R.style.MyDialog){
									@Override
									public void onYesButtonClick() {
										SharedPreferences settings=getSharedPreferences(Constant.INEEDUSPR, Context.MODE_PRIVATE);
										int userId=settings.getInt(Constant.ID, 0);
										solveId=needComments.get(longClickPosition).getUserId();
										long timeNow=new Date().getTime();
										int NeedId=needId;
										
										JsonObject jsonObject=new JsonObject();
										jsonObject.setInt1(userId);
										jsonObject.setInt2(solveId);
										jsonObject.setInt3(NeedId);
										jsonObject.setLong1(timeNow);
					
										String jsonSend = JacksonUtil.json().fromObjectToJson(jsonObject);
										final NameValuePair jsonSendNameValuePair= new BasicNameValuePair("data", jsonSend);
										new Thread(){
											public void run() {
												CustomerHttpClient.post(URLConstant.THANK_URL, jsonSendNameValuePair);
												Message msg=toastHandler.obtainMessage();
												msg.what=Constant.THANK_SUCCESS;
												msg.sendToTarget();
											};
										}.start();
									dismiss();
									};
								};
								dialog2.setText("是否采纳"+otherUserInfos.get(longClickPosition).getRealName()+"的解决方案?");
				                	dialog2.show();
							}
							else {
								MyToast.showToast(NeedDetailActivity.this, "不能选择自己为解决者~", true);
							}
							return true;
						}
					});
			
		}
	}
	@SuppressLint("NewApi")
	private void InitView() {
		// TODO Auto-generated method stub
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+needUserInfo.getUserId()+".png",headPic, getHeadDisplayOption()); 
		if(needUserInfo.getSex().equals("男")){
			loveNumTextView.setText(Constant.GOD_VALUE_STRING+needUserInfo.getLoveNum());
			sexPic.setBackgroundResource(R.drawable.sex_boy_press);
		}else if(needUserInfo.getSex().equals("女")){
			sexPic.setBackgroundResource(R.drawable.sex_girl_press);
			loveNumTextView.setText(Constant.GODNESS_VALUE_STRING+needUserInfo.getLoveNum());
		}
			
		nicknameTextView.setText(needUserInfo.getNickName()+"  "+userDetailInfo.getAcademy());
		
		
		 //实现TextView复制粘贴功能
		contentTextView.setTransformationMethod(HideReturnsTransformationMethod 
                .getInstance());  
		contentTextView.setTextIsSelectable(true);  
        mScaleDetector = new ScaleGestureDetector(this, new MyScaleListener());  
        mGestureDetector = new GestureDetector(this,  
                new GestureDetector.SimpleOnGestureListener() {  
                });
        mGestureDetector.setOnDoubleTapListener(null);  
		
        
		contentTextView.setText(need.getContent());
		switch (need.getType()) {
			case Constant.TYPE_ASK:
				state_button.setBackgroundResource(R.drawable.common_ask_press);
				break;
			case Constant.TYPE_BORROW:
				state_button.setBackgroundResource(R.drawable.common_borrow_press);	
				break;
			case Constant.TYPE_INVITE:
				state_button.setBackgroundResource(R.drawable.common_invite_press);
				break;
			case Constant.TYPE_BRING:
				state_button.setBackgroundResource(R.drawable.common_bring_press);
				break;
			case Constant.TYPE_BUY:
				state_button.setBackgroundResource(R.drawable.common_buy_press);
				break;
			default:
				break;
		}
		rewardTextView.setText(need.getReward());
		
		//将Long型的数据处理为还剩X天X小时X分钟形式
		long dueTime=need.getTime()+need.getTimeLimit();//截止时间点
		long timeNow=DateUtil.getMSTime();//现在时间
		long timeRemain=dueTime-timeNow;
		timeRemainTextView.setText("剩余");
		if(timeRemain>0)
		{
			timeTextView.setText(DataTraslator.LongToTimeRemain(timeRemain));
		}
		else {
			timeTextView.setText("已截止");
		}
		int dis = DataTraslator.GetDistanceToMe(NeedDetailActivity.this, need.getLat(), need.getLng());
		distanceTxtVu.setText(DataTraslator.DistanceToString(dis));
		sendtimeTxtVu.setText(DataTraslator.LongToTimePastGeneral(need.getTime()));
		//没有人帮助时不显示帮助者一栏
		if(need.getSolveId()==-1)
			helperLinearLayout.setVisibility(View.GONE);
		else{
			helperNameTextView.setText(solveUserInfo.getNickName());
		    //helperHeadPic;
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+solveUserInfo.getUserId()+".png", helperHeadPic, getHeadDisplayOption());
		}
		popularityNumTextView.setText(needUserInfo.getPopularityNum()+"");
		commentNumTextView.setText(need.getCommentNum()+"");
		needNumTxtVu.setText("#"+need.getId());
	}
	
	private void findView() {
		headPic=(ImageButton)findViewById(R.id.home_page_user_headpic_button);//暂时使用静态
		sexPic=(ImageView)findViewById(R.id.home_page_sex);
		nicknameTextView=(TextView)findViewById(R.id.home_page_name);
		loveNumTextView=(TextView)findViewById(R.id.home_page_loveNum);
		contentTextView=(TextView)findViewById(R.id.home_page_content);
		state_button=(ImageView)findViewById(R.id.homepage_state_ask_icon);
		rewardTextView=(TextView)findViewById(R.id.home_page_reward);
		timeTextView=(TextView)findViewById(R.id.home_page_time);
		timeRemainTextView=(TextView)findViewById(R.id.need_detail_time_remain_text);
		
		helperLinearLayout=(LinearLayout)findViewById(R.id.need_detail_helper_linearlayout);//Helper所在的Linearlayout，未解决时不显示
		helperNameTextView=(TextView)findViewById(R.id.home_page_helperName);
		helperHeadPic=(ImageButton)findViewById(R.id.home_page_helper_headpic_button);
		
		popularityNumTextView=(TextView)findViewById(R.id.home_page_item_numofheart);
		commentNumTextView=(TextView)findViewById(R.id.home_page_item_numofcomment);
		deleteTextView=(TextView)findViewById(R.id.need_detail_delete);//只有主人才能看见
		//if(new SPHelper(NeedDetailActivity.this).GetUserId()!=userId)
			deleteTextView.setVisibility(View.GONE);
		
		detailListView=(ScrollListView)findViewById(R.id.need_detail_page_listview);
		giveHeartButton=(ImageButton)findViewById(R.id.need_detail_page_giveheart_button); 
		messageButton=(ImageButton)findViewById(R.id.need_detail_page_message_button);//私信
		commentButton=(ImageButton)findViewById(R.id.need_detail_page_comment_button);
		backButton=(ImageButton)findViewById(R.id.need_detail_back_button);
		distanceTxtVu=(TextView)findViewById(R.id.tv_distance);
		sendtimeTxtVu=(TextView)findViewById(R.id.tv_time);
		needNumTxtVu=(TextView) findViewById(R.id.tv_num);

	}
	private boolean isScrollDown=false;//获取数据后是否滚动到最底部
	public class NeedDetailCommentUtil extends CommentUtil
	{

		public NeedDetailCommentUtil(Context _context, int NeedId,
				int _whoId, TextView _commentNumTextView, int COMMNET_FLAG) {
			super(_context, NeedId, _whoId, _commentNumTextView, COMMNET_FLAG);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void NeedDetailUIAction() {
			// TODO Auto-generated method stub
			super.NeedDetailUIAction();
			new GetDataTask().execute();
		}
		
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler mUiHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case ZOOM_IN:  
                zoomIn();  
                contentTextView.invalidate();  
                break;  
            case ZOOM_OUT:  
                zoomOut();  
                contentTextView.invalidate();//修改TextView后，调用该方法刷新TextView，这样才能看到重新绘制的界面。  
                break;  
            default:  
                break;  
            }  
        }  
    };  
	
	private void zoomIn() {  
        mTextSize = mTextSize + THE_SIZE_OF_PER_ZOOM <= MAX_ZOOM_IN_SIZE ? mTextSize  
                + THE_SIZE_OF_PER_ZOOM  
                : MAX_ZOOM_IN_SIZE;  
        if (mTextSize >= MAX_ZOOM_IN_SIZE) {  
            mTextSize = MAX_ZOOM_IN_SIZE;  
        }  
        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);  
    }  
  
    private void zoomOut() {  
        mTextSize = mTextSize - THE_SIZE_OF_PER_ZOOM < MAX_ZOOM_OUT_SIZE ? MAX_ZOOM_OUT_SIZE  
                : mTextSize - THE_SIZE_OF_PER_ZOOM;  
        if (mTextSize <= MAX_ZOOM_OUT_SIZE) {  
            mTextSize = MAX_ZOOM_OUT_SIZE;  
        }  
        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);  
    }  
  
    private class MyScaleListener extends  
            ScaleGestureDetector.SimpleOnScaleGestureListener {  
        @Override  
        public boolean onScale(ScaleGestureDetector detector) {  
            float scale = detector.getScaleFactor();  
            if (scale < 0.999999 || scale > 1.00001) {  
                mScaleFactor = scale;  
            }  
            return true;  
        }  
  
        @Override  
        public boolean onScaleBegin(ScaleGestureDetector detector) {  
            return true;  
        }  
  
        @Override  
        public void onScaleEnd(ScaleGestureDetector detector) {  
            float scale = detector.getScaleFactor();  
            if (mScaleFactor > 1.0) {  
                mZoomMsg = ZOOM_IN;  
            } else if (mScaleFactor < 1.0) {  
                mZoomMsg = ZOOM_OUT;  
            }  
        }  
    }  
  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        mScaleDetector.onTouchEvent(ev);  
        final int action = ev.getAction();  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            mGestureDetector.onTouchEvent(ev);  
            return false;  
  
        case MotionEvent.ACTION_MOVE:  
            mGestureDetector.onTouchEvent(ev);  
            return false;  
  
        case MotionEvent.ACTION_UP:  
            mGestureDetector.onTouchEvent(ev);  
            Message msg = Message.obtain();  
            msg.what = mZoomMsg;  
            mUiHandler.sendMessage(msg);  
            mZoomMsg = -1;  
            return false;  
        }  
        return true;  
    }  
  
    public boolean onTouchEvent(MotionEvent ev) {  
        mScaleDetector.onTouchEvent(ev);  
        final int action = ev.getAction();  
  
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        case MotionEvent.ACTION_MOVE:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        case MotionEvent.ACTION_UP:  
            mGestureDetector.onTouchEvent(ev);  
            Message msg = Message.obtain();  
            msg.what = mZoomMsg;  
            mUiHandler.sendMessage(msg);  
            mZoomMsg = -1;  
            return true;  
  
        case MotionEvent.ACTION_CANCEL:  
            mGestureDetector.onTouchEvent(ev);  
            return true;  
  
        default:  
            if (mGestureDetector.onTouchEvent(ev)) {  
                return true;  
            }  
  
            return true;  
        }  
    }  
  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mUiHandler.removeCallbacksAndMessages(null);  
    }
}