package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eethan.ineedu.adapter.PopularityRankActivityAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.LovePopularityIncrease;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.fragment.MainFragment;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RankJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.SumPopularityRankFragmentModel;
import com.eethan.ineedu.model.WeekPopularityRankFragmentModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataTraslator;

public class PopularityRankActivity extends MyViewpagerActivity{

	
	//viewpager的适配器
	private PopularityRankActivityAdapter popularityRankActivityAdapter;
	
	//存放服务器获得的数据
	public static ArrayList<WeekPopularityRankFragmentModel> nContacts=new ArrayList<WeekPopularityRankFragmentModel>();
	public static ArrayList<SumPopularityRankFragmentModel> sumContacts=new ArrayList<SumPopularityRankFragmentModel>();
	
	ImageButton back;
	private LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popularityrank);
		loadingDialog=new LoadingDialog(this);
		loadingDialog.show();
		
		titleliLinearLayout = (LinearLayout)findViewById(R.id.title_linearlayout);
		viewPager = (ViewPager) findViewById(R.id.popularityrank_viewpager);
		new GetDataTask().execute();
		
		back=(ImageButton)findViewById(R.id.popularity_page_back_button);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		//后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			//后台获取数据
			JsonObject jsonObject=new JsonObject();
			
			double lng=LocateManager.getInstance().getLontitude();
			double lat=LocateManager.getInstance().getLatitude();
			String range=MainFragment.DISTANCE+"";
			
			jsonObject.setDouble1(lng);
			jsonObject.setDouble2(lat);  
			jsonObject.setString1(range);
			
			String response;
			RankJsonObject refreshLoveRank = null;
			try {
				response = ServerCommunication.requestWithoutEncrypt(jsonObject, URLConstant.REFRESH_POPULARITY_RANK_URL);
				refreshLoveRank = JacksonUtil.json().fromJsonToObject(response,RankJsonObject.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return refreshLoveRank;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(result==null || result.equals("")){
				MyToast.showToast(PopularityRankActivity.this,"加载失败");
				loadingDialog.dismiss();
				return;
			}
			if(!ServerCommunication.checkResult(PopularityRankActivity.this, result))//各种网络异常的处理部分
			{
				MyToast.showToast(PopularityRankActivity.this, (String)result);
				loadingDialog.dismiss();
				return;
			}

			RankJsonObject refreshPopulartityRankJsonObject=(RankJsonObject)result;
			ArrayList<UserInfo> icreaseUserInfos=(ArrayList<UserInfo>) refreshPopulartityRankJsonObject.getIcreaseUserInfos();
			ArrayList<UserLocation> icreaseuserLocations=(ArrayList<UserLocation>) refreshPopulartityRankJsonObject.getIcreaseuserLocations();
			ArrayList<LovePopularityIncrease> lovePopularityIncreases=(ArrayList<LovePopularityIncrease>) refreshPopulartityRankJsonObject.getLovePopularityIncreases();
			
			ArrayList<UserInfo> sumUserInfos=(ArrayList<UserInfo>) refreshPopulartityRankJsonObject.getUserInfos();
			ArrayList<UserLocation> sumUserLocations=(ArrayList<UserLocation>) refreshPopulartityRankJsonObject.getUserLocations();
			/*
			setIcreaseUserInfos(icreaseUserInfos);
			setIcreaseUserLocations(icreaseuserLocations);
			setLovePopularityIncreases(lovePopularityIncreases);
			
			setSumUserInfos(sumUserInfos);
			setSumUserLocations(sumUserLocations);
*/
			nContacts.clear();
			for(int i=0;i<icreaseUserInfos.size();i++){
				WeekPopularityRankFragmentModel nContact=new WeekPopularityRankFragmentModel();
				int userid = icreaseUserInfos.get(i).getUserId();
				String realname=icreaseUserInfos.get(i).getRealName();
				Integer popularityNum=icreaseUserInfos.get(i).getPopularityNum();
				Integer popularity_addnum=lovePopularityIncreases.get(i).getIncreasePopularityNum();
				nContact.setId(userid);
				nContact.setName(realname);
				nContact.setPopularityNum(popularityNum);
				nContact.setPopularity_addnum(popularity_addnum);
				nContact.setSex(icreaseUserInfos.get(i).getSex());
				double lat,lng;
				
				//好友的经纬度
				lat=icreaseuserLocations.get(i).getLat();
				lng=icreaseuserLocations.get(i).getLng();
				//距离
				int s=DataTraslator.GetDistanceToMe(PopularityRankActivity.this, lat, lng);
				nContact.setDistant(s);
				
				nContacts.add(nContact);
			}

			sumContacts.clear();
			for(int i=0;i<sumUserInfos.size();i++){
				SumPopularityRankFragmentModel sumContact=new SumPopularityRankFragmentModel();
				int userid = sumUserInfos.get(i).getUserId();
				String realname=sumUserInfos.get(i).getRealName();
				Integer popularityNum=sumUserInfos.get(i).getPopularityNum();
				sumContact.setId(userid);
				sumContact.setRankNum(i+1);
				sumContact.setName(realname);
				sumContact.setPopularityNum(popularityNum);
				sumContact.setSex(sumUserInfos.get(i).getSex());
				double lat_a,lat_b,lng_a,lng_b;
				//好友的经纬度
				lat_b=sumUserLocations.get(i).getLat();
				lng_b=sumUserLocations.get(i).getLng();
				//距离
				int s=DataTraslator.GetDistanceToMe(PopularityRankActivity.this,lat_b,lng_b);
				sumContact.setDistant(s);
				
				sumContacts.add(sumContact);
			}
			
			viewPager.setOffscreenPageLimit(2);/* 预加载页面 */
			popularityRankActivityAdapter = new PopularityRankActivityAdapter(getSupportFragmentManager());
			viewPager.setAdapter(popularityRankActivityAdapter);
			viewPager.setCurrentItem(0);
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
			//初始化页卡标题
			firstTextView = (TextView) findViewById(R.id.dayrank_text);
			secondTextView = (TextView) findViewById(R.id.sumrank_text);

			firstTextView.setOnClickListener(new MyOnClickListener(0));
			secondTextView.setOnClickListener(new MyOnClickListener(1));
			
			loadingDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if(popularityRankActivityAdapter!=null){
			popularityRankActivityAdapter.notifyDataSetChanged();
		}
		super.onStart();
	}
	

}
