package com.eethan.ineedu.service;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.NotificationType;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.NotificationJsonObject;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.NeedDetailActivity;
import com.eethan.ineedu.primaryactivity.NotificationActivity;
import com.eethan.ineedu.primaryactivity.PhotoNewsDetailActivity;
import com.eethan.ineedu.primaryactivity.PlaysDetailsActivity;
import com.eethan.ineedu.primaryactivity.PourListenDetailActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.primaryactivity.WishDetailsActivity;
import com.eethan.ineedu.setting.ShakeAndSound;

public class NoticeService extends Service {

	public final static int NEED = 1;// 1代表评论
	public final static int POURLISTEN = 2;// 2代表回复
	public final static int GIVEHEART = 3;// 2代表点赞

	public final static int YES = 100;// 100代表没事
	public final static int NO = 101;// 101代表没事

	private List<com.eethan.ineedu.databasebeans.Notification> notifications;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case NO:
				new Thread(mTasks).start();
				break;
			case YES:
				NotificationJsonObject notificationJsonObject = (NotificationJsonObject) msg.obj;
				notifications = notificationJsonObject.getNotifications();

				int x = 0;
				int needId[] = new int[notifications.size()];
				int needReplyNum[] = new int[notifications.size()];
				int y = 0;
				int PourlistenId[] = new int[notifications.size()];
				int PourlistenReplyNum[] = new int[notifications.size()];
				int z = 0;
				int wishIds[] = new int[notifications.size()];
				int wishReplyNum[] = new int[notifications.size()];

				int flowerNum = 0;// 送花

				int photoId = 0;
				int photoCommentNum = 0;
				int photoTransmitNum = 0;
				int photoPraiseNum = 0;

				int playId = 0;
				int playCommentNum = 0;
				int playJoinNum = 0;
				
				int wishCommNum = 0;
				int wishId = -1;

				int numOfPlay = 0;

				for (int i = 0; i < notifications.size(); i++) {
					int type = Math.abs(notifications.get(i).getNoticeFlag());
					switch (type) {
					case NotificationType.FLOWER_WAIT:
						flowerNum++;
						break;
					case NotificationType.PHOTO_COMMENT_WAIT:
						photoCommentNum++;
						photoId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.PHOTO_PRAISE_WAIT:
						photoPraiseNum++;
						photoId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.PHOTO_TRANSMIT_WAIT:
						photoTransmitNum++;
						photoId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.PLAY_COMMENT_WAIT:
						playCommentNum++;
						playId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.WISH_COMMENT_WAIT:
						wishCommNum++;
						wishId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.PARTICIPATE_WAIT:
						playJoinNum++;
						playId = notifications.get(i).getNotificationId();
						break;
					case NotificationType.NEED_NOTIFY_WAIT:
						String userSex = getSharedPreferences(
								Constant.INEEDUSPR, 0).getString(Constant.SEX,
								"");
						String cc = "";
						if (userSex.equals("男")) {
							cc = "你附近有妹子发了一条见面o(≧v≦)o";
						} else if (userSex.equals("女")) {
							cc = "你附近有帅哥发了一条见面o(≧v≦)o";
						} else {
							cc = "你附近有人发了一条见面o(≧v≦)o";
						}
						sendNotifiNearNeed(R.drawable.logo_icon, cc, "点击查看",
								"needId", notifications.get(i)
										.getNotificationId(), "userId",
								notifications.get(i).getUserId(),
								NeedDetailActivity.class,
								NotificationType.NEED_NOTIFY_WAIT);
						// new
						// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
						numOfPlay++;
						break;
					case NotificationType.WISH_NOTIFY_WAIT:

						cc = "你附近有妹子发了一条心愿o(≧v≦)o";

						sendNotifiNearNeed(R.drawable.logo_icon, cc, "点击查看",
								"wishId", notifications.get(i)
										.getNotificationId(), "userId",
								notifications.get(i).getUserId(),
								WishDetailsActivity.class,
								NotificationType.NEED_NOTIFY_WAIT);
						// new
						// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
						numOfPlay++;
						break;
					case NotificationType.WISH_ACCEPT_WAIT:

						cc = "恭喜你的心愿被摘走o(≧v≦)o";	

						sendNotifiNearNeed(R.drawable.logo_icon, cc, "点击前往通知查看他的电话",
								"wishId", notifications.get(i)
										.getNotificationId(), "userId",
								notifications.get(i).getUserId(),
								NotificationActivity.class,
								NotificationType.NEED_NOTIFY_WAIT);
						// new
						// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
						numOfPlay++;
						break;
					case NotificationType.WISH_TO_BOY_WAIT:

						cc = "你摘取了女孩的心愿o(≧v≦)o";	

						sendNotifiNearNeed(R.drawable.logo_icon, cc, "点击前往通知查看她的电话",
								"wishId", notifications.get(i)
										.getNotificationId(), "userId",
								notifications.get(i).getUserId(),
								NotificationActivity.class,
								NotificationType.NEED_NOTIFY_WAIT);
						// new
						// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
						numOfPlay++;
						break;
					case NotificationType.WISH_JOIN_WAIT:

						cc = "有人想实现你的心愿o(≧v≦)o";	

						sendNotifiNearNeed(R.drawable.logo_icon, cc, "点击查看",
								"wishId", notifications.get(i)
										.getNotificationId(), "userId",
								notifications.get(i).getUserId(),
								NotificationActivity.class,
								NotificationType.NEED_NOTIFY_WAIT);
						// new
						// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
						numOfPlay++;
						break;
					default:
						break;
					}

				}
				for (int i = 0; i < notifications.size(); i++) {
					needId[i] = -1;
					needReplyNum[i] = 0;
					PourlistenId[i] = -1;
					PourlistenReplyNum[i] = 0;
					wishIds[i]=-1;
					wishReplyNum[i]=0;
				}

				for (int i = 0; i < notifications.size(); i++) {
					switch (notifications.get(i).getNoticeFlag()) {
					case NEED: {
						int j;
						for (j = 0; j < x; j++) {
							if (needId[j] == notifications.get(i)
									.getNotificationId()) {
								needReplyNum[j]++;
								break;
							}
						}
						if (j == x) {
							needId[x] = notifications.get(i)
									.getNotificationId();
							needReplyNum[j]++;
							x++;
						}
						break;
					}
					case NotificationType.WISH_COMMENT_WAIT: {
						int j;
						for (j = 0; j < z; j++) {
							if (wishIds[j] == notifications.get(i)
									.getNotificationId()) {
								wishReplyNum[j]++;
								break;
							}
						}
						if (j == z) {
							wishIds[z] = notifications.get(i)
									.getNotificationId();
							wishReplyNum[j]++;
							z++;
						}
						break;
					}
					case POURLISTEN: {
						int j;
						for (j = 0; j < y; j++) {
							if (PourlistenId[j] == notifications.get(i)
									.getNotificationId()) {
								PourlistenReplyNum[j]++;
								break;
							}
						}
						if (j == y) {
							PourlistenId[y] = notifications.get(i)
									.getNotificationId();
							PourlistenReplyNum[j]++;
							y++;
						}
						break;
					}
					case GIVEHEART:

						break;
					default:
						break;
					}

				}

				for (int i = 0; i < x; i++) {
					sendNeedNotifi(R.drawable.logo_icon, "你有" + needReplyNum[i]
							+ "条见面的新回复(╯3╰)", "点击查看", needId[i], notifications
							.get(i).getUserId(), NeedDetailActivity.class,
							needId[i] + 100);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
//				private void sendNotifiWithExtra(int iconId, String contentTitle,
//						String contentText, String extraName, int notificationId,
//						Class activity, int notiId) {
				for (int i = 0; i < z; i++) {
					sendNotifiWithExtra(R.drawable.logo_icon, "你的心愿有新的回复(╯3╰)", "点击查看","wishId",wishIds[i], WishDetailsActivity.class, notifications
							.get(i).getNotificationId());
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}

				for (int i = 0; i < y; i++) {
					sendPourlistenNotifi(R.drawable.logo_icon, "你有"
							+ PourlistenReplyNum[i] + "条我想说的新回复(╯3╰)", "点击查看",
							PourlistenId[i], PourListenDetailActivity.class,
							PourlistenId[i] + 300);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}

				if (flowerNum != 0) {
					sendGiveHeartNotifi(R.drawable.logo_icon, flowerNum
							+ "个人给你送过花^_^~", "点击查看", NotificationActivity.class);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (photoCommentNum != 0) {
					sendNotifiWithExtra(R.drawable.logo_icon, "你的照片有"
							+ photoCommentNum + "条新回复(╯3╰)", "点击查看", "photoId",
							photoId, PhotoNewsDetailActivity.class,
							NotificationType.PHOTO_COMMENT_WAIT);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (photoPraiseNum != 0) {
					sendNotifiWithExtra(R.drawable.logo_icon, "有"
							+ photoPraiseNum + "个人赞了你的照片╭(′▽`)╯", "点击查看",
							"photoId", photoId, PhotoNewsDetailActivity.class,
							NotificationType.PHOTO_PRAISE_WAIT);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (photoTransmitNum != 0) {
					sendNotifiWithExtra(R.drawable.logo_icon, "有"
							+ photoTransmitNum + "个人转发了你的照片╰(￣▽￣)╮", "点击查看",
							"photoId", photoId, PhotoNewsDetailActivity.class,
							NotificationType.PHOTO_TRANSMIT_WAIT);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (playCommentNum != 0) {
					sendNotifiWithExtra(R.drawable.logo_icon, "你的活动有"
							+ playCommentNum + "条新回复(╯3╰)", "点击查看", "playId",
							playId, PlaysDetailsActivity.class,
							NotificationType.PLAY_COMMENT_WAIT);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (playJoinNum != 0) {
					sendNotifiWithExtra(R.drawable.logo_icon, "有" + playJoinNum
							+ "个人加入了你发起的活动o(≧v≦)o", "点击查看", "playId", playId,
							PlaysDetailsActivity.class,
							NotificationType.PARTICIPATE_WAIT);
					// new
					// ShakeAndSound(getApplicationContext()).PlayShakeAndSound();
					numOfPlay++;
				}
				if (numOfPlay > 0)
					new ShakeAndSound(getApplicationContext())
							.PlayShakeAndSound();
				new Thread(mTasks).start();
				break;
			default:
				new Thread(mTasks).start();
				break;
			}
			
		}
		
	};

	private Runnable mTasks = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			try {
				Thread.sleep(Constant.CHECK_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int userId = getSharedPreferences(Constant.INEEDUSPR,
					Context.MODE_PRIVATE).getInt(Constant.ID, -1);

			String result = null;
			try {
				result = ServerCommunication.request(userId,
						URLConstant.CHECK_NOTIFICATION_URL);
			} catch (PostException e) {
				e.printStackTrace();
				return;
			}

			msg.what = NO;

			if (result != null) {
				try {
					NotificationJsonObject notificationJsonObject = JacksonUtil
							.json().fromJsonToObject(result,
									NotificationJsonObject.class);
					if (notificationJsonObject.getResult() == true) {
						msg.what = YES;
						msg.obj = notificationJsonObject;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			mHandler.sendMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		new Thread(mTasks).start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private void sendNeedNotifi(int iconId, String contentTitle,
			String contentText, int notificationId, int userid, Class activity,
			int notiId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("needId", notificationId);
		notifyIntent.putExtra("userd", userid);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this).setAutoCancel(true).setSmallIcon(iconId)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(appIntent)
				.setDefaults(Notification.DEFAULT_SOUND);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notiId, nBuilder.build());
	}

	private void sendPourlistenNotifi(int iconId, String contentTitle,
			String contentText, int notificationId, Class activity, int notiId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("pourlistenId", notificationId);

		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this).setAutoCancel(true).setSmallIcon(iconId)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(appIntent)
				.setDefaults(Notification.DEFAULT_SOUND);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notiId, nBuilder.build());
	}

	private void sendGiveHeartNotifi(int iconId, String contentTitle,
			String contentText, Class activity) {
		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);

		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this).setAutoCancel(true).setSmallIcon(iconId)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(appIntent)
				.setDefaults(Notification.DEFAULT_LIGHTS);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(3, nBuilder.build());
	}

	private void sendNotifiWithExtra(int iconId, String contentTitle,
			String contentText, String extraName, int notificationId,
			Class activity, int notiId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra(extraName, notificationId);

		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this).setAutoCancel(true).setSmallIcon(iconId)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(appIntent)
				.setDefaults(Notification.DEFAULT_SOUND);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notiId, nBuilder.build());
	}

	private void sendNotifiNearNeed(int iconId, String contentTitle,
			String contentText, String extraName, int notificationId,
			String extraName1, int noticedId, Class activity, int notiId) {

		/*
		 * 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		 */
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra(extraName, notificationId);
		notifyIntent.putExtra(extraName1, noticedId);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this).setAutoCancel(true).setSmallIcon(iconId)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setContentIntent(appIntent)
				.setDefaults(Notification.DEFAULT_SOUND);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notiId, nBuilder.build());
	}
}
