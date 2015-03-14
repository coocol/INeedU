package com.eethan.ineedu.service;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.im.Notice;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.im.XmppUtil;
import com.eethan.ineedu.manager.MessageManager;
import com.eethan.ineedu.model.IMMessage;
import com.eethan.ineedu.primaryactivity.ChatActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.setting.ShakeAndSound;
import com.eethan.ineedu.util.DateUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * 聊天服务
 */
public class IMChatService extends Service {

	private Context mContext;
	private MessageManager messageManager;
	private NotificationManager notificationManager;
	
	private String contentString;
	private String nameString;
	private boolean isHidden = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		initChatManager();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//XmppConnection.getConnection()
				//.removePacketListener(packetListener);
				XmppConnection.getConnection().getChatManager().removeChatListener(chatManagerListner);
			}
		}).start();
		
		super.onDestroy();
	}
	
	/**
	 * 初始化,包括初始化MessageManager,NoticeManager,notificationManager,XMPPConnection, addPacketListener
	 */
	private void initChatManager() {
		messageManager = MessageManager.getInstance(mContext);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
	
		
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				XMPPConnection conn = XmppConnection.getConnection();
				//conn.addPacketListener(packetListener, new MessageTypeFilter(
				//		Message.Type.chat));
				conn.getChatManager().addChatListener(chatManagerListner);
			}
		}).start();	
		
	}
	
	//消息监听器,所有人发的消息
	ChatManagerListener  chatManagerListner  = new ChatManagerListener() {
		
		@Override
		public void chatCreated(Chat chat, boolean arg1) {
			// TODO Auto-generated method stub
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat chat2, Message message) {
					
					if (message != null && message.getBody() != null
							&& !message.getBody().equals("null")) {
						//要把这个from转换为真实姓名
						String from = message.getFrom().split("/")[0].split("@")[0];
						
						
						String content = message.getBody();
						String userid = "";
						
						try {
							JSONObject jsonObject = new JSONObject(content);
							content = jsonObject.getString(IMMessage.CONTENT);
							userid = jsonObject.getString(IMMessage.USERID);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(content.contains("noname//")){
							content = content.replace("noname//", "");
							isHidden = true;
							contentString = content;
						}
						long curtime = DateUtil.getMSTime();
						//生成IMMessage
						IMMessage msg = new IMMessage(userid,IMMessage.MSG_TYPE[0], from, content);
						messageManager.saveMsg(msg);//保存IMMessage
						// 生成通知
						
						Notice notice = new Notice(Notice.CHAT_MSG, "私信", content, from,
								curtime, Notice.UNREAD);
						//这里不保存会话消息
							Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
							intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
							intent.putExtra(Notice.NOTICE_KEY, notice);
						
							sendBroadcast(intent);
							Log.d("IMCS",msg.getWith());
							if(!Constant.is_chatting) {
								sendNotification(R.drawable.logo_icon, notice.getContent()
										,ChatActivity.class, msg.getWith(), (int) 0);
								new ShakeAndSound(mContext).PlayShakeAndSound();
							}
									
					}

					
				}
			});
		}
	};
	

	/**
	 * 消息监听器,注意这里只监听接收的消息,客户端发送消息时不会广播
	 */
	//好友消息
	PacketListener packetListener = new PacketListener() {

		@Override
		public void processPacket(Packet arg0) {
			Message message = (Message) arg0;
			if (message != null && message.getBody() != null
					&& !message.getBody().equals("null")) {
				//要把这个from转换为真实姓名
				String from = message.getFrom().split("/")[0].split("@")[0];
				
				
				String content = message.getBody();
				String userid = "";
				
				try {
					JSONObject jsonObject = new JSONObject(content);
					content = jsonObject.getString(IMMessage.CONTENT);
					userid = jsonObject.getString(IMMessage.USERID);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(content.contains("noname//")){
					content = content.replace("noname//", "");
					isHidden = true;
					contentString = content;
				}
				long curtime = DateUtil.getMSTime();
				//生成IMMessage
				IMMessage msg = new IMMessage(userid,IMMessage.MSG_TYPE[0], from, content);
				messageManager.saveMsg(msg);//保存IMMessage
				// 生成通知
			
				Notice notice = new Notice(Notice.CHAT_MSG, "私信", content, from,
						curtime, Notice.UNREAD);
				//这里不保存会话消息
					Intent intent = new Intent(Constant.NEW_MESSAGE_ACTION);
					intent.putExtra(IMMessage.IMMESSAGE_KEY, msg);
					intent.putExtra(Notice.NOTICE_KEY, notice);
					
				
					sendBroadcast(intent);
					Log.d("IMCS",msg.getWith());
					if(!Constant.is_chatting) {
						sendNotification(R.drawable.logo_icon, notice.getContent()
								,ChatActivity.class, msg.getWith(), (int) 0);
						new ShakeAndSound(mContext).PlayShakeAndSound();
					}
							
			}
		}
	};

	/**
	 * 发出通知
	 * @param iconId 图标
	 * @param contentTitle 通知标题
	 * @param contentText 通知内容
	 * @param activity 要启动的activity
	 * @param from 对方的userId,注意是IneedU服务器的userId
	 * @param notiId 通知id
	 */
	private void sendNotification(int iconId,
			String contentText, Class activity, String from, int notiId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		
		//启动需要的数据
		// notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String userName = XmppUtil.getUserNameFromUserId(Integer.parseInt(from));
		SharedPreferences myPreferences = getSharedPreferences(Constant.INEEDUSPR, MODE_PRIVATE);
		String userId = String.valueOf(myPreferences.getInt(Constant.ID, -1));
		if(isHidden){
			userName = "匿名用户";
			notifyIntent.putExtra("isHidden", isHidden);
		}
		isHidden = false;
		notifyIntent.putExtra("USERID",userId);//自己的id
		notifyIntent.putExtra("FRIENDID",from);//对方的id
		notifyIntent.putExtra("FRIEND_NAME",userName);//对方的姓名
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(mContext)
			.setAutoCancel(true).setSmallIcon(iconId).setContentTitle(userName)
			.setContentText(contentText).setContentIntent(appIntent)
			.setDefaults(Notification.DEFAULT_SOUND);
		notificationManager.notify(notiId, nBuilder.build());
	}
}
