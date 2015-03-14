package com.eethan.ineedu.primaryactivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.MolestAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Molest;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.MolestJsonObject;
import com.eethan.ineedu.model.MolestModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.WebTime;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MolestActivity extends BaseActivity{
	private EditText msgText;
	private TextView chat_name;
	
	private ImageButton mBtnBack;
	private ImageButton btsend;
	private ImageButton emotionbButton;

	
	
//	private ListView mListView;   //核心的listview
	private MolestAdapter adapter;  //核心的listview的设配器
	private PullToRefreshListView mPullDownView;  //包含下拉刷新，上拉加载的listview，通过getListView()可得到里面的核心listview
	private int[] imageIds = new int[Constant.NUMOFFACE];
	private HorizontalScrollView hsview;
	private ArrayList<MolestModel> dataSource = new ArrayList<MolestModel>();
	private int lastNum = 0;
	
	private final int COMMIT_SUCCESS = 1;
	private final int COMMIT_FAILED = 2;
	private final int REFRESH = 3;
	private final int COMMIT_ERROR = 4;
	
	private int commentedUserId = -1;
	
	private int debgunum = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.molest_page);
		setPageName("MolestActivity");
		loadingDialogShow();
		init();
		findView();
		refreshMessage();
	}
		

	private void init() {
		// 获取Intent传过来的用户名
		
		chat_name = (TextView) findViewById(R.id.chat_page_username_text);
		
		chat_name.setText("伊伊反馈群");
		
//		receivedMsg();// 接收消息
//		sendMsg();// 发送消息

	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			//mPullDownView.getRefreshableView().setSelection(dataSource.size()-1);
			switch (msg.what) {
			case COMMIT_SUCCESS:
				//MyToast.showToast(getApplicationContext(), "调戏成功~t");
				refreshMessage();
				hideSoftInput(msgText);
				msgText.setText("");
				break;
			case COMMIT_FAILED:
				MyToast.showToast(getApplicationContext(), "调戏失败~");
				break;
			case REFRESH:
				try {
					
					loadingDialogDismiss();
					mPullDownView.onRefreshComplete();
					adapter.notifyDataSetChanged();
					mPullDownView.getRefreshableView().setSelection(dataSource.size()-1);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				break;
			default:
				break;
			}
		};
	};
	@SuppressLint("CutPasteId")
	private void findView(){
		// 返回按钮
		mBtnBack = (ImageButton) findViewById(R.id.chat_page_back_button);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		// 发送消息
		btsend = (ImageButton) findViewById(R.id.chat_page_send_button);
		
		btsend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = msgText.getText().toString();
				if ("".equals(message)) {
					MyToast.showToast(MolestActivity.this, "请输入内容~", false);
				} else {

					try {
						commentedUserId = -1;
						sendMessage(message);
					} catch (Exception e) {
						MyToast.showToast(MolestActivity.this, "连接不到服务器了:(", false);
						msgText.setText(message);
						Log.d("TA", e.toString());
					}
				}
			}
		});
		//表情
		
		emotionbButton = (ImageButton)findViewById(R.id.chat_page_emoticon_button);
		emotionbButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FaceButton(v);
			}
		});
		msgText = (EditText) findViewById(R.id.chat_page_msg_edit_text);
		
		hsview = (HorizontalScrollView) findViewById(R.id.hsview);
        
        msgText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hsview.setVisibility(View.GONE);
				return false;
			}
		});
        
        mPullDownView = (PullToRefreshListView) findViewById(R.id.Activity_Talk_Pull_Down_View);
    	
        mPullDownView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				getMoreMessage();
			}
		});
        mPullDownView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        // 点击的是headerView或者footerView 
			        return; 
			    } 
				int realPosition=(int)id; 
				commentedUserId = dataSource.get(realPosition).getUserId();
				msgText.setHint("回复 "+dataSource.get(realPosition).getName()+": ");
				hideSoftInput(msgText);
			}
		});
        mPullDownView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInput(msgText);
				hsview.setVisibility(View.GONE);
				return false;
			}
		});
	    	adapter = new MolestAdapter(getContext(),dataSource);
	    	mPullDownView.setAdapter(adapter);
    	
				
	}

	private void sendMessage(final String messageContent) throws Exception {
		hideSoftInput(msgText);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int userId = new SPHelper(getContext()).GetUserId();
					//String content = msgText.getText().toString();
					String time;
					try {
						time = WebTime.getTime() + "";
					} catch (Exception e) {
						time = String.valueOf(new Date().getTime());
					}
					Molest molest = new Molest(userId, commentedUserId, messageContent, time);
					
					
					String response = ServerCommunication.requestWithoutEncrypt(molest, URLConstant.COMMIT_MOLEST_URL);
					Boolean object = JacksonUtil.json().
							fromJsonToObject(response, Boolean.class);
					Message message = mHandler.obtainMessage();
					if(object)
						message.what = COMMIT_SUCCESS;
					else
						message.what = COMMIT_FAILED;
					message.sendToTarget();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				
			}
		}).start();
		
	}
	
	private void refreshMessage() {
		// TODO Auto-generated method stub
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String response = ServerCommunication.requestWithoutEncrypt("第一次进入", URLConstant.SHOW_MOLEST_URL);
					dataSource.clear();
					dataHandle(response);
					Message message = mHandler.obtainMessage();
					message.what = REFRESH;
					message.sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		}).start();
		
	}

	private void getMoreMessage() {
		// TODO Auto-generated method stub
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String response = ServerCommunication.requestWithoutEncrypt(lastNum, URLConstant.GET_MORE_MOLEST_URL);
					dataHandle(response);
					Message message = mHandler.obtainMessage();
					message.what = REFRESH;
					message.sendToTarget();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		}).start();
		
	}
	private void dataHandle(String response)
	{
		if(debgunum==1){
			int x=1;
			x++;
			x--;
			int y =x;
			x = x+y;
		}
		debgunum++;
		if(response==null){
			Message msgMessage = mHandler.obtainMessage();
			msgMessage.what = COMMIT_FAILED;
			msgMessage.sendToTarget();
			return;
		}
		try {
			MolestJsonObject object = JacksonUtil.json().
					fromJsonToObject(response, MolestJsonObject.class);
			List<UserInfo> userInfos = object.getUserInfos();
			List<UserInfo> commentedUserInfos = object.getCommentedUserInfos();
			List<Molest> molests = object.getMolests();
			lastNum = object.getLastNum();
			
			List<MolestModel> tmp = new ArrayList<MolestModel>();
			for(int i = 0;i<molests.size();i++)
			{
				MolestModel model = new MolestModel();
				model.setUserId(userInfos.get(i).getUserId());
				if(molests.get(i).commentedUserId != -1)
					model.setCommentedUserName(commentedUserInfos.get(i).getNickName());
				model.setContent(molests.get(i).content);
				model.setFlag(userInfos.get(i).flag);
				model.setName(userInfos.get(i).getNickName());
				model.setTime(molests.get(i).time);
				
				tmp.add(model);
			}
			Collections.reverse(tmp);
			dataSource.addAll(0,tmp);
		} catch (Exception e) {
			Message msgMessage = mHandler.obtainMessage();
			msgMessage.what = COMMIT_FAILED;
			msgMessage.sendToTarget();
			return;
		}
		
	}
	/**
	 * 表情按钮的事件
	 */
	public void FaceButton(View view) {
		GridView gridView = (GridView) findViewById(R.id.facegrid);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
		//生成表情的id，封装
		for(int i = 0; i < Constant.NUMOFFACE; i++) {
			try {
				if(i<10){
					Field field = R.drawable.class.getDeclaredField("f00" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else if(i<100){
					Field field = R.drawable.class.getDeclaredField("f0" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else{
					Field field = R.drawable.class.getDeclaredField("f" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
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
	        Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("emotion", imageIds[i]);
			listItems.add(listItem);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.faceitem, new String[]{"emotion"}, new int[]{R.id.emotion});
		gridView.setAdapter(simpleAdapter);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int allWidth = 20*listItems.size()*(int)density;
		int itemWidth = 20*(int)density;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
		gridView.setLayoutParams(params);
		gridView.setColumnWidth(itemWidth);
		gridView.setNumColumns((int)((listItems.size()+3)/3));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				int index = msgText.getSelectionStart();
				bitmap = BitmapFactory.decodeResource(getResources(), imageIds[arg2 % imageIds.length]);
				bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true); 
				ImageSpan imageSpan = new ImageSpan(MolestActivity.this, bitmap);
				String str = null;
				if(arg2<10){
					str = "f00"+arg2;
				}else if(arg2<100){
					str = "f0"+arg2;
				}else{
					str = "f"+arg2;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				Editable editable = msgText.getEditableText();
				if(index <0 || index >= msgText.length()) {
					editable.append(spannableString);
				} else {
					editable.insert(index, spannableString);
				}
			}
		});
		hsview.setVisibility(View.VISIBLE);
		hideSoftInput(msgText);
	}

	@Override
	public void onBackPressed() {
		if(hsview.getVisibility() == View.VISIBLE) hsview.setVisibility(View.GONE);
		else super.onBackPressed();
	}


	/**
	 * 隐藏输入法
	 */
	private void hideSoftInput(EditText _edit) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(_edit.getWindowToken(), 0);
	}


//	@Override
//	public void onRefresh() {
//		// TODO Auto-generated method stub
//		getMoreMessage();
//		/** 关闭 刷新完毕 ***/
//		mPullDownView.RefreshComplete();
//	}
//
//
//	@Override
//	public void onMore() {
//		// TODO Auto-generated method stub
//		
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		//MobclickAgent.onPageEnd(PAGE_NAME);
		super.onPause();
	}
	
	
}
