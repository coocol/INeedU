package com.eethan.ineedu.primaryactivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
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

import com.baidu.location.f;
import com.eethan.ineedu.adapter.MessageListAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.im.Notice;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.im.XmppUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.manager.MessageManager;
import com.eethan.ineedu.manager.NoticeManager;
import com.eethan.ineedu.model.IMMessage;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.PullDownView;
import com.eethan.ineedu.util.PullDownView.OnPullDownListener;
import com.eethan.ineedu.util.WebTime;


public class ChatActivity extends BaseActivity implements OnPullDownListener{

	private static final String PAGE_NAME = "聊天";
	private String userChat = "";// 当前聊天 userChat,即friendJid
	private List<IMMessage> message_pool = new LinkedList<IMMessage>();
	private String userId;// 自己的id
	private String friendId;//好友id
	private String friendName;// 对方姓名
	private EditText msgText;
	private TextView chat_name;
	private NotificationManager mNotificationManager;
	private ChatManager cm;
	
	private ImageButton mBtnBack;
	private ImageButton btsend;
	private ImageButton emotionbButton;

	Chat newchat;
	
	private boolean isFirstComeHere = true;
	
	
	private ListView mListView;   //核心的listview
	private MessageListAdapter adapter;  //核心的listview的设配器
	private PullDownView mPullDownView;  //包含下拉刷新，上拉加载的listview，通过getListView()可得到里面的核心listview
	private int pageIndex;
	private int[] imageIds = new int[Constant.NUMOFFACE];
	private HorizontalScrollView hsview;
	private PullDownView outView;
	
	private boolean isHidden = false;//是否匿名
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_page);
		setPageName("ChatActivity");
		isFirstComeHere = true;
		init();
		findView();
		receivedMsg();// 接收消息
		sendMsg();// 发送消息
	}
		

	private void init() {
		//
		pageIndex = 1;
		mNotificationManager = (NotificationManager) this
				.getSystemService(Service.NOTIFICATION_SERVICE);
		
		
		
		// 获取Intent传过来的用户名
		this.userId = getIntent().getExtras().getString("USERID");
		this.friendId =  getIntent().getExtras().getString("FRIENDID");
		this.userChat = XmppUtil.getUserIdToJid( getIntent().getExtras().getString("FRIENDID"));
		this.friendName =  getIntent().getExtras().getString("FRIEND_NAME");
		this.isHidden = getIntent().getExtras().getBoolean("isHidden");
		/*
		 * System.out.println("接收消息的用户pFRIENDID是：" + userChat);
		 * System.out.println("发送消息的用户userId是：" + userId);
		 * System.out.println(" 消息的用户pFRIENDID是：" + pFRIENDID);
		 */
		
		// 消息监听
		try {
			cm = XmppConnection.getConnection().getChatManager();
			//创建新对话
			newchat = cm.createChat(userChat, null);
			

		} catch (Exception e) {
			MyToast.showToast(ChatActivity.this,"抱歉，聊天服务器出错了");
		}
	

		chat_name = (TextView) findViewById(R.id.chat_page_username_text);
		if(!isHidden)
			chat_name.setText(friendName);
		else//匿名
			chat_name.setText("Ta");

	}
	
	private void findView(){
		// 返回按钮
		mBtnBack = (ImageButton) findViewById(R.id.chat_page_back_button);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
//		// 发送消息
//		btsend = (ImageButton) findViewById(R.id.chat_page_send_button);
//		
//		btsend.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String message = msgText.getText().toString();
//				if ("".equals(message)) {
//					MyToast.showToast(ChatActivity.this, "请输入内容~", false);
//				} else {
//
//					try {
//						sendMessage(message);
//						msgText.setText("");
//					} catch (Exception e) {
//						MyToast.showToast(ChatActivity.this, "无法连接服务器~", false);
//						msgText.setText(message);
//						Log.d("TA", e.toString());
//					}
//				}
//			}
//		});
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
    	outView = (PullDownView) findViewById(R.id.Activity_Talk_Pull_Down_View);
    	
        outView.getListView().setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInput(msgText);
				hsview.setVisibility(View.GONE);
				return false;
			}
		});
        
        msgText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hsview.setVisibility(View.GONE);
				return false;
			}
		});
        
        mPullDownView = (PullDownView) findViewById(R.id.Activity_Talk_Pull_Down_View);
    	
    	mPullDownView.setOnPullDownListener(this);
    	
    	mListView = mPullDownView.getListView();
    	adapter = new MessageListAdapter(this, message_pool, mListView,isHidden);
    	mListView.setAdapter(adapter);
    	mListView.setDivider(null);//去掉listview中分割线
    	
        //设置可以自动获取更多 滑到最后一个自动获取  改成false将禁用自动获取更多
      	mPullDownView.enableAutoFetchMore(false, 1);
      	//隐藏 并禁用尾部
      	mPullDownView.setHideFooter();
      	//显示并启用自动获取更多
      	//mPullDownView.setShowFooter();
      	//隐藏并且禁用头部刷新
      	//mPullDownView.setHideHeader();
      	//显示并且可以使用头部刷新
      	mPullDownView.setShowHeader();
				
	}
	
	/**
	 * 发送消息
	 * 
	 * @author Administrator
	 * 
	 */
	public void sendMsg() {
		// 发送消息
		ImageButton btsend = (ImageButton) findViewById(R.id.chat_page_send_button);
		// 发送消息给pc服务器的好友（获取自己的服务器，和好友）
		newchat = cm.createChat(userChat, null);
		btsend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取text文本
				String time = DateUtil.getStringTime();
				final String messageContent = msgText.getText().toString();
				if (messageContent.length() > 0) {
					
					if(isFirstComeHere){
						new Thread(new Runnable() {	
							@Override
							public void run() {
								JsonObject jsonObject = new JsonObject();
								jsonObject.setInt1(Integer.parseInt(userId));
								jsonObject.setInt2(Integer.parseInt(friendId));
								jsonObject.setInt3(Constant.CONTACT_CHAT_TYPE);
								try {
									jsonObject.setString1(String.valueOf(WebTime.getTime()));
								} catch (Exception e) {
									jsonObject.setString1(String.valueOf(DateUtil.getMSTime()));
								}
								try {
									String reString = ServerCommunication.request(jsonObject, URLConstant.ADD_CHAT_CONTACT);
									String dd = reString;
								} catch (PostException e) {
								}
							}
						}).start();
					}
					isFirstComeHere = false;
					
					// 自己显示消息
					IMMessage saveMessage = new IMMessage();
					saveMessage.setUserid(userId);
					saveMessage.setType(IMMessage.MSG_TYPE[1]);
					saveMessage.setWith(XmppUtil.getJidToUserId(newchat.getParticipant()));//把自己的id发给对方，相对于对方就是with
					saveMessage.setContent(messageContent);
					saveMessage.setTime(time);
					
					message_pool.add(saveMessage);
					MessageManager.getInstance(ChatActivity.this).saveMsg(saveMessage);
					// MChatManager.message_pool.add(newMessage);

					// 刷新视图
					
 					//发送对方
					IMMessage sendMessage = new IMMessage();
					sendMessage.setUserid(userId);
					sendMessage.setType(IMMessage.MSG_TYPE[0]);//对方收
					sendMessage.setWith(userId);//把自己的id发给对方，相对于对方就是with
					if(isHidden){
						sendMessage.setContent("noname//"+messageContent);
					}else {
						sendMessage.setContent(messageContent);
					}
					
					sendMessage.setTime(time);
					
					// 刷新适配器
 					refreshMessage(message_pool);
					try {
						// 发送消息
						newchat.sendMessage(IMMessage.toJson(sendMessage));

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					MyToast.showToast(ChatActivity.this, "请输入内容~", false);
				}
				// 清空text
				msgText.setText("");
			}
		});
	}

	/**
	 * 接收消息
	 */
	public void receivedMsg() {

		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat2, Message message) {
						// 收到来自pc服务器的消息（获取自己好友发来的信息）
						if (message.getFrom().contains(userChat)) {
							// Msg.analyseMsgBody(message.getBody(),userChat);
							// 获取用户、消息、时间、IN
							/*
							 * String[] args = new String[] { userChat,
							 * message.getBody(), TimeRender.getDate(), "IN" };
							 */
							// 在handler里取出来显示消息
							android.os.Message msg = handler.obtainMessage();
							System.out.println("服务器发来的消息是 chat："
									+ message.getBody());
							msg.what = 1;
							msg.obj = message.getBody();
							msg.sendToTarget();

						}
					}
				});
			}
		});
	}


	private void sendMessage(String messageContent) throws Exception {

		String time = DateUtil.getStringTime();
		
		IMMessage sendMessage = new IMMessage();
		sendMessage.setUserid(userId);
		sendMessage.setType(IMMessage.MSG_TYPE[0]);//对方收
		sendMessage.setWith(userId);//把自己的id发给对方，相对于对方就是with
		sendMessage.setContent(messageContent);
		sendMessage.setTime(time);
		try {
			// 发送消息
			
			newchat.sendMessage(IMMessage.toJson(sendMessage));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IMMessage saveMessage = new IMMessage();
		saveMessage.setUserid(userId);
		saveMessage.setType(IMMessage.MSG_TYPE[1]);
		saveMessage.setWith(XmppUtil.getJidToUserId(newchat.getParticipant()));//把自己的id发给对方，相对于对方就是with
		saveMessage.setContent(messageContent);
		saveMessage.setTime(time);
		
		message_pool.add(saveMessage);
		MessageManager.getInstance(this).saveMsg(saveMessage);
		// MChatManager.message_pool.add(newMessage);

		// 刷新视图
		refreshMessage(message_pool);

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
				ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
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


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		pageIndex++;
		List<IMMessage> list = MessageManager.getInstance(this).getMessageList(friendId, pageIndex, Constant.PAGESIZE);
		message_pool.addAll(0, list);
		this.refreshMessageHistory(message_pool, list.size());
		/** 关闭 刷新完毕 ***/
		mPullDownView.RefreshComplete();
	}


	@Override
	public void onMore() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		Constant.is_chatting = true;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		//registerReceiver(receiver, filter);
		//更新其人的消息为已读
		NoticeManager.getInstance(this).updateStatusByFrom(friendId, Notice.READ);
		message_pool = MessageManager.getInstance(this).getMessageList(friendId, pageIndex, Constant.PAGESIZE);
		this.refreshMessage(message_pool);
		//MobclickAgent.onPageStart(PAGE_NAME);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		//unregisterReceiver(receiver);
		pageIndex = 1;
		//MobclickAgent.onPageEnd(PAGE_NAME);
		Constant.is_chatting = false;
		super.onPause();
	}
	
	
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
//				Log.d("TA", "获得消息");
//				IMMessage message = intent
//						.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
//				if(message.getWith().equals(friendId)) {
//					receiveNewMessage(message);
//					refreshMessage(message_pool);
//				}
//			}
//		}
//	};
	
	
	private void receiveNewMessage(IMMessage message) {
		message_pool.add(message);
	}

	private void refreshMessage(List<IMMessage> messages) {
		adapter.refreshList(messages);
	}
	
	private void refreshMessageHistory(List<IMMessage> messages, int temp) {
		adapter.refreshHistory(messages, temp);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				IMMessage chatMsg = IMMessage.analyseMsgBody(msg.obj.toString());
				if (chatMsg != null) {
					receiveNewMessage(chatMsg);
					refreshMessage(message_pool);
				}

			default:
				break;
			}
		};
	};

	
}