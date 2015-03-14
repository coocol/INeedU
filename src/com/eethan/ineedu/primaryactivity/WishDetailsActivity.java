package com.eethan.ineedu.primaryactivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.CommonUse.WishFlowerEvent;
import com.eethan.ineedu.adapter.WishDetailAdapter;
import com.eethan.ineedu.adapter.WishesAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.Wish;
import com.eethan.ineedu.databasebeans.WishComment;
import com.eethan.ineedu.jackson.DetailWishJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.LastPick;
import com.eethan.ineedu.model.WishDetailModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.IssueNeedActivity.viewHolderToDatabase;
import com.eethan.ineedu.secondaryactivity.WishJoinActivity;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.WebTime;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WishDetailsActivity extends BaseActivity {

	private int wishId = -1;
	private int ownerId = -1;
	private int commentedUserId = -1;

	private ImageView headImgVu;
	private TextView nameTxtVu;
	private TextView distanceTxtVu;
	private TextView timeTxtVu;

	private ImageButton backImgBtn;
	private ImageView backgroundImgVu;
	private TextView contentTxtVu;
	private ScrollView scrollView;
	private TextView wishNumTxtVu;
	private ScrollListView commListView;
	private ImageButton pickImgBtn;
	private ImageButton commImgBtn;
	private ImageButton flowerImgBtn;
	private TextView pickersTxtVu;

	private boolean isCommAMan = false;
	private boolean isFirstIn = false;
	private boolean isrefreshComm = false;

	private ArrayList<WishDetailModel> commlisList;
	private WishDetailAdapter adapter;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;
	private UserInfo girlUserInfo;
	private Wish wish;
	private android.content.DialogInterface.OnClickListener positiveClickListener;
	
	private MyTakeDialog dialog2 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wish_detail);

		wishId = getIntent().getIntExtra("wishId", -1);
		isFirstIn = false;
		
		
		 dialog2 = new MyTakeDialog(WishDetailsActivity.this,R.style.MyDialog,"不","是"){
				@Override
				public void onYesButtonClick() {
					// TODO Auto-generated method stub
					super.onYesButtonClick();
					dialog2.dismiss();
					new PickTheWish().execute();
				} 
			 };
				dialog2.setText("你想摘女孩的心愿吗？");
		
//		positiveClickListener = new android.content.DialogInterface.OnClickListener() {	
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				
//			}
//		};
		
		//将值赋给item类中对应的控件
		 options= new DisplayImageOptions.Builder()
		//.showStubImage(R.drawable.ic_stub_horizon)
		//.showImageForEmptyUri(R.drawable.ic_error_horizon)
		//.showImageOnFail(R.drawable.ic_error_horizon)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		 
		 options_head = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
			.showImageOnLoading(R.drawable.logo)
			.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
			.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		

		headImgVu = (ImageView) findViewById(R.id.iv_head);
		
		nameTxtVu = (TextView) findViewById(R.id.tv_nickname);
		distanceTxtVu = (TextView) findViewById(R.id.tv_distance);
		timeTxtVu = (TextView) findViewById(R.id.tv_time);
		wishNumTxtVu=(TextView) findViewById(R.id.tv_num);
		backImgBtn = (ImageButton) findViewById(R.id.imgbt_back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		pickersTxtVu = (TextView)findViewById(R.id.tv_pickoff);
		pickersTxtVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(WishDetailsActivity.this, WishJoinActivity.class);
				intent.putExtra("ownerId", ownerId);
				intent.putExtra("wishId", wishId);
				intent.putExtra("solvedId", wish.getSolveId());
				startActivity(intent);
			}
		});
		contentTxtVu = (TextView) findViewById(R.id.tv_content);
		backgroundImgVu = (ImageView) findViewById(R.id.iv_background);
		scrollView = (ScrollView) findViewById(R.id.sv_wish);
		commListView = (ScrollListView) findViewById(R.id.scllv_wish_comment);
//		commListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				commentedUserId = commlisList.get((int) arg3).getCommUserInfo()
//						.getUserId();
//				isCommAMan = true;
//			new CommentUtil();
//			}
//		});
		pickImgBtn = (ImageButton) findViewById(R.id.imgbt_pickoff);
		pickImgBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				try {
					LastPick lastPick = DbUtil.getDbUtils(WishDetailsActivity.this).findFirst(Selector.from(LastPick.class)
							.where(LastPick.USERID,"=",new SPHelper(WishDetailsActivity.this).GetUserId()));
					if(lastPick!=null){
						String lastTime = lastPick.getTimeStr();
						String[] lasttimeStrings = lastTime.split("-");
						String nowtimeMString = DataTraslator.longToFormatString(new Date().getTime(), "MM");
						String nowtimeDString = DataTraslator.longToFormatString(new Date().getTime(), "dd");
						if(Integer.parseInt(nowtimeMString)-Integer.parseInt(lasttimeStrings[0])==0){
							if(Integer.parseInt(nowtimeDString)-Integer.parseInt(lasttimeStrings[1])<=0){
								MyToast.showToast(WishDetailsActivity.this, "每天只能摘下一个心愿哦");
								return;
							}
						}
					}
				} catch (Exception e) {
					
				}
				
				if(wish.getSolveId()>0){
					MyToast.showToast(WishDetailsActivity.this,"已经被摘走了~");
					return;
				}

				int myUserId = new SPHelper(WishDetailsActivity.this)
						.GetUserId();
				if (myUserId == girlUserInfo.getUserId()) {
					MyToast.showToast(WishDetailsActivity.this, "这是自己的心愿哦");
					return;
				}

				String userSex = new SPHelper(WishDetailsActivity.this).get(
						"sex").toString();
				if (userSex != null && userSex.equals("女")) {
					MyToast.showToast(WishDetailsActivity.this,
							"只有男生才能实现女生的心愿哦");
					return;
				}
				dialog2.show();

			}
		});
		flowerImgBtn = (ImageButton) findViewById(R.id.imgbt_flower);
		flowerImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					new GiveHeartEvent(WishDetailsActivity.this,girlUserInfo.getUserId(), wishId).exeute();
				} catch (Exception e) {
				}
				
			}
		});
		commImgBtn = (ImageButton) findViewById(R.id.imgbt_comment);
		commImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isCommAMan = false;
				new CommentUtil();
			}
		});

		imageLoader = ImageLoader.getInstance();
		commlisList = new ArrayList<WishDetailModel>();
		adapter = new WishDetailAdapter(WishDetailsActivity.this, commlisList,
				imageLoader,getOwnerId());
		commListView.setAdapter(adapter);
		
		isrefreshComm = false;
		loadingDialogShow();
		new GetWishDetail().execute();

	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	private class GetWishDetail extends AsyncTask<Void, Void, Object> {
		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.WISH_DETAIL;

			try {
				int ddd = wishId;
				response = ServerCommunication.requestWithoutEncrypt(wishId, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}

			DetailWishJsonObject refreshPR = null;
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						DetailWishJsonObject.class);
			} catch (Exception e) {
				return "";
			}

			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			loadingDialogDismiss();

			// Call onRefreshComplete when the list has been refreshed.
			if (result == null || !ServerCommunication.checkResult(WishDetailsActivity.this,
					result)) {
				MyToast.showToast(WishDetailsActivity.this, "服务器出错啦~");
				return;
			}

			if (result.equals("")) {
				MyToast.showToast(WishDetailsActivity.this, "服务器出错啦~");
				return;
			}

			DetailWishJsonObject wishJsonObject = (DetailWishJsonObject) result;
			girlUserInfo = wishJsonObject.getMyUserInfo();
			wish = wishJsonObject.getWish();
			
			SharedPreferences sharedPre = getSharedPreferences(Constant.INEEDUSPR,
							Context.MODE_PRIVATE);
			int userId = sharedPre.getInt(Constant.ID, -1);

			if(girlUserInfo.getUserId()==userId){
				pickersTxtVu.setVisibility(View.VISIBLE);
			}
			
			if (!isrefreshComm) {
		
				imageLoader.displayImage(wish.getImageUrl(), backgroundImgVu,options);
				if (wish.getAuth() == 0) {
					nameTxtVu.setText("匿名"+"  "+wishJsonObject.getUserDetailInfo().getAcademy());
					headImgVu.setOnClickListener(new View.OnClickListener() {	
						@Override
						public void onClick(View arg0) {
							SharedPreferences sharedPre = WishDetailsActivity.this
									.getSharedPreferences(Constant.INEEDUSPR,
											Context.MODE_PRIVATE);
							int userId = sharedPre.getInt(Constant.ID, -1);
							MyIntent.toChatActivity(WishDetailsActivity.this, userId, girlUserInfo.getUserId(),
									"匿名聊天", true);
						}
					});
				} else if(wish.getAuth() == 1){
					imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL
							+ girlUserInfo.getUserId() + ".png", headImgVu,options_head);
					
					nameTxtVu.setText(girlUserInfo.getNickName()+"  "+wishJsonObject.getUserDetailInfo().getAcademy());
					headImgVu.setOnClickListener(new View.OnClickListener() {	
						@Override
						public void onClick(View arg0) {
							new HeadClickEvent(WishDetailsActivity.this, girlUserInfo.getUserId()).click();
						}
					});
				}
				try {
					int distance = DataTraslator
							.GetDistanceToMe(WishDetailsActivity.this,
									wish.getLat(), wish.getLng());
					distanceTxtVu.setText(DataTraslator.DistanceToString(distance));			
				} catch (Exception e) {
					distanceTxtVu.setText("出错了 :(");
				}
				try {
					timeTxtVu.setText(DataTraslator.LongToTimePastGeneral(Long.parseLong(wish.getTime())));
				} catch (Exception e) {
					timeTxtVu.setText("出错了 :(");
				}
				contentTxtVu.setText(wish.getContent());
				wishNumTxtVu.setText("#"+wish.getId());
			}

			int num = wishJsonObject.getWishComments().size();
			
			setOwnerId(wishJsonObject.getMyUserInfo().getUserId());
			WishDetailModel model = null;
			commlisList.clear();
			for (int i = 0; i < num; i++) {
				model = new WishDetailModel();
				model.setCommUserInfo(wishJsonObject.getCommentUserInfos().get(
						i));
				model.setWishComment(wishJsonObject.getWishComments().get(i));
				model.setCommUserLocation(wishJsonObject.getCommUserLocations().get(i));
				commlisList.add(model);
			}

			//adapter.notifyDataSetChanged();
			adapter = new WishDetailAdapter(WishDetailsActivity.this, commlisList,
					imageLoader,getOwnerId());
			commListView.setAdapter(adapter);

			if (isrefreshComm) {
				Handler handler = new Handler();
				handler.post(new Runnable() {
					@Override
					public void run() {
						scrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});

			}

			isFirstIn = false;
			isrefreshComm = true;
			super.onPostExecute(result);

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

		public CommentUtil() {
			init();
		}

		private void init() {
			view1 = LayoutInflater.from(WishDetailsActivity.this).inflate(R.layout.dialog_comment,
					null);
			myAlertDialog = new AlertDialog.Builder(WishDetailsActivity.this).setView(view1)
					.create();
			// 调整评论条的位置和大小
			Window w = myAlertDialog.getWindow();
			w.setGravity(Gravity.BOTTOM);
			w.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT,
					android.view.WindowManager.LayoutParams.WRAP_CONTENT);

			view2 = LayoutInflater.from(WishDetailsActivity.this).inflate(R.layout.dialog_comment,
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
					InputMethodManager imm = (InputMethodManager) WishDetailsActivity.this
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
					SimpleAdapter simpleAdapter = new SimpleAdapter(WishDetailsActivity.this,
							listItems, R.layout.faceitem,
							new String[] { "emotion" }, new int[] { R.id.emotion });
					gridView.setAdapter(simpleAdapter);
					DisplayMetrics dm = new DisplayMetrics();
					((Activity) WishDetailsActivity.this).getWindowManager().getDefaultDisplay()
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
									WishDetailsActivity.this.getResources(), imageIds[arg2
											% imageIds.length]);
							bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true); 
							ImageSpan imageSpan = new ImageSpan(
									((Activity) WishDetailsActivity.this), bitmap);
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
				

				SharedPreferences sharedPre = WishDetailsActivity.this
						.getSharedPreferences(Constant.INEEDUSPR,
								Context.MODE_PRIVATE);
				int userId = sharedPre.getInt(Constant.ID, -1);

				WishComment wishComment = new WishComment();
				wishComment.setContent(commentString);
				wishComment.setTime(String.valueOf(commentTiemLong));
				wishComment.setUserId(userId);
				wishComment.setWishId(wishId);
				if (isCommAMan) {
					wishComment.setCommentedUserId(commentedUserId);
				} else {
					wishComment.setCommentedUserId(-1);
				}
				commentedUserId = -1;
				isCommAMan = false;
				boolean resbool = false;
				try {
					response = ServerCommunication.request(wishComment,
							URLConstant.WISH_COMMENT_WISH);
					resbool = JacksonUtil.json().fromJsonToObject(response,
							Boolean.class);;
				} catch (Exception e) {
					e.printStackTrace();
					return e.getMessage();
				}
				return resbool;
			}

			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);

				loadingDialogDismiss();
				if(result == null){
					MyToast.showToast(WishDetailsActivity.this, "加载失败");
					return;
				}

				if (!ServerCommunication.checkResult(WishDetailsActivity.this,
						result)) {
					MyToast.showToast(WishDetailsActivity.this, (String) result);
					return;
				}
				// 处理返回的结果
				Boolean _resultBoolean = (Boolean) result;
				if (_resultBoolean) {
					MyToast.showToast(WishDetailsActivity.this, "评论成功");
				}
				isrefreshComm = true;
				new GetWishDetail().execute();
			}
		}

	}
	private class PickTheWish extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.WISH_JOIN;
			JsonObject jObject = new JsonObject();
			jObject.setInt1(wishId);
			int boy = new SPHelper(WishDetailsActivity.this).GetUserId();
			jObject.setInt2(girlUserInfo.getUserId());
			jObject.setInt3(boy);
			
			try {
				response = ServerCommunication.request(jObject, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			int refreshPR ;
			try {
				 refreshPR = JacksonUtil.json()
							.fromJsonToObject(response,Integer.class);
			} catch (Exception e) {
				refreshPR = -1;
			}
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {
			
			super.onPostExecute(result);

			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication.checkResult(WishDetailsActivity.this,
					result)) {
				MyToast.showToast(WishDetailsActivity.this, "好像有问题:(~");
				return;
			}

			int res = (Integer)result;
			if (res==-1) {
				MyToast.showToast(WishDetailsActivity.this, "似乎有问题:(");
			}else if(res==0){
				MyToast.showToast(WishDetailsActivity.this, "你已经加入了");
			}else {
				MyToast.showToast(WishDetailsActivity.this, "已加入队列");
				new Thread(){
					@Override
					public void run() {
						LastPick lastPick = new LastPick();
						String hh = DataTraslator.longToFormatString(new Date().getTime(), "MM-dd");
						lastPick.setTimeStr(hh);
						lastPick.setUserId(new SPHelper(WishDetailsActivity.this).GetUserId());
						try {
							DbUtil.getDbUtils(WishDetailsActivity.this).save(lastPick);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							SharedPreferences sharedPre = WishDetailsActivity.this.getSharedPreferences(
									Constant.INEEDUSPR, 0);
							Editor editor = sharedPre.edit();
							editor.putString(SPHelper.PICK_LAST_TIME,hh);
							editor.commit();
						}
					};
				}.start();
			
			}
		}
	}
	
	
	
}
