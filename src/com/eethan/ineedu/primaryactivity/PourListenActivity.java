package com.eethan.ineedu.primaryactivity;


import java.util.ArrayList;

import com.eethan.ineedu.adapter.PourListenActivityAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Pourlisten;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.RefreshPourlistenJsonObject;
import com.eethan.ineedu.model.PourListenActivityModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataCache;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

public class PourListenActivity extends BaseActivity{

	Handler dataHandler;
	public static final int REFRESH_OVER=1;
	public static final int GET_MORE=2;
	public static final int FAILED=3;
	
	public int lastNum=0;
	public static boolean REFRESH_TASK=true;
	public static boolean GET_MORE_TASK=false;

	public static int GET_MORE_TOAST_TIME_LEFT=0;
	
	//加载互联网图片用
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	ArrayList<Pourlisten> plsFromServer=new ArrayList<Pourlisten>();
//	ArrayList<PourListenActivityModel> pls=new ArrayList<PourListenActivityModel>();
	PourListenActivityAdapter pla;
	
	ImageButton backButton;
	ImageButton issueButton;
	private PullToRefreshListView mPullRefreshListView;
	
	public MyTimer refreshTimer=new MyTimer();
	
	public static boolean isRefresh=false;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isRefresh)
		{
			new GetDataTask().execute();
			isRefresh=false;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("PourlistenActivity");
		loadingDialogShow();
//		NetCondition.check(this);
		
		Init();
		
		
		//设置ImageButton监听
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PourListenActivity.this.finish();
			}
		});
		issueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(PourListenActivity.this, PourListenBackgroundActivity.class);
				intent.putExtra("isCustom", false);
				startActivity(intent);
			}
		});
		
		//点击item监听
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        return; 
			    } 
				int realPosition=(int)id; 
				Intent intent=new Intent(PourListenActivity.this,PourListenDetailActivity.class);
				int pourlistenId=DataCache.pls.get(realPosition).getId();
				int ownerUserId=DataCache.pls.get(realPosition).getOwnerId();//pourlisten主人的id
				intent.putExtra("pourlistenId", pourlistenId);
				intent.putExtra("userId", ownerUserId);
				intent.putExtra("sex", DataCache.pls.get(realPosition).getSex());
				startActivity(intent);			
				}
		});
		//刷新监听
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(PourListenActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				REFRESH_TASK=true;
				GET_MORE_TASK=false;
				new GetDataTask().execute();
			}
		});

		// 拉至最底部监听
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//加载更多数据
				REFRESH_TASK=false;
				GET_MORE_TASK=true;
				new GetDataTask().execute();
			}
		});
		new GetDataTask().execute();
	}
	
	
	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		//后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
				if(REFRESH_TASK)
					MyTimer.Start();
				//后台获取数据
				SharedPreferences settings;
				settings = PourListenActivity.this.getSharedPreferences(Constant.INEEDUSPR,0);
				
				//userId
				Integer userId=settings.getInt(Constant.ID, 1);
				if(!REFRESH_TASK)
					userId=lastNum;
				
				String response=null,URL=URLConstant.REFRESH_POURLISTEN_URL;
				if(!REFRESH_TASK)
					URL=URLConstant.GET_MORE_POURLISTEN_URL;
				try {
					response = ServerCommunication.requestWithoutEncrypt(userId, URL);//发送请求，获得数据
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				RefreshPourlistenJsonObject refreshPR = null;
				try {
					refreshPR = JacksonUtil.json().fromJsonToObject(response,RefreshPourlistenJsonObject.class);
					return refreshPR;
				} catch (Exception e) {
					refreshPR = null;
				}
				if(REFRESH_TASK)
					MyTimer.RefreshDelay();
				return refreshPR;
			}
			

		@Override
		protected void onPostExecute(Object result) {
			if(result==null){
				MyToast.showToast(PourListenActivity.this, "加载数据失败");
				mPullRefreshListView.onRefreshComplete();
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			if(!ServerCommunication.checkResult(PourListenActivity.this, result))
			{

				MyToast.showToast(PourListenActivity.this, (String)result);
				mPullRefreshListView.onRefreshComplete();
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			RefreshPourlistenJsonObject object=(RefreshPourlistenJsonObject)result;
			//刷新后执行的操作
			if(REFRESH_TASK)
			{
				plsFromServer=(ArrayList<Pourlisten>)object.getPourlistens();
				if(plsFromServer.size()==0)
				{
					MyToast.showToast(PourListenActivity.this, "没有信息~", false);
					loadingDialogDismiss();
					mPullRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				}
				
				lastNum=plsFromServer.get(plsFromServer.size()-1).getId();
				
			}
			else if(GET_MORE_TASK){
				RefreshPourlistenJsonObject getMoreData=(RefreshPourlistenJsonObject)result;
				
				ArrayList<Pourlisten> tmpPls=(ArrayList<Pourlisten>)getMoreData.getPourlistens();
				
				//清除上次获取的服务器数据，并重新装入
				plsFromServer.clear();
				plsFromServer=tmpPls;
				
				
				if(plsFromServer.size()!=0)
					lastNum=plsFromServer.get(plsFromServer.size()-1).getId();
				
			}
			//数据处理
			if(plsFromServer.size()==0)
			{
				if(REFRESH_TASK)
					MyToast.showToast(PourListenActivity.this, "没有信息~", false);
				else
					MyToast.showToast(PourListenActivity.this, "没有更多了~", false);
				mPullRefreshListView.onRefreshComplete();
				loadingDialogDismiss();
				super.onPostExecute(result);
			}
			if(REFRESH_TASK)
				DataCache.pls.clear();
			for(int i=0;i<plsFromServer.size();i++)
			{
				PourListenActivityModel p=new PourListenActivityModel();
				p.setId(plsFromServer.get(i).getId());
				p.setContent(plsFromServer.get(i).getContent());
				p.setImageUrl(plsFromServer.get(i).getImageUrl());
				p.setNumOfComment(plsFromServer.get(i).getCommentNum());
				p.setOwnerId(plsFromServer.get(i).getUserId());
				p.setLat(plsFromServer.get(i).getLng());
				p.setLng(plsFromServer.get(i).getLat());
				p.setTime(plsFromServer.get(i).getTime());
				p.setSex(plsFromServer.get(i).getSex());
				DataCache.pls.add(p);
			}
			pla.notifyDataSetChanged();
			
			
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			loadingDialogDismiss();
			super.onPostExecute(result);
		}
}
//	//倒序获得
//	private ArrayList<PourListenActivityModel> getSomePourListenFromLast(int num){
//		ArrayList<PourListenActivityModel> PourListenList = new ArrayList<PourListenActivityModel>();
//		//创建实体，在这里从数据库取数据
//		PourListenList=DatabaseToModel.databaseToModel.getPourListenActivityDataFromLast(num);
//		return PourListenList;
//	}
	public void Init()
	{
		setContentView(R.layout.pourlisten_page);
		backButton=(ImageButton)findViewById(R.id.pour_listen_back);
		issueButton=(ImageButton)findViewById(R.id.pour_listen_issue);
		//设置Adapter
		mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pour_listen_listview);
		pla = new PourListenActivityAdapter(PourListenActivity.this,DataCache.pls,imageLoader);
		mPullRefreshListView.setAdapter(pla);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
//		try {
//			DbUtil.getDbUtils(getContext()).deleteAll(PourListenActivityModel.class);
//			DbUtil.getDbUtils(getContext()).saveAll(pls);
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	

}