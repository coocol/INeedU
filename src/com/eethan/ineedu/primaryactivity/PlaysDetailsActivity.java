package com.eethan.ineedu.primaryactivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.PlayJoinEvent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.HeadGalleryAdapter;
import com.eethan.ineedu.adapter.PlaysDetailsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.NeedComment;
import com.eethan.ineedu.databasebeans.Plays;
import com.eethan.ineedu.databasebeans.PlaysComment;
import com.eethan.ineedu.databasebeans.PlaysParticipant;
import com.eethan.ineedu.databasebeans.PourlistenComment;
import com.eethan.ineedu.databasebeans.TakePhotosComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.DetailPlaysJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.PlayCommentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.UploadHelper;
import com.eethan.ineedu.util.WebTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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

public class PlaysDetailsActivity extends BaseActivity {
	
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	// 声明mShakeController, 参数1为sdk 控制器描述符
	UMShakeService mShakeController = UMShakeServiceFactory
	                              .getShakeService("write.your.content");

	private Long timediffLong;
	
	private ScrollListView commscrollListView;
	private Plays plays;
	private ArrayList<PlayCommentModel> playsCommentsList;
	private ArrayList<UserInfo> commUserInfos;
	private UserInfo ownerUserInfo;
	private PlaysDetailsAdapter adapter;

	private ImageLoader imageLoader;
	android.content.DialogInterface.OnClickListener positiveClickListener;
	
	private ImageButton backImgBtn;

	private ImageButton ownerHeadImgBtn;
	private ImageView ownerSexImgVu;
	private TextView ownerNameTxtVu;
	private TextView sendTimeTxtVu;
	private TextView themeTxtVu;
	private TextView contentTxtVu;
	private TextView requireTxtVu;
	private TextView timeTxtVu;
	private TextView placeTxtVu;
	private TextView distanceTxtVu;
	private TextView deadLineTxtVu;
	private TextView joinNumTxtVu;
	private ImageButton joinImgBtn;
	private ImageButton transmitImgBtn;
	private ImageButton commImgBtn;
	private RelativeLayout galleryRL;
	
	ScrollView scrollView;
	private Gallery gallery;
	private HeadGalleryAdapter headGalleryAdapter;
	private ArrayList<Integer> joinUserIdList;
	private int playId;
	
	private boolean isFirstIn = true;
	
	private boolean isCommAMan = false;
	private int commentedManId = -1;
	
	private DisplayImageOptions options_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.plays_detail);
		
		options_head = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
		.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		
		scrollView = (ScrollView)findViewById(R.id.playdetail_scroll);
		galleryRL = (RelativeLayout)findViewById(R.id.re_gallery);
		backImgBtn = (ImageButton)findViewById(R.id.back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		positiveClickListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				PlaysParticipant playsParticipant = new PlaysParticipant();
				playsParticipant.setOwner(ownerUserInfo.getUserId());
				playsParticipant.setUserId(new SPHelper(
						PlaysDetailsActivity.this).GetUserId());
				playsParticipant.setPlaysId(plays.getId());
				new PlayJoinEvent(playsParticipant, PlaysDetailsActivity.this).start();
			}
		};

		commscrollListView = (ScrollListView) findViewById(R.id.lv_comment);
		ownerHeadImgBtn = (ImageButton) findViewById(R.id.imgbt_head);
		ownerHeadImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ownerUserInfo != null && ownerUserInfo.getUserId() > 0) {
					new HeadClickEvent(getApplicationContext(), ownerUserInfo
							.getUserId()).click();
				}
			}
		});
		ownerSexImgVu = (ImageView) findViewById(R.id.home_page_sex);
		ownerNameTxtVu = (TextView) findViewById(R.id.tv_nickname);
		sendTimeTxtVu = (TextView) findViewById(R.id.tv_init_time);
		themeTxtVu = (TextView) findViewById(R.id.tv_theme);
		contentTxtVu = (TextView) findViewById(R.id.tv_content);
		requireTxtVu = (TextView) findViewById(R.id.tv_requirement);
		timeTxtVu = (TextView) findViewById(R.id.tv_date);
		placeTxtVu = (TextView) findViewById(R.id.tv_place);
		joinNumTxtVu = (TextView)findViewById(R.id.tv_join_num);
		distanceTxtVu = (TextView) findViewById(R.id.tv_distance);
		deadLineTxtVu = (TextView) findViewById(R.id.tv_time_limit);
		joinImgBtn = (ImageButton) findViewById(R.id.bt_praise);
		joinImgBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				if(deadLineTxtVu.getText().toString().equals("报名已截止")){
					MyToast.showToast(PlaysDetailsActivity.this, "报名已截止");
					return;
				}
				MyTakeDialog dialog2 = new MyTakeDialog(PlaysDetailsActivity.this,R.style.MyDialog){
					@Override
					public void onYesButtonClick() {
						PlaysParticipant playsParticipant = new PlaysParticipant();
						playsParticipant.setOwner(ownerUserInfo.getUserId());
						playsParticipant.setUserId(new SPHelper(
								PlaysDetailsActivity.this).GetUserId());
						playsParticipant.setPlaysId(plays.getId());
						new PlayJoinEvent(playsParticipant, PlaysDetailsActivity.this).start();
					dismiss();
					};
				};
				dialog2.setText("确定要参加该活动?");
                	dialog2.show();

			}
		});
		transmitImgBtn = (ImageButton) findViewById(R.id.bt_transmit);
		transmitImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 mController.openShare(PlaysDetailsActivity.this, false);
		         UMAppAdapter appAdapter = new UMAppAdapter(PlaysDetailsActivity.this);    
		         mShakeController.takeScrShot(PlaysDetailsActivity.this, appAdapter, new OnScreenshotListener() {
		 			
		 			@Override
		 			public void onComplete(Bitmap bmp) {
		 				// TODO Auto-generated method stub
		 				if(bmp!=null)
		 				{
		 					 int oldHeight1 = bmp.getHeight();
			     				int oldWidth1 = bmp.getWidth();
			     				float scale = (float) 800 / oldHeight1;
			                     //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存  
			                     Bitmap smallBitmap = zoomBitmap(bmp, (int) (oldWidth1 * scale),
			     						(int) (oldHeight1 * scale));
		 					long time=new Date().getTime();
		 					String name=time+"";
		 					new UploadHelper(0).uploadScrShot(smallBitmap, name);
		 					setUMShareContent(name,smallBitmap);
		 				}
		 				else
		 				{
		 					MyToast.showToast(PlaysDetailsActivity.this, "截图失败，请稍后再试...");
		 				}
		 			}
		 			
		 			public Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth,
							int newHeight) {
						// 获得图片的宽高
						int width = oldBitmap.getWidth();
						int height = oldBitmap.getHeight();
						// 计算缩放比例
						float scaleWidth = ((float) newWidth) / width;
						float scaleHeight = ((float) newHeight) / height;
						// 取得想要缩放的matrix参数
						Matrix matrix = new Matrix();
						matrix.postScale(scaleWidth, scaleHeight);
						// 得到新的图片
						Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height,
								matrix, true);
						oldBitmap.recycle();
						return newbm;
					}
		 		});
			}
		});
		commImgBtn = (ImageButton) findViewById(R.id.bt_comment);
		commImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new CommentUtil();
			}
		});

		playId = getIntent().getIntExtra("playId", -1);

		imageLoader = ImageLoader.getInstance();
		plays = new Plays();
		playsCommentsList = new ArrayList<PlayCommentModel>();

		adapter = new PlaysDetailsAdapter(this, playsCommentsList, imageLoader);
		commscrollListView.setAdapter(adapter);

		commscrollListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				isCommAMan = true;
				commentedManId = playsCommentsList.get((int)arg3).getCommUserInfo().getUserId();
				new CommentUtil();
			}
		});
		
		gallery = (Gallery)findViewById(R.id.ga_people_join);
		joinUserIdList = new ArrayList<Integer>();
		headGalleryAdapter = new HeadGalleryAdapter(PlaysDetailsActivity.this, joinUserIdList, imageLoader);
		gallery.setSpacing(6);
		gallery.setAdapter(headGalleryAdapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				new HeadClickEvent(PlaysDetailsActivity.this, joinUserIdList.get((int)arg3)).click();
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				UMShareInit();
			}
		}).start();
		
		loadingDialogShow();
		new GetDataTask().execute();

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
		qqShareContent.setShareImage(new UMImage(PlaysDetailsActivity.this, image));
		qqShareContent.setTargetUrl(imageUrl);
		mController.setShareMedia(qqShareContent);
		
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("伊伊，校园约会神器");
		qzone.setTargetUrl(imageUrl);
		qzone.setTitle("分享到QQ空间:");
		qzone.setShareImage(new UMImage(PlaysDetailsActivity.this, image));
		mController.setShareMedia(qzone);
	
		SinaShareContent sina = new SinaShareContent();
		sina.setShareContent("伊伊，校园约会神器");
		sina.setTargetUrl(imageUrl);
		sina.setTitle("分享到新浪微博:");
		qzone.setShareImage(new UMImage(PlaysDetailsActivity.this, image));
		mController.setShareMedia(sina);
		
		TencentWbShareContent tencentWbShareContent=new TencentWbShareContent();
		tencentWbShareContent.setShareContent("伊伊，校园约会神器");
		tencentWbShareContent.setTargetUrl(imageUrl);
		tencentWbShareContent.setTitle("分享到腾讯微博:");
		qzone.setShareImage(new UMImage(PlaysDetailsActivity.this, image));
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

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.PLAYS_DETAIL_PALY;
			DetailPlaysJsonObject refreshPR;
			try {
				response = ServerCommunication.requestWithoutEncrypt(playId, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						DetailPlaysJsonObject.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);

			loadingDialogDismiss();
			
			if(result==null){
				MyToast.showToast(PlaysDetailsActivity.this,"加载失败");
				return;
			}

			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication.checkResult(PlaysDetailsActivity.this,
					result)) {
				MyToast.showToast(PlaysDetailsActivity.this, (String) result);
				return;
			}

			DetailPlaysJsonObject res = (DetailPlaysJsonObject) result;
			ownerUserInfo = res.getMyUserInfo();

			plays = res.getPlays();
			int num = res.getCommentUserInfos().size();
			playsCommentsList.clear();
			PlayCommentModel model;
			for (int i = 0; i < num; i++) {
				model = new PlayCommentModel();
				model.setCommUserInfo(res.getCommentUserInfos().get(i));
				model.setCommUserLocation(res.getCommentUserLocations().get(i));
				model.setPlaysComment(res.getComments().get(i));
				playsCommentsList.add(model);
			}

			
			adapter.notifyDataSetChanged();
			
			joinUserIdList.clear();
			num = res.getMembers().size();
			if(num>0){
				galleryRL.setVisibility(View.VISIBLE);
			}
			for (int i = 0; i < num; i++) {
				joinUserIdList.add(res.getMembers().get(i));
			}
			
			headGalleryAdapter.notifyDataSetChanged();
			
			imageLoader.displayImage(
					URLConstant.BIG_HEAD_PIC_URL + ownerUserInfo.getUserId()
							+ ".png", ownerHeadImgBtn,options_head);
			if (ownerUserInfo.getSex().equals("女")) {
				ownerSexImgVu.setImageResource(R.drawable.sex_girl_press);
			} else {
				ownerSexImgVu.setImageResource(R.drawable.sex_boy_press);
			}
			ownerNameTxtVu.setText(ownerUserInfo.getNickName());
			joinNumTxtVu.setText(String.valueOf(plays.getJoinNum()));
			try {
				Long curLong = DateUtil.getMSTime();
				Long deadLong = Long.parseLong(plays.getDeadLine());
				timediffLong = deadLong-curLong;
				if(timediffLong>0){
					deadLineTxtVu.setText(DataTraslator.LongToTimeRemain(timediffLong));
				}else {
					deadLineTxtVu.setText("报名已截止");
				}
				
			} catch (Exception e) {
				deadLineTxtVu.setText(plays.getDeadLine());
			}
			try {
				sendTimeTxtVu.setText(DataTraslator.LongToTimePast(Long.parseLong(plays.getInitTime())));
			} catch (Exception e) {
				sendTimeTxtVu.setText(plays.getInitTime());
			}
			try {
				int distanceToMe = DataTraslator.GetDistanceToMe(
						PlaysDetailsActivity.this, plays.getLat(),
						plays.getLng());
				distanceTxtVu
				.setText(DataTraslator.DistanceToString(distanceToMe));
			} catch (Exception e) {
				distanceTxtVu.setText(String.valueOf(plays.getLat())
						+ " "+String.valueOf(plays.getLng()));
			}
			themeTxtVu.setText(plays.getTheme());
			contentTxtVu.setText(plays.getContent());
			timeTxtVu.setText(plays.getTime());
			
			requireTxtVu.setText(plays.getRequirement());
			placeTxtVu.setText(plays.getPlace());
			
			if(!isFirstIn){
				Handler handler = new Handler();
				handler.post(new Runnable() {
					@Override
					public void run() {
						scrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});

			}

			isFirstIn = false;

		}
	}

	class CommentUtil {

		private View view1;
		private View view2;
		private AlertDialog myAlertDialog;
		private GridView gridView;
		private int[] imageIds;
		private HorizontalScrollView hsview;
		private BaseAdapter adapter;
		private EditText commentEdit;
		private ImageButton faceBtn;
		
		public CommentUtil(){
			init();
		}
		
		private void init() {
			view1 = LayoutInflater.from(PlaysDetailsActivity.this).inflate(R.layout.dialog_comment,
					null);
			myAlertDialog = new AlertDialog.Builder(PlaysDetailsActivity.this).setView(view1)
					.create();
			// 调整评论条的位置和大小
			Window w = myAlertDialog.getWindow();
			w.setGravity(Gravity.BOTTOM);
			w.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT,
					android.view.WindowManager.LayoutParams.WRAP_CONTENT);

			view2 = LayoutInflater.from(PlaysDetailsActivity.this).inflate(R.layout.dialog_comment,
					null);
			gridView = (GridView) view2.findViewById(R.id.facegrid);
			imageIds = new int[Constant.NUMOFFACE];
			hsview = (HorizontalScrollView) view2.findViewById(R.id.hsview);
			commentEdit = (EditText) view2.findViewById(R.id.dialog_comment_edit);

			faceBtn = (ImageButton) view2.findViewById(R.id.dialog_comment_face);
			faceBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showFacePanel();
				}

				/**
				 * 隐藏输入法
				 */
				private void hideSoftInput(EditText _edit) {
					InputMethodManager imm = (InputMethodManager) PlaysDetailsActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(_edit.getWindowToken(), 0);
				}

				/**
				 * 显示表情选单
				 */
				private void showFacePanel() {
					List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
					// 生成143个表情的id，封装
					for (int i = 0; i < Constant.NUMOFFACE; i++) {
						try {
							if (i < 10) {
								Field field = R.drawable.class
										.getDeclaredField("f00" + i);
								int resourceId = Integer.parseInt(field.get(null)
										.toString());
								imageIds[i] = resourceId;
							} else if (i < 100) {
								Field field = R.drawable.class
										.getDeclaredField("f0" + i);
								int resourceId = Integer.parseInt(field.get(null)
										.toString());
								imageIds[i] = resourceId;
							} else {
								Field field = R.drawable.class.getDeclaredField("f"
										+ i);
								int resourceId = Integer.parseInt(field.get(null)
										.toString());
								imageIds[i] = resourceId;
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("emotion", imageIds[i]);
						listItems.add(listItem);
					}
					SimpleAdapter simpleAdapter = new SimpleAdapter(PlaysDetailsActivity.this,
							listItems, R.layout.faceitem,
							new String[] { "emotion" }, new int[] { R.id.emotion });
					gridView.setAdapter(simpleAdapter);
					DisplayMetrics dm = new DisplayMetrics();
					((Activity) PlaysDetailsActivity.this).getWindowManager().getDefaultDisplay()
							.getMetrics(dm);
					float density = dm.density;
					int allWidth = 20 * listItems.size() * (int) density;
					int itemWidth = 20 * (int) density;
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
					gridView.setLayoutParams(params);
					gridView.setColumnWidth(itemWidth);
					gridView.setNumColumns((int) ((listItems.size() + 3) / 3));
					gridView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Bitmap bitmap = null;
							int index = commentEdit.getSelectionStart();
							bitmap = BitmapFactory.decodeResource(
									PlaysDetailsActivity.this.getResources(), imageIds[arg2
											% imageIds.length]);
							bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true); 
							ImageSpan imageSpan = new ImageSpan(
									((Activity) PlaysDetailsActivity.this), bitmap);
							String str = null;
							if (arg2 < 10) {
								str = "f00" + arg2;
							} else if (arg2 < 100) {
								str = "f0" + arg2;
							} else {
								str = "f" + arg2;
							}
							SpannableString spannableString = new SpannableString(
									str);
							spannableString.setSpan(imageSpan, 0, 4,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							Editable editable = commentEdit.getEditableText();
							if (index < 0 || index >= commentEdit.length()) {
								editable.append(spannableString);
							} else {
								editable.insert(index, spannableString);
							}
						}
					});
					hsview.setVisibility(View.VISIBLE);
					hideSoftInput(commentEdit);
				}
			});

			ImageButton sendBtn = (ImageButton) view2
					.findViewById(R.id.dialog_comment_send);
			sendBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (commentEdit.getText().toString().trim().equals("")) // 输入框不能为空
					{
						commentEdit.setError(Html
								.fromHtml("<font color=#E10979>评论内容不能为空!</font>"));
						return;
					}
					new CommentTask().execute();
					myAlertDialog.dismiss();
					loadingDialogShow();
				}
			});

			commentEdit.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hsview.setVisibility(View.GONE);
					return false;
				}
			});

			myAlertDialog.show();

			myAlertDialog.setContentView(view2);

			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) commentEdit
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 400);
	}

		// 异步处理线程
		private class CommentTask extends AsyncTask<Void, Void, Object> {

			private String commentString;
			private Long commentTiemLong;
			private String commentJson;
			private String response;
			private NeedComment needComment;
			private PourlistenComment pourlistenComment;
			private TakePhotosComment takePhotosComment;
			private PlaysComment playsComment;

			@Override
			protected Object doInBackground(Void... params) {
				// TODO Auto-generated method stub

				if (!commentEdit.getText().toString().trim().equals("")) {// 输入框不为空
					commentString = commentEdit.getText().toString();
				}
				try {
					commentTiemLong = WebTime.getTime();
				} catch (Exception e) {
					commentTiemLong = DateUtil.getMSTime();
				}
				

				SharedPreferences sharedPre = PlaysDetailsActivity.this
						.getSharedPreferences(Constant.INEEDUSPR,
								Context.MODE_PRIVATE);
				int userId = sharedPre.getInt(Constant.ID, -1);

				PlaysComment playsComment = new PlaysComment();
				playsComment.setUserId(userId);
				playsComment.setContent(commentString);
				playsComment.setTime(String.valueOf(commentTiemLong));
				playsComment.setPlaysId(plays.getId());
				if(isCommAMan){
					playsComment.setCommentedUserId(commentedManId);
				}else {
					playsComment.setCommentedUserId(-1);
				}
				isCommAMan = false;
				try {
					response = ServerCommunication.request(playsComment,
							URLConstant.PLAYS_COMMENT_PALY);
				} catch (PostException e) {
					e.printStackTrace();
					return e.getMessage();
				}
				try {
					return JacksonUtil.json().fromJsonToObject(response,
							Boolean.class);
				} catch (Exception e) {
					return false;
				}
				
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);

				loadingDialogDismiss();
				if(result==null){
					MyToast.showToast(PlaysDetailsActivity.this,
							"评论失败");
					return;
				}

				if (!ServerCommunication.checkResult(PlaysDetailsActivity.this,
						result)) {
					MyToast.showToast(PlaysDetailsActivity.this,
							(String) result);
					super.onPostExecute(result);
					return;
				}
				// 处理返回的结果
				Boolean _resultBoolean = (Boolean) result;
				if(_resultBoolean){
					MyToast.showToast(PlaysDetailsActivity.this, "评论成功");
					SharedPreferences sharedPre = getSharedPreferences(
							Constant.INEEDUSPR, Context.MODE_PRIVATE);
					int userId = sharedPre.getInt(Constant.ID, -1);
					new AddLoveNum(PlaysDetailsActivity.this, 1,userId).execute();
				}
				
				
				new GetDataTask().execute();
			}
		}

	}

}
