package com.eethan.ineedu.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smackx.carbons.Carbon.Private;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.MoodComment;
import com.eethan.ineedu.databasebeans.NeedComment;
import com.eethan.ineedu.databasebeans.PlaysComment;
import com.eethan.ineedu.databasebeans.PourlistenComment;
import com.eethan.ineedu.databasebeans.TakePhotosComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.WishComment;
import com.eethan.ineedu.jackson.DetailPhotosJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.MoodModel;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.model.PhotoNewsDetailsModel;
import com.eethan.ineedu.model.PhotoNewsModel;
import com.eethan.ineedu.model.PlaysModel;
import com.eethan.ineedu.model.PourListenActivityModel;
import com.eethan.ineedu.model.WishModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.IssuePourListenActivity;
import com.eethan.ineedu.primaryactivity.PhotoNewsDetailActivity;
import com.eethan.ineedu.primaryactivity.R;

/**
 * 在评论按钮的事件里面直接new这个类就可以完成浮动评论框
 * 
 * @author shaw
 * 
 */
public class CommentUtil {

	private Context mContext;
	private View view1;
	private View view2;
	private AlertDialog myAlertDialog;
	private GridView gridView;
	private int[] imageIds;
	private HorizontalScrollView hsview;
	private BaseAdapter adapter;
	private EditText commentEdit;
	private ImageButton faceBtn;
	private int whichId;
	private int position;
	private int whoId;
	private TextView commentNumTextView;
	private List list;
	private int COMMMENT_FLAG;
	//
	private ArrayList<NeedFragmentModel> needFragmentModels;
	private ArrayList<PourListenActivityModel> pourListenActivityModels;
	private ArrayList<PhotoNewsModel> photoNewsModels;
	private ArrayList<PlaysModel> playsModels;
	private ArrayList<WishModel> wishModels;
	private ArrayList<MoodModel> moodModels;
	private String orignConString = "";
	
	private LoadingDialog loadingDialog;

	/**
	 * 由于接口不好,所以实现起来特别恶心
	 * 
	 * @param _context
	 *            上下文
	 * @param _whichId
	 *            对哪一条进行评论
	 * @param _position
	 *            对第几条进行评论，为了在客户端直接把评论的数目+1
	 * @param _commentNumTextView
	 *            评论成功以后需要更新的textView
	 * @param _whoId
	 *            默认是对事件进行评论值为-1，如果对人进行回复那么这个就是那个人的id
	 * @param COMMENT_FLAG
	 *            对Need进行评论还是对PourListen进行评论,是对详细界面评论还是在listView那个界面评论
	 */
	public CommentUtil(Context _context, int _whichId, int _whoId,
			TextView _commentNumTextView, int COMMNET_FLAG) {// 这个是在详细页面用的评论够着函数
		this.mContext = _context;
		this.whichId = _whichId;
		this.whoId = _whoId;
		this.commentNumTextView = _commentNumTextView;
		this.COMMMENT_FLAG = COMMNET_FLAG;

		loadingDialog = new LoadingDialog(_context);

		init();
	}
	
	public CommentUtil(Context _context, int _whichId, int _whoId,
			TextView _commentNumTextView, int COMMNET_FLAG,String orignConsString) {// 这个是在详细页面用的评论够着函数
		this.mContext = _context;
		this.whichId = _whichId;
		this.whoId = _whoId;
		this.commentNumTextView = _commentNumTextView;
		this.COMMMENT_FLAG = COMMNET_FLAG;
		this.orignConString = orignConsString;

		loadingDialog = new LoadingDialog(_context);

		init();
	}

	public CommentUtil(Context _context, int _position, int _who, List _list,
			BaseAdapter _adapter, int COMMNET_FLAG) {// 这个是在listView页面用的构造函数
		this.mContext = _context;
		this.position = _position;
		this.whoId = _who;
		this.list = _list;
		this.adapter = _adapter;
		this.COMMMENT_FLAG = COMMNET_FLAG;

		loadingDialog = new LoadingDialog(_context);

		init();
	}

	public CommentUtil(Context _context, int _who) {
		loadingDialog = new LoadingDialog(_context);
	}

	private void init() {
		view1 = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment,
				null);
		myAlertDialog = new AlertDialog.Builder(mContext).setView(view1)
				.create();
		// 调整评论条的位置和大小
		Window w = myAlertDialog.getWindow();
		w.setGravity(Gravity.BOTTOM);
		w.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		view2 = LayoutInflater.from(mContext).inflate(R.layout.dialog_comment,
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
				InputMethodManager imm = (InputMethodManager) mContext
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
				SimpleAdapter simpleAdapter = new SimpleAdapter(mContext,
						listItems, R.layout.faceitem,
						new String[] { "emotion" }, new int[] { R.id.emotion });
				gridView.setAdapter(simpleAdapter);
				DisplayMetrics dm = new DisplayMetrics();
				((Activity) mContext).getWindowManager().getDefaultDisplay()
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
								mContext.getResources(), imageIds[arg2
										% imageIds.length]);
						bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true); 
						ImageSpan imageSpan = new ImageSpan(
								((Activity) mContext), bitmap);
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
				loadingDialog.show();
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
			

			SharedPreferences sharedPre = mContext.getSharedPreferences(
					Constant.INEEDUSPR, Context.MODE_PRIVATE);
			int userId = sharedPre.getInt(Constant.ID, -1);

			switch (COMMMENT_FLAG) {
			case Constant.COMMENT_NEED:// 评论need
				needFragmentModels = (ArrayList<NeedFragmentModel>) list;
				// -1不用管，随便填的，服务器会生成这个id
				needComment = new NeedComment(-1, needFragmentModels.get(
						position).getId(), userId, whoId, commentTiemLong,
						commentString);

				try {
					response = ServerCommunication.request(needComment,
							URLConstant.COMMENT_NEED_URL);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_POUR_LISTEN:// 评论倾诉倾听
				pourListenActivityModels = (ArrayList<PourListenActivityModel>) list;
				pourlistenComment = new PourlistenComment(-1,
						pourListenActivityModels.get(position).getId(), userId,
						whoId, commentTiemLong, commentString);
				try {
					response = ServerCommunication.requestWithoutEncrypt(pourlistenComment,
							URLConstant.COMMENT_POURLISTEN_URL);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_NEED_DETAIL:// 评论need的详细界面
				needComment = new NeedComment(-1, whichId, userId, whoId,
						commentTiemLong, commentString);
				try {
					response = ServerCommunication.request(needComment,
							URLConstant.COMMENT_NEED_URL);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_POUR_LISTEN_DETAIL:// 评论倾诉倾听的详细界面
				pourlistenComment = new PourlistenComment(-1, whichId, userId,
						whoId, commentTiemLong, commentString);
				try {
					response = ServerCommunication.requestWithoutEncrypt(pourlistenComment,
							URLConstant.COMMENT_POURLISTEN_URL);
				} catch (PostException e) {
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_PHOTONEWS_DETAIL:
				takePhotosComment = new TakePhotosComment(position, userId,
						whoId, String.valueOf(commentTiemLong), commentString);
				try {
					response = ServerCommunication.request(takePhotosComment,
							URLConstant.COMMENT_PHOTOS_URL);
				} catch (PostException e) {
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_PHOTONEWS:
				photoNewsModels = (ArrayList<PhotoNewsModel>) list;
				takePhotosComment = new TakePhotosComment(photoNewsModels
						.get(position).getTakePhotos().getId(), userId, whoId,
						String.valueOf(commentTiemLong), commentString);
				try {
					response = ServerCommunication.request(takePhotosComment,
							URLConstant.COMMENT_PHOTOS_URL);
					String s = response;
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.COMMENT_MOOD_PHOTO:
				moodModels = (ArrayList<MoodModel>) list;
				takePhotosComment = new TakePhotosComment(moodModels
						.get(position).getMood().getId(), userId, whoId,
						String.valueOf(commentTiemLong), commentString);
				try {
					response = ServerCommunication.request(takePhotosComment,
							URLConstant.COMMENT_PHOTOS_URL);
					String s = response;
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				break;
			case Constant.TRANSMIT_PHOTONEWS_DETAIL:
				takePhotosComment = new TakePhotosComment(whichId, userId,
						whoId, String.valueOf(commentTiemLong), "转发: "+commentString);
				JsonObject jsonObject_detail = new JsonObject();
				jsonObject_detail.setInt1(whichId);
				jsonObject_detail.setInt2(new SPHelper(mContext).GetUserId());
				jsonObject_detail.setDouble1(LocateManager.getInstance()
						.getLontitude());
				jsonObject_detail.setDouble2(LocateManager.getInstance()
						.getLatitude());
				try {
					jsonObject_detail.setLong1(WebTime.getTime());
				} catch (Exception e) {
					jsonObject_detail.setLong1(DateUtil.getMSTime());
				}
				
				jsonObject_detail.setString1(orignConString);
				try {
					String response1 = ServerCommunication.request(
							takePhotosComment, URLConstant.COMMENT_PHOTOS_URL);
					String response2 = ServerCommunication.request(
							jsonObject_detail, URLConstant.TRANSMIT_PHOTOS_URL);
					JacksonUtil.json().fromJsonToObject(response1,
							Boolean.class);
					if (!ServerCommunication.checkResult(
							mContext,
							JacksonUtil.json().fromJsonToObject(response1,
									Boolean.class)
									|| !ServerCommunication.checkResult(
											mContext,
											JacksonUtil.json()
													.fromJsonToObject(
															response2,
															Boolean.class)))) {
						MyToast.showToast(mContext, "转发失败");
						return Constant.ConnectException;
					}
					return true;
				} catch (PostException e) {
					// TODO Auto-generated catch block
					MyToast.showToast(mContext, "转发失败");
					e.printStackTrace();
					return e.getMessage();
				}
			case Constant.TRANSMIT_PHOTONEWS: {
				photoNewsModels = (ArrayList<PhotoNewsModel>) list;
				takePhotosComment = new TakePhotosComment(photoNewsModels
						.get(position).getTakePhotos().getId(), userId, whoId,
						String.valueOf(commentTiemLong), commentString);

				// TODO Auto-generated method stub
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(photoNewsModels.get(position)
						.getTakePhotos().getId());
				jsonObject.setInt2(new SPHelper(mContext).GetUserId());
				jsonObject.setDouble1(LocateManager.getInstance()
						.getLontitude());
				jsonObject
						.setDouble2(LocateManager.getInstance().getLatitude());
				jsonObject.setLong1(DateUtil.getMSTime());
				jsonObject.setString1(photoNewsModels.get(position)
						.getTakePhotos().getContent());
				try {
					String response1 = ServerCommunication.request(
							takePhotosComment, URLConstant.COMMENT_PHOTOS_URL);
					String response2 = ServerCommunication.request(jsonObject,
							URLConstant.TRANSMIT_PHOTOS_URL);
					JacksonUtil.json().fromJsonToObject(response1,
							Boolean.class);
					if (!ServerCommunication.checkResult(
							mContext,
							JacksonUtil.json().fromJsonToObject(response1,
									Boolean.class)
									|| !ServerCommunication.checkResult(
											mContext,
											JacksonUtil.json()
													.fromJsonToObject(
															response2,
															Boolean.class)))) {
						MyToast.showToast(mContext, "转发失败");
						return Constant.ConnectException;
					}
					return true;
				} catch (PostException e) {
					// TODO Auto-generated catch block
					MyToast.showToast(mContext, "转发失败");
					e.printStackTrace();
					return e.getMessage();
				}
			}
			case Constant.TRANSMIT_PHOTONEWS_MOOD: {
				moodModels = (ArrayList<MoodModel>) list;
				takePhotosComment = new TakePhotosComment(moodModels
						.get(position).getMood().getId(), userId, whoId,
						String.valueOf(commentTiemLong), commentString);

				// TODO Auto-generated method stub
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(moodModels.get(position)
						.getMood().getId());
				jsonObject.setInt2(new SPHelper(mContext).GetUserId());
				jsonObject.setDouble1(LocateManager.getInstance()
						.getLontitude());
				jsonObject
						.setDouble2(LocateManager.getInstance().getLatitude());
				jsonObject.setLong1(DateUtil.getMSTime());
				jsonObject.setString1(moodModels.get(position)
						.getMood().getContent());
				try {
					String response1 = ServerCommunication.request(
							takePhotosComment, URLConstant.COMMENT_PHOTOS_URL);
					String response2 = ServerCommunication.request(jsonObject,
							URLConstant.TRANSMIT_PHOTOS_URL);
					JacksonUtil.json().fromJsonToObject(response1,
							Boolean.class);
					if (!ServerCommunication.checkResult(
							mContext,
							JacksonUtil.json().fromJsonToObject(response1,
									Boolean.class)
									|| !ServerCommunication.checkResult(
											mContext,
											JacksonUtil.json()
													.fromJsonToObject(
															response2,
															Boolean.class)))) {
						MyToast.showToast(mContext, "转发失败");
						return Constant.ConnectException;
					}
					return true;
				} catch (PostException e) {
					// TODO Auto-generated catch block
					MyToast.showToast(mContext, "转发失败");
					e.printStackTrace();
					return e.getMessage();
				}
			}
			case Constant.COMMENT_PLAYS:
				{
					playsModels = (ArrayList<PlaysModel>)list;
					PlaysComment playsComment = new PlaysComment();
					playsComment.setUserId(userId);
					playsComment.setContent(commentString);
					playsComment.setTime(String.valueOf(commentTiemLong));
					playsComment.setPlaysId(playsModels.get(position).getPlays().getId());
					playsComment.setCommentedUserId(-1);
					try {
						response = ServerCommunication.request(playsComment,
								URLConstant.PLAYS_COMMENT_PALY);
					} catch (PostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return e.getMessage();
					}
				}
				break;
			case Constant.COMMENT_WISH:
			{
				wishModels = (ArrayList<WishModel>)list;
				WishComment wishComment = new WishComment();
				wishComment.setContent(commentString);
				wishComment.setTime(String.valueOf(commentTiemLong));
				wishComment.setUserId(userId);
				wishComment.setCommentedUserId(-1);
				wishComment.setWishId(wishModels.get(position).getWish().getId());
				try {

					response = ServerCommunication.request(wishComment,
							URLConstant.WISH_COMMENT_WISH);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
			}
			break;
			case Constant.COMMENT_MOOD:
			{
				moodModels = (ArrayList<MoodModel>)list;
				MoodComment moodComment = new MoodComment();
				moodComment.setContent(commentString);
				moodComment.setTime(String.valueOf(commentTiemLong));
				moodComment.setUserId(userId);
				moodComment.setCommentedUserId(-1);
				moodComment.setMoodId(moodModels.get(position).getMood().getId());
				try {

					response = ServerCommunication.request(moodComment,
							URLConstant.COMMENT_MOOD_URL);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
			}
			break;
			default:
				break;

			}
			try {
				if(response==null){
					return true;
				}
				return JacksonUtil.json().fromJsonToObject(response, Boolean.class);
			} catch (Exception e) {
				return true;
			}
			
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			loadingDialog.dismiss();
			
			if(result==null){
				MyToast.showToast(mContext,"加载失败");
				return;
			}

			if (!ServerCommunication.checkResult(mContext, result)) {
				MyToast.showToast(mContext, (String) result);
				return;
			}
			// 处理返回的结果
			Boolean _resultBoolean = (Boolean) result;
			if (_resultBoolean) {// 评论成功,评论数目+1
				switch (COMMMENT_FLAG) {
				case Constant.COMMENT_NEED:
					needFragmentModels.get(position)
							.setCommentNum(
									needFragmentModels.get(position)
											.getCommentNum() + 1);
					adapter.notifyDataSetChanged();
					break;
				case Constant.COMMENT_POUR_LISTEN:
					pourListenActivityModels.get(position).setNumOfComment(
							pourListenActivityModels.get(position)
									.getNumOfComment() + 1);
					adapter.notifyDataSetChanged();
					break;
				case Constant.COMMENT_POUR_LISTEN_DETAIL:
				case Constant.COMMENT_NEED_DETAIL:
					NeedDetailUIAction();
					break;
				case Constant.COMMENT_PHOTONEWS_DETAIL:
					new RefreshComms().execute();
					break;
				case Constant.COMMENT_PHOTONEWS: {
					int num = photoNewsModels.get(position).getTakePhotos()
							.getCommentNum() + 1;
					photoNewsModels.get(position).getTakePhotos()
							.setCommentNum(num);
					adapter.notifyDataSetChanged();
				}
					break;
				case Constant.TRANSMIT_PHOTONEWS_DETAIL:
					MyToast.showToast(mContext, "转发成功");
					return;
				case Constant.TRANSMIT_PHOTONEWS: {
					int num = photoNewsModels.get(position).getTakePhotos()
							.getCommentNum() + 1;
					int num2 = photoNewsModels.get(position).getTakePhotos()
							.getTransmitNum() + 1;
					photoNewsModels.get(position).getTakePhotos()
							.setCommentNum(num);
					photoNewsModels.get(position).getTakePhotos()
							.setTransmitNum(num2);
					adapter.notifyDataSetChanged();
					MyToast.showToast(mContext, "转发成功");
					return;
				}
				case Constant.COMMENT_PLAYS:
					int num = playsModels.get(position).getPlays().getCommentNum();
					playsModels.get(position).getPlays().setCommentNum(num+1);
					adapter.notifyDataSetChanged();
					break;
				case Constant.COMMENT_WISH:
					int num2 = wishModels.get(position).getWish().getCommentNum();
					wishModels.get(position).getWish().setCommentNum(num2+1);
					adapter.notifyDataSetChanged();
					break;
				case Constant.COMMENT_MOOD:
					int num3 = moodModels.get(position).getMood().getCommentNum();
					moodModels.get(position).getMood().setCommentNum(num3+1);
					adapter.notifyDataSetChanged();
					break;
				case Constant.COMMENT_MOOD_PHOTO:
					int num4 = moodModels.get(position).getMood().getCommentNum();
					moodModels.get(position).getMood().setCommentNum(num4+1);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
				//MyToast.showToast(mContext, "评论成功");
				SharedPreferences sharedPre = mContext.getSharedPreferences(
						Constant.INEEDUSPR, Context.MODE_PRIVATE);
				int userId = sharedPre.getInt(Constant.ID, -1);
				new AddLoveNum(mContext, 1,userId).execute();
			} else {// 评论失败
				MyToast.showToast(mContext, "评论失败");
			}
		}

	}

	// NeedDetail服务器数据获取后进行的UI刷新操作
	public void NeedDetailUIAction() {
		if (commentNumTextView != null)
			commentNumTextView.setText(String.valueOf(Integer
					.parseInt(commentNumTextView.getText().toString()) + 1));
	}

	public void PhotoDetailUIAction() {

	}

	private class RefreshComms extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.DETAIL_PHOTOS_URL;
			try {
				response = ServerCommunication.request(position, URL);// 发送请求，获得数据
			} catch (PostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			DetailPhotosJsonObject refreshPR;
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						DetailPhotosJsonObject.class);
			} catch (Exception e) {
				return "fail";
			}

			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			if (result==null || result.equals("fail")) {
				return;
			}
			DetailPhotosJsonObject res = (DetailPhotosJsonObject) result;

			ArrayList<TakePhotosComment> commLists = (ArrayList<TakePhotosComment>) res
					.getPhotosComments();
			ArrayList<UserInfo> commUserLists = (ArrayList<UserInfo>) res
					.getCommentUserInfos();
			int num = commLists.size();
			PhotoNewsDetailsModel model = null;
			PhotoNewsDetailActivity.commList.clear();
			for (int i = 0; i < num; i++) {
				model = new PhotoNewsDetailsModel();
				model.setTakePhotosComment(commLists.get(i));
				model.setUserInfo(commUserLists.get(i));
				PhotoNewsDetailActivity.commList.add(model);
			}
			adapter.notifyDataSetChanged();

			final ScrollView scrollView = (ScrollView) ((Activity) mContext)
					.findViewById(R.id.photonews_details_scroll);
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});

			super.onPostExecute(result);
		}
	}

}
