package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.NotificationActivityAdapter;
import com.eethan.ineedu.constant.NotificationType;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Notification;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.AllNotificationsJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.NotificationActivityModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.WishJoinActivity;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NotificationActivity extends BaseActivity{
	public static final int REFRESH_OVER=1;
	public static final int GET_MORE=2;
	public static final int FAILED=3;
	
	public static int lastNum=0;
	public static boolean REFRESH_TASK=true;

	public int userId;
	ArrayList<Notification> notifications=new ArrayList<Notification>();
	ArrayList<UserInfo> userinfos=new ArrayList<UserInfo>();
	ArrayList<String> comments=new ArrayList<String>();
	private boolean result;
	
	private ImageButton backButton;
	private PullToRefreshListView mPullRefreshListView;
	public NotificationActivityAdapter adapter;
	ArrayList<NotificationActivityModel> dataSource=new ArrayList<NotificationActivityModel>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("NotificationActivity");
		setContentView(R.layout.notification_page);
		new GetDataTask().execute();
		Init();
		MyPullRefreshListViewListener();
	}
	
	private void MyPullRefreshListViewListener() {
			// TODO Auto-generated method stub
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				REFRESH_TASK=false;
				new GetDataTask().execute();
			}
		});
		
		
		//   item单击监听
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        // 点击的是headerView或者footerView 
			        return; 
			    } 
				int realPosition=(int)id; 
				
				Intent intent;
				int type=Math.abs(dataSource.get(realPosition).getTYPE());
				switch (type) {
				case NotificationType.NEED_COMMENT_WAIT:
					intent=new Intent(NotificationActivity.this,NeedDetailActivity.class);
					intent.putExtra("needId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.POURLISTEN_COMMENT_WAIT:
					intent=new Intent(NotificationActivity.this,PourListenDetailActivity.class);
					intent.putExtra("pourlistenId", dataSource.get(realPosition).getMessageId());
					intent.putExtra("userId", new SPHelper(NotificationActivity.this).GetUserId());
					startActivity(intent);		
					break;
				case NotificationType.FLOWER_WAIT:
					
					break;
				case NotificationType.PHOTO_COMMENT_WAIT:
				case NotificationType.PHOTO_PRAISE_WAIT:
				case NotificationType.PHOTO_TRANSMIT_WAIT:
					intent=new Intent(NotificationActivity.this,PhotoNewsDetailActivity.class);
					intent.putExtra("photoId", dataSource.get(realPosition).getMessageId());
					intent.putExtra("userId", new SPHelper(NotificationActivity.this).GetUserId());
					startActivity(intent);
					break;
				
				case NotificationType.PLAY_COMMENT_WAIT:
				case NotificationType.PARTICIPATE_WAIT:
					intent=new Intent(NotificationActivity.this,PlaysDetailsActivity.class);
					intent.putExtra("playId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.NEED_NOTIFY_WAIT:
					intent=new Intent(NotificationActivity.this,NeedDetailActivity.class);
					intent.putExtra("needId", dataSource.get(realPosition).getMessageId());
					intent.putExtra("userId", dataSource.get(realPosition).getReplyManId());
					startActivity(intent);
					break;
				case NotificationType.WISH_NOTIFY_WAIT:
					intent=new Intent(NotificationActivity.this,WishDetailsActivity.class);
					intent.putExtra("wishId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.WISH_COMMENT_WAIT:
					intent=new Intent(NotificationActivity.this,WishDetailsActivity.class);
					intent.putExtra("wishId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.WISH_ACCEPT_WAIT:
					intent=new Intent(NotificationActivity.this,WishDetailsActivity.class);
					intent.putExtra("wishId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.WISH_TO_BOY_WAIT:
					intent=new Intent(NotificationActivity.this,WishDetailsActivity.class);
					intent.putExtra("wishId", dataSource.get(realPosition).getMessageId());
					startActivity(intent);
					break;
				case NotificationType.WISH_JOIN_WAIT:
					intent=new Intent(NotificationActivity.this,WishJoinActivity.class);
					intent.putExtra("wishId", dataSource.get(realPosition).getMessageId());
					intent.putExtra("ownerId", dataSource.get(realPosition).getReplyManId());
					startActivity(intent);
					break;
				default:
					break;
				}
	
			}
		});
		
		//刷新监听
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(NotificationActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	
				// Do work to refresh the list here.
				REFRESH_TASK=true;
				new GetDataTask().execute();
				
			}
		});
		}
	
	private void Init() {
			// TODO Auto-generated method stub
		backButton=(ImageButton)findViewById(R.id.back_button);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.home_page_listview);
		adapter = new NotificationActivityAdapter(NotificationActivity.this,dataSource);
		mPullRefreshListView.setAdapter(adapter);
		
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		
		@Override
		protected Object doInBackground(Void... params) {
			int userId=new SPHelper(NotificationActivity.this).GetUserId();
			
			if(REFRESH_TASK)
				MyTimer.Start();
			Object object=(Integer)userId;
			String URL=URLConstant.GET_ALL_NOTIFICATIONS_URL;
			if(!REFRESH_TASK)
			{
				JsonObject jsonObject=new JsonObject();
				jsonObject.setInt1(userId);
				jsonObject.setInt2(lastNum);
				object=jsonObject;
				
				URL=URLConstant.GET_MORE_NOTIFICATIONS_URL;
			}
			
			String response;
			try {
				response=ServerCommunication.request(object, URL);
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
				
			AllNotificationsJsonObject result = null;
			try {
					result = JacksonUtil.json().fromJsonToObject(response,AllNotificationsJsonObject.class);
			} catch (Exception e) {
				return null;
			}
			if(REFRESH_TASK)
				MyTimer.RefreshDelay();
			return result;
		}

		
		@Override
		protected void onPostExecute(Object object) {
			if(object==null || object.toString().equals("null"))
			{
				if(REFRESH_TASK){
					mPullRefreshListView.onRefreshComplete();
				}
				super.onPostExecute(object);
				return;
			}
			if(!ServerCommunication.checkResult(getContext(), object))
			{
				MyToast.showToast(getContext(), (String)object);
				if(REFRESH_TASK){
					mPullRefreshListView.onRefreshComplete();
				}
				
				loadingDialogDismiss();
				super.onPostExecute(object);
				return;
			}
			
			AllNotificationsJsonObject jsonObject=(AllNotificationsJsonObject)object;
			userinfos=(ArrayList<UserInfo>) jsonObject.getUserInfos();
			notifications=(ArrayList<Notification>) jsonObject.getNotifications();
			comments=(ArrayList<String>) jsonObject.getComments();
			lastNum=jsonObject.getLastNum();
			if(userinfos==null||userinfos.size()==0)
			{
				MyToast.showToast(NotificationActivity.this, "没有更多了~");
				mPullRefreshListView.onRefreshComplete();
				return;
			}

			if(REFRESH_TASK)
				dataSource.clear();
			for(int i=0;i<notifications.size();i++)
			{
				NotificationActivityModel model=new NotificationActivityModel();
				model.setReplyManId(notifications.get(i).getUserId());
				if(comments.size()>i)
					model.setReplyContent(comments.get(i));
				else
					model.setReplyContent("");
				model.setMessageId(notifications.get(i).getNotificationId());
				model.setReplySex(userinfos.get(i).getSex());
				model.setReplyName(userinfos.get(i).getNickName());
				model.setTYPE(notifications.get(i).getNoticeFlag());
				dataSource.add(model);
			}
			adapter.notifyDataSetChanged();
			if(REFRESH_TASK){
				mPullRefreshListView.onRefreshComplete();
			}
			super.onPostExecute(result);
		}
	}
}
