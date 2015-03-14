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

import com.eethan.ineedu.adapter.LoveRankActivityAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.LovePopularityIncrease;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.fragment.MainFragment;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RankJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.DayLoveRankFragmentModel;
import com.eethan.ineedu.model.SumLoveRankFragmentModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataTraslator;

public class LoveRankActivity extends MyViewpagerActivity{

	///viewpager的适配器
	private LoveRankActivityAdapter loveRankActivityAdapter;
	//存放服务器获得的数据
	public static ArrayList<DayLoveRankFragmentModel> nContacts=new ArrayList<DayLoveRankFragmentModel>();
	public static ArrayList<SumLoveRankFragmentModel> sumContacts=new ArrayList<SumLoveRankFragmentModel>();
	
	private ImageButton back;
	private LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loverank);
		loadingDialog=new LoadingDialog(this);
		loadingDialog.show();
		
		titleliLinearLayout = (LinearLayout)findViewById(R.id.title_linearlayout);
		viewPager = (ViewPager) findViewById(R.id.loverank_viewpager);
		new GetDataTask().execute();
		
		
		back=(ImageButton) findViewById(R.id.love_rank_page_back_button);
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
				response = ServerCommunication.requestWithoutEncrypt(jsonObject, URLConstant.REFRESH_LOVE_RANK_URL);
				refreshLoveRank = JacksonUtil.json().fromJsonToObject(response,RankJsonObject.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			//Log.d("Response",response+"");
			
			return refreshLoveRank;
		}

		@Override
		protected void onPostExecute(Object result) {
			if(result==null){
				MyToast.showToast(LoveRankActivity.this,"加载失败");
				loadingDialog.dismiss();
				return;
			}
			if(!ServerCommunication.checkResult(LoveRankActivity.this, result))//各种网络异常的处理部分
			{
				MyToast.showToast(LoveRankActivity.this, (String)result);
				loadingDialog.dismiss();
				return;
			}
			RankJsonObject refreshLoveRankJsonObject=(RankJsonObject)result;
			ArrayList<UserInfo> icreaseUserInfos=(ArrayList<UserInfo>) refreshLoveRankJsonObject.getIcreaseUserInfos();
			ArrayList<UserLocation> icreaseuserLocations=(ArrayList<UserLocation>) refreshLoveRankJsonObject.getIcreaseuserLocations();
			ArrayList<LovePopularityIncrease> lovePopularityIncreases=(ArrayList<LovePopularityIncrease>) refreshLoveRankJsonObject.getLovePopularityIncreases();
			
			ArrayList<UserInfo> sumUserInfos=(ArrayList<UserInfo>) refreshLoveRankJsonObject.getUserInfos();
			ArrayList<UserLocation> sumUserLocations=(ArrayList<UserLocation>) refreshLoveRankJsonObject.getUserLocations();
			/*
			setIcreaseUserInfos(icreaseUserInfos);
			setIcreaseUserLocations(icreaseuserLocations);
			setLovePopularityIncreases(lovePopularityIncreases);
			
			setSumUserInfos(sumUserInfos);
			setSumUserLocations(sumUserLocations);
*/
			nContacts.clear();
			for(int i=0;i<icreaseUserInfos.size();i++){
				DayLoveRankFragmentModel nContact=new DayLoveRankFragmentModel();
				int userid = icreaseUserInfos.get(i).getUserId();
				String realname=icreaseUserInfos.get(i).getRealName();
				Integer loveNum=icreaseUserInfos.get(i).getLoveNum();
				Integer love_addnum=lovePopularityIncreases.get(i).getIncreaseLoveNum();
				nContact.setId(userid);
				nContact.setName(realname);
				nContact.setLoveNum(loveNum);
				nContact.setLove_addnum(love_addnum);
				nContact.setSex(icreaseUserInfos.get(i).getSex());
				double lat,lng;
				//好友的经纬度
				lat=icreaseuserLocations.get(i).getLat();
				lng=icreaseuserLocations.get(i).getLng();
				//距离
				int s=DataTraslator.GetDistanceToMe(LoveRankActivity.this, lat, lng);
				nContact.setDistant(s);
				
				nContacts.add(nContact);
			}

			sumContacts.clear();
			for(int i=0;i<sumUserInfos.size();i++){
				SumLoveRankFragmentModel sumContact=new SumLoveRankFragmentModel();
				int userid = sumUserInfos.get(i).getUserId();
				String realname=sumUserInfos.get(i).getRealName();
				Integer loveNum=sumUserInfos.get(i).getLoveNum();
				sumContact.setId(userid);
				sumContact.setRankNum(i+1);
				sumContact.setName(realname);
				sumContact.setLoveNum(loveNum);
				sumContact.setSex(sumUserInfos.get(i).getSex());
				double lat_b,lng_b;
				//好友的经纬度
				lat_b=sumUserLocations.get(i).getLat();
				lng_b=sumUserLocations.get(i).getLng();
				//距离
				int s=DataTraslator.GetDistanceToMe(LoveRankActivity.this,lat_b,lng_b);
				sumContact.setDistant(s);
				
				sumContacts.add(sumContact);
			}
			
			viewPager.setOffscreenPageLimit(2);/* 预加载页面 */
			loveRankActivityAdapter = new LoveRankActivityAdapter(getSupportFragmentManager());
			viewPager.setAdapter(loveRankActivityAdapter);
			viewPager.setCurrentItem(0);
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
			//初始化页卡标题
			firstTextView = (TextView) findViewById(R.id.weekrank_text);
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
		if(loveRankActivityAdapter!=null){
			loveRankActivityAdapter.notifyDataSetChanged();
		}

		super.onStart();
	}

}
