package com.eethan.ineedu.fragment;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.NearFragmentAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.NearUserJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.NearFragmentModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataCache;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class NearFragment extends Fragment{
	public static final int REFRESH_OVER=1;
	public static final int GET_MORE=2;
	public static final int FAILED=3;
	
	
	public static boolean REFRESH_TASK=true;
	public static int lastNum=0;
	
	private static Fragment fragment;
	public static Fragment GetFragment()
	{
		return fragment;
	}
	private PullToRefreshListView mPullRefreshListView;
	public NearFragmentAdapter nearAdapter;
	
	View nearFragmentView;
	private LoadingDialog loadingDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragment=NearFragment.this;
		loadingDialog=new LoadingDialog(getActivity());
		nearFragmentView = inflater.inflate(R.layout.nearby_page,null);
		new GetDataTask().execute();
		Init();
		MyPullRefreshListViewListener();
		return nearFragmentView;
	}
	public static boolean isRefresh=false;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isRefresh)
		{
			loadingDialog.show();
			REFRESH_TASK=true;
			new GetDataTask().execute();
			isRefresh=false;
		}
		MobclickAgent.onPageStart("NearFragment"); //统计页面
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("NearFragment"); 
	}
	private void MyPullRefreshListViewListener() {
				// 拉至最底部监听
				mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
					REFRESH_TASK=false;
					new GetDataTask().execute();
					return;
					}
				});
				
				
				//   item单击监听
				mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
							long arg3) {
						// TODO Auto-generated method stub

						String friendId = DataCache.nears.get(pos-1).getId();
						String friend_Name = DataCache.nears.get(pos-1).getRealName();
						SharedPreferences lightDB = getActivity().getSharedPreferences(Constant.INEEDUSPR,0);
						String userId = ""+lightDB.getInt(Constant.ID,-1);
						
						MyIntent.toChatActivity(getActivity().getApplicationContext(),
								userId, friendId, friend_Name, false);
					}
				});
				
				//刷新监听
				mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						REFRESH_TASK=true;
						new GetDataTask().execute();
					}
				});
	}


	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		
		@Override
		protected Object doInBackground(Void... params) {
			if(Constant.OFFLINE_MODE)
			{
				mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("无网络连接");
				return null;
			}
				if(REFRESH_TASK)
					MyTimer.Start();
				//后台获取数据
				JsonObject jsonObject=new JsonObject();
				
				double lng=LocateManager.getInstance().getLontitude();
				double lat=LocateManager.getInstance().getLatitude();
				int id = new SPHelper(getActivity()).GetUserId();
				if(id==-1){
					return null;
				}
				
				jsonObject.setInt1(id);
				if(!REFRESH_TASK)//Get More
					jsonObject.setInt2(lastNum);
				jsonObject.setDouble1(lng);
				jsonObject.setDouble2(lat);
				String tmpsex = MainFragment.SEX;
				String mysex = getActivity().getSharedPreferences(Constant.INEEDUSPR,0).getString(Constant.SEX,Constant.SEX_ALL);
				if(mysex!=null && mysex.equals("男")){
					tmpsex = "女";
				}else if (mysex!=null && mysex.equals("女")){
					tmpsex = "男";
				}else {
					tmpsex = MainFragment.SEX;
				}
				jsonObject.setString1(tmpsex);			
				jsonObject.setString2(MainFragment.DISTANCE+"");

				String response=null,URL=URLConstant.REFRESH_NEAR_URL;
				if(!REFRESH_TASK)//刷新
					URL=URLConstant.GET_MORE_NEAR_USER_URL;
				try {
					response = ServerCommunication.requestWithoutEncrypt(jsonObject, URL);//发送请求，获得数据
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				NearUserJsonObject refreshNear = null;
				try {
					refreshNear = JacksonUtil.json().fromJsonToObject(response,NearUserJsonObject.class);
				} catch (Exception e) {
					refreshNear = null;
				}
				
				
				if(REFRESH_TASK)
					MyTimer.RefreshDelay();
				
				return refreshNear;
			
		}

		
		@Override
		protected void onPostExecute(Object result) {
			if(Constant.OFFLINE_MODE)
				{
					mPullRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				}
			if(result==null){
				return;
			}
			if(!ServerCommunication.checkResult(getActivity(), result))
			{
				MyToast.showToast(getActivity(), (String)result);
				loadingDialog.dismiss();
				mPullRefreshListView.onRefreshComplete();
				return;
				
			}
			if(((NearUserJsonObject)result).getUserInfos().size()==0)
				if(REFRESH_TASK)
					MyToast.showToast(getActivity(), "没有该类信息~");
				else
					MyToast.showToast(getActivity(), "没有更多了~");
			//doInBackground返回数据成功后进行数据操作处理
			NearUserJsonObject refreshNearJsonObject=(NearUserJsonObject)result;
			ArrayList<UserInfo> userinfos=(ArrayList<UserInfo>) refreshNearJsonObject.getUserInfos();
			ArrayList<UserLocation> userLocation=(ArrayList<UserLocation>) refreshNearJsonObject.getUserLocations();
			ArrayList<UserDetailInfo> userDetailInfos=(ArrayList<UserDetailInfo>) refreshNearJsonObject.getUserDetailInfos();
			lastNum=refreshNearJsonObject.getLastNum();//服务器返回Lastnum作为标记
			
			if(REFRESH_TASK)//refresh
				DataCache.nears.clear();
			for(int i=0;i<userinfos.size();i++)
			{
				NearFragmentModel near=new NearFragmentModel();
				double lng,lat;
				lat=LocateManager.getInstance().getLatitude();
				lng=LocateManager.getInstance().getLontitude();
				double distance=DataTraslator.GetDistance(lat, lng, userLocation.get(i).getLat(), userLocation.get(i).getLng());
				near.setAcademy(userDetailInfos.get(i).getAcademy());
				near.setDistant((int)distance);
				near.setName(userinfos.get(i).getNickName());
				near.setLoveNum(userinfos.get(i).getLoveNum());
				near.setId(userinfos.get(i).getUserId()+"");
				near.setRealName(userinfos.get(i).getRealName());
				near.setTime(userLocation.get(i).getTime());
				near.setSex(userinfos.get(i).getSex());
				DataCache.nears.add(near);
			}
			nearAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			
			loadingDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	public void Init()
	{
		
		//numBefore=getNear(INF).size();
		mPullRefreshListView = (PullToRefreshListView) nearFragmentView.findViewById(R.id.nearby_page_listview);
		//nears=getNear(displayNum);
		
		nearAdapter = new NearFragmentAdapter(getActivity().getApplicationContext(),DataCache.nears);
		mPullRefreshListView.setAdapter(nearAdapter);
		
	}
	
}
