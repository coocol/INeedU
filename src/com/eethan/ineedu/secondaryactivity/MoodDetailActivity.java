package com.eethan.ineedu.secondaryactivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.MoodLikeEvent;
import com.eethan.ineedu.CommonUse.PhotoLikeEvent;
import com.eethan.ineedu.adapter.MoodDetailAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Mood;
import com.eethan.ineedu.databasebeans.MoodComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.DetailMoodJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.MoodDetailModel;
import com.eethan.ineedu.model.MoodLike;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.WebTime;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MoodDetailActivity extends BaseActivity {

	private ImageButton backButton;
	private ImageButton commButton;
	private ImageButton flowerButton;
	private ImageButton ownerHeadImgBtn;
	private ImageView ownerSexImfVu;
	private TextView atNameTxtVu;
	private TextView timetxtVu;
	private TextView ownerNameTxtVu;
	private TextView contentTxtVu;
	private ImageButton likeImgBtn;
	private TextView distanceTxtVu;

	public static List<MoodDetailModel> commModelList;
	private ScrollListView commListView;
	private MoodDetailAdapter adapter;

	public static boolean isScrollToBottom = false;
	
	private int commentedUserId = -1;

	private UserInfo ownerUserInfo;
	private Mood mood;
	private ArrayList<UserInfo> commUserLists;
	private ArrayList<UserLocation> commUserLocationLists;
	private ArrayList<MoodComment> commLists;

	private int userId;
	private int moodId;
	
	private boolean isCommAMan = false;
	private boolean isFirstIn = false;

	private boolean isFirstJumpToHere = true;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mood_detail);

		userId = getIntent().getIntExtra("userId", -1);
		moodId = getIntent().getIntExtra("moodId", -1);

		options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.ic_stub_horizon)
				// .showImageForEmptyUri(R.drawable.ic_error_horizon)
				// .showImageOnFail(R.drawable.ic_error_horizon)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_head = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		ownerHeadImgBtn = (ImageButton) findViewById(R.id.head);
		ownerSexImfVu = (ImageView) findViewById(R.id.sex);
		ownerNameTxtVu = (TextView) findViewById(R.id.nickname);
		atNameTxtVu = (TextView) findViewById(R.id.at_nickname);
		timetxtVu = (TextView) findViewById(R.id.time);
		contentTxtVu = (TextView) findViewById(R.id.content);
		likeImgBtn = (ImageButton) findViewById(R.id.bt_praise);
		commListView = (ScrollListView) findViewById(R.id.lv_comment);
		backButton = (ImageButton) findViewById(R.id.photonews_imgbt_back);
		commButton = (ImageButton) findViewById(R.id.bt_comment);
		flowerButton = (ImageButton) findViewById(R.id.bt_flower);
		distanceTxtVu = (TextView)findViewById(R.id.tv_distance);
		commModelList = new ArrayList<MoodDetailModel>();
		adapter = new MoodDetailAdapter(getApplicationContext(), commModelList,userId);
		commListView.setAdapter(adapter);

		ownerHeadImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ownerUserInfo != null && ownerUserInfo.getUserId() > 0) {
					new HeadClickEvent(getApplicationContext(), ownerUserInfo
							.getUserId()).click();
				}
			}
		});
		
		ownerNameTxtVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						MoodDetailActivity.class);
				intent.putExtra("userId", ownerUserInfo.getUserId());
				startActivity(intent);
			}
		});
	
		commButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isCommAMan = false;
				new CommentUtil();
//				new CommentUtil(MoodDetailActivity.this, mood
//						.getId(), -1, commLists, adapter,
//						Constant.COMMENT_PHOTONEWS_DETAIL);
			}
		});
		flowerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new GiveHeartEvent(getApplicationContext(), ownerUserInfo.getUserId(), moodId).start();
			}
		});
		likeImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new MoodLikeEvent(getApplicationContext(), mood.getUserId(), moodId).start();
			}
		});
	
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		commListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				isCommAMan = true;
				commentedUserId = commModelList.get((int) arg3).getUserInfo()
						.getUserId();
				new CommentUtil();
				

			}
		});

		if (isFirstJumpToHere) {
			loadingDialogShow();
		}

		new GetDataTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isFirstJumpToHere = true;
	};

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.DETAIL_MOOD_URL;
			int x = moodId;
			try {
				response = ServerCommunication.requestWithoutEncrypt(moodId, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			DetailMoodJsonObject refreshPR;
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						DetailMoodJsonObject.class);
			} catch (Exception e) {
				return null;
			}

			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			if (result==null) {
				MyToast.showToast(MoodDetailActivity.this, "加载失败");
				loadingDialogDismiss();
				return;
			}

			if (!ServerCommunication.checkResult(MoodDetailActivity.this,
					result)) {
				MyToast.showToast(MoodDetailActivity.this, (String) result);
				loadingDialogDismiss();
				return;
			}

			DetailMoodJsonObject res = (DetailMoodJsonObject) result;

			ownerUserInfo = res.getMyUserInfo();
		
			commLists = (ArrayList<MoodComment>) res.getMoodComments();
			commUserLists = (ArrayList<UserInfo>) res.getCommentUserInfos();
			commUserLocationLists = (ArrayList<UserLocation>) res.getCommentUserLocations();
			mood = res.getMood();
			if (ownerUserInfo != null) {
				imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL
						+ ownerUserInfo.getUserId() + ".png", ownerHeadImgBtn,
						options_head);
				ownerNameTxtVu.setText(ownerUserInfo.getNickName()+"  "+res.getUserDetailInfo().getAcademy());
				if (ownerUserInfo.getSex().equals("女"))
					ownerSexImfVu.setImageResource(R.drawable.sex_girl_press);
				else {
					ownerSexImfVu.setImageResource(R.drawable.sex_boy_press);
				}
			}
			String readable_time = DataTraslator.LongToTimePastGeneral(Long
					.parseLong(mood.getTime()));
			timetxtVu.setText(readable_time);
			distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(MoodDetailActivity.this, mood.getLat(), mood.getLng())));
			
			if (mood != null) {
				contentTxtVu.setText(mood.getContent());
				
			}
			commModelList.clear();
			int num = commLists.size();
			MoodDetailModel model = null;
			int curUserId;
			for (int i = 0; i < num; i++) {

				model = new MoodDetailModel();
				model.setMoodComment(commLists.get(i));
				model.setUserInfo(commUserLists.get(i));
				model.setUserLocation(commUserLocationLists.get(i));
				commModelList.add(model);

			}

			if (isFirstJumpToHere) {
				loadingDialogDismiss();
			}
			isFirstJumpToHere = false;

			adapter.notifyDataSetChanged();
			final ScrollView scrollView = (ScrollView) findViewById(R.id.photonews_details_scroll);
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_UP);
				}
			});
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
			view1 = LayoutInflater.from(MoodDetailActivity.this).inflate(R.layout.dialog_comment,
					null);
			myAlertDialog = new AlertDialog.Builder(MoodDetailActivity.this).setView(view1)
					.create();
			// 调整评论条的位置和大小
			Window w = myAlertDialog.getWindow();
			w.setGravity(Gravity.BOTTOM);
			w.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT,
					android.view.WindowManager.LayoutParams.WRAP_CONTENT);

			view2 = LayoutInflater.from(MoodDetailActivity.this).inflate(R.layout.dialog_comment,
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
					InputMethodManager imm = (InputMethodManager) MoodDetailActivity.this
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
					SimpleAdapter simpleAdapter = new SimpleAdapter(MoodDetailActivity.this,
							listItems, R.layout.faceitem,
							new String[] { "emotion" }, new int[] { R.id.emotion });
					gridView.setAdapter(simpleAdapter);
					DisplayMetrics dm = new DisplayMetrics();
					((Activity) MoodDetailActivity.this).getWindowManager().getDefaultDisplay()
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
									MoodDetailActivity.this.getResources(), imageIds[arg2
											% imageIds.length]);
							bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true); 
							ImageSpan imageSpan = new ImageSpan(
									((Activity) MoodDetailActivity.this), bitmap);
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
				

				SharedPreferences sharedPre = MoodDetailActivity.this
						.getSharedPreferences(Constant.INEEDUSPR,
								Context.MODE_PRIVATE);
				int userId = sharedPre.getInt(Constant.ID, -1);
				
				MoodComment moodComment = new MoodComment();
				moodComment.setCommentedUserId(-1);
				moodComment.setContent(commentString);
				moodComment.setTime(String.valueOf(commentTiemLong));
				moodComment.setMoodId(moodId);
				moodComment.setUserId(userId);

				
				if (isCommAMan) {
					moodComment.setCommentedUserId(commentedUserId);
				} else {
					moodComment.setCommentedUserId(-1);
				}
				commentedUserId = -1;
				isCommAMan = false;
				boolean resbool = false;
				try {
					response = ServerCommunication.request(moodComment,
							URLConstant.COMMENT_MOOD_URL);
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
					MyToast.showToast(MoodDetailActivity.this, "加载失败");
					return;
				}

				if (!ServerCommunication.checkResult(MoodDetailActivity.this,
						result)) {
					MyToast.showToast(MoodDetailActivity.this, (String) result);
					return;
				}
				// 处理返回的结果
				boolean _resultBoolean = (Boolean) result;
				if (_resultBoolean) {
					MyToast.showToast(MoodDetailActivity.this, "评论成功");
				}
				new GetDataTask().execute();
			}
		}

	}
	


}
